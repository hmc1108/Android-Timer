package com.hcmp.timer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Account extends AppCompatActivity {

    private static String KEY_SUCCESS = "success";
    TextView hMail;
    EditText hName;
    EditText hPhone;
    EditText hCompany;
    Button hChangePass;
    Button hLogout;
    Button hSync;
    UserLocalStore userLocalStore;
    User user;
    boolean changePass = true;

    @Override
    public void onBackPressed() {
        String name = hName.getText().toString();
        String phone = hPhone.getText().toString();
        String company =hCompany.getText().toString();

        if(name.equals(user.m_name) && phone.equals(user.m_phone) && company.equals(user.m_company)&& changePass){
            Intent main=new Intent(getApplicationContext(),MainActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
            finish();
        }
        else showDialog();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        hMail = (TextView) findViewById(R.id.evMail);
        hName = (EditText) findViewById(R.id.evName);
        hPhone = (EditText) findViewById(R.id.evPhone);
        hCompany = (EditText) findViewById(R.id.evCompany);
        hChangePass = (Button) findViewById(R.id.bChangePass);
        hSync = (Button) findViewById(R.id.bSync);
        hLogout = (Button) findViewById(R.id.bLogout);
        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        hMail.setText(user.m_email);
        hName.setText(user.m_name);
        hPhone.setText(user.m_phone);
        hCompany.setText(user.m_company);
        hChangePass.setOnClickListener(mChangePass);
        hLogout.setOnClickListener(mLogout);
        hSync.setOnClickListener(mSync);
    }

    View.OnClickListener mChangePass = new View.OnClickListener() {
        public void onClick(View v) {
            changePassDialog();
        }
    };

    View.OnClickListener mSync = new View.OnClickListener() {
        public void onClick(View v) {
            NetAsyncAccount();
        }
    };

    View.OnClickListener mLogout = new View.OnClickListener() {
        public void onClick(View v) {
            userLocalStore.setUserLoggedIn(false);
            userLocalStore.clearUserData();
            //Sync data

            Intent login=new Intent(getApplicationContext(),Login.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            finish();

        }
    };

    public void showDialog(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Lưu thông tin hiện tại?");
        alertDialogBuilder
                .setMessage("Bấm có để lưu!")
                .setCancelable(false)
                .setPositiveButton("Có",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                user.m_name = hName.getText().toString();
                                user.m_phone = hPhone.getText().toString();
                                user.m_company = hCompany.getText().toString();
                                userLocalStore.storeUserData(user);

                                NetAsync();

                                Intent main=new Intent(getApplicationContext(),MainActivity.class);
                                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(main);
                                finish();
                            }
                        })

                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        Intent main=new Intent(getApplicationContext(),MainActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(main);
                        finish();
                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void changePassDialog(){
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Account.this);
        View promptView = layoutInflater.inflate(R.layout.changepass_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Account.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("THAY ĐỔI MẬT KHẨU");

        final EditText oldPass = (EditText) promptView.findViewById(R.id.etOldPass);
        final EditText newPass = (EditText) promptView.findViewById(R.id.etNewPass);
        final EditText confPass = (EditText) promptView.findViewById(R.id.etConfPass);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (oldPass.getText().toString().equals("")||newPass.getText().toString().equals("")
                                ||confPass.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(),"Bạn phải điền đầy đủ các ô",Toast.LENGTH_LONG).show();
                            changePassDialog();
                        }

                        else if (!oldPass.getText().toString().equals(user.m_password)){
                            Toast.makeText(getApplicationContext(),"Mật khẩu không đúng",Toast.LENGTH_LONG).show();
                            changePassDialog();
                        }

                        else if(!newPass.getText().toString().equals(confPass.getText().toString())){
                            Toast.makeText(getApplicationContext(),"Xác nhận mật khẩu không trùng khớp",Toast.LENGTH_LONG).show();
                            changePassDialog();
                        }

                        else if(newPass.length() < 8){
                            Toast.makeText(getApplicationContext(),"Mật khẩu phải dài từ 8-32 kí tự",Toast.LENGTH_LONG).show();
                            changePassDialog();
                        }
                        else {
                            user.m_password = newPass.getText().toString();
                            changePass = false;
                        }
                    }
                })
                .setNegativeButton("Bỏ qua",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void NetAsync(){
        new NetChecker().execute();
    }
    private class NetChecker extends AsyncTask<String,Void,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Boolean th){
            if(th == true){
                //Execute Login
                new ProcessSyncAccount().execute();
            }
            else{
                //Văng ra diaglog kêu mất kết nối hoặc làm gì đó...
                Log.e("LOI","MAT KET NOI INTERNET");
                Toast.makeText(getApplicationContext(),"Không thể kết nối internet để đồng bộ",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(String...params) {

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("https://www.google.com/");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
    private class ProcessSyncAccount extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            UserFunctions userfunction=new UserFunctions();
            JSONObject json = userfunction.syncAccount(user);
            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try{
                if(json.getString(KEY_SUCCESS).equals("1")){
                    Toast.makeText(getApplicationContext(),"Đồng bộ thành công",Toast.LENGTH_LONG).show();
                    // Đăng kí thành công, chuyển về activity login
                    // ANH EM's CODE HERE:
                }else{
                    //Đồng bộ thất bại
                    Toast.makeText(getApplicationContext(),"Đồng bộ thất bại",Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }
    //Đồng bộ tài khoản
    public void NetAsyncAccount(){
        new NetCheckerAccount().execute();
    }
    private class NetCheckerAccount extends AsyncTask<String,Void,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Boolean th){
            if(th == true){
                //Execute Login
                new upTaskProcess().execute();
            }
            else{
                //Văng ra diaglog kêu mất kết nối hoặc làm gì đó...
                Log.e("LOI","MAT KET NOI INTERNET");
                Toast.makeText(getApplicationContext(),"Không thể kết nối internet để lấy dữ liệu",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(String...params) {

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("https://www.google.com/");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
    private class upTaskProcess extends AsyncTask<String,Void,JSONObject> {

        ProgressDialog progressDialog;

        @Override
        protected JSONObject doInBackground(String... params) {
            UserFunctions userfunction=new UserFunctions();
            JSONObject json = userfunction.upData(userLocalStore.getITasks());
            return json;
        }

        public upTaskProcess(){
            progressDialog = new ProgressDialog(Account.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Đưa dữ liệu lên server");
            progressDialog.setMessage("Đang xử lý...");
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            progressDialog.dismiss();
            try{
                if(json.getString("Success").equals("1")){
                    Toast.makeText(getApplicationContext(),"Đưa dữ liệu thành công",Toast.LENGTH_LONG).show();
                    // Có task

                }else{
                    //Không có task
                    Toast.makeText(getApplicationContext(),"Đưa dữ liệu thất bại",Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }
}
