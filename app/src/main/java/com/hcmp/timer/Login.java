package com.hcmp.timer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.hcmp.timer.Register;

public class Login extends AppCompatActivity {

    private static String KEY_SUCCESS = "success";
    UserLocalStore userLocalStore;

    Button loginBtn;
    TextView registerBtn;
    TextView resetPassBtn;
    EditText inputEmail;
    EditText inputPassword;
    TextView notiBox;

    User user;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Thoát ứng dụng?");
        alertDialogBuilder
                .setMessage("Bấm Đúng để thoát!")
                .setCancelable(false)
                .setPositiveButton("Đúng",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("Bỏ qua", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userLocalStore = new UserLocalStore(this);

        this.loginBtn=(Button)findViewById(R.id.bSignin);
        this.registerBtn=(TextView)findViewById(R.id.tRegister);
        this.resetPassBtn=(TextView)findViewById(R.id.requestEmail);
        this.inputEmail=(EditText)findViewById(R.id.etEmail);
        this.inputPassword=(EditText)findViewById(R.id.etPassword);
        this.notiBox=(TextView)findViewById(R.id.notifybox);

        this.loginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(checkEmailPassword())
                    NetAsync(v);
            }
        });
        this.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển qua intent đăng kí
                //ANH EM's code here:
                Intent myIntent = new Intent(v.getContext(), Register.class);
                startActivityForResult(myIntent, 0);
                finish();
            }
        });
        this.resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputEmail.getText().toString().equals(""))
                    Toast.makeText(Login.this, "Nhập email để gửi passreset.", Toast.LENGTH_SHORT).show();
                else
                    NetAsyncForgetPass(v);

            }
        });
    }
    private boolean checkEmailPassword(){
        //Kiểm tra có trống không
        if (inputEmail.getText().toString().equals("")||inputPassword.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Hãy điền đủ email và mật khẩu",Toast.LENGTH_LONG).show();
            return false;
        }

        //Kiểm tra địa chỉ email hợp lệ
        if(!Register.isValidEmailAddress(inputEmail.getText().toString())){
            Toast.makeText(getApplicationContext(),"Email không hợp lệ",Toast.LENGTH_LONG).show();
            return false;
        }
        //Kiểm tra độ dài password
        if(inputPassword.getText().toString().length()<8 || inputPassword.getText().toString().length()>32){
            Toast.makeText(getApplicationContext(),"Độ dài mật khẩu từ 8-32 kí tự",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // Xử lý đăng nhập
    public void NetAsync(View v){
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
                new ProcessLogin().execute();
            }
            else{
                //Văng ra diaglog kêu mất kết nối hoặc làm gì đó...
                Log.e("LOI","MAT KET NOI INTERNET");
                Toast.makeText(getApplicationContext(),"Không thể kết nối internet",Toast.LENGTH_LONG).show();
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
    private class ProcessLogin extends AsyncTask<String,Void,JSONObject>{
        String email;
        String password;
        ProgressDialog progressDialog;

        public ProcessLogin(){
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Đăng nhập");
            progressDialog.setMessage("Đang xử lý...");
        }

        @Override
        protected JSONObject doInBackground(String...args) {
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(email,password);
            return json;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            email=((EditText)findViewById(R.id.etEmail)).getText().toString();
            password=((EditText)findViewById(R.id.etPassword)).getText().toString();
            //thich thi them ti mau me diaglog vao :v
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            progressDialog.dismiss();
            try{
                if(json.getString(KEY_SUCCESS).equals("1")){
                    Toast.makeText(getApplicationContext(),"Đăng nhập thành công",Toast.LENGTH_LONG).show();
                    // Đăng nhập thành công, chuyển vào trong ứng dụng, kêu activity been trong
                    user = new User(json.getString("name"),password,email,json.getString("phone"),json.getString("company"));
                    logUserIn();
                }else{
                    //Thông báo sai mật khẩu
                    Toast.makeText(getApplicationContext(),"Sai tên đăng nhập hoặc mật khẩu",Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
    private void logUserIn() {
        userLocalStore.storeUserData(user);
        userLocalStore.setUserLoggedIn(true);

        NetAsyncITasks();
    }

    //Xử lý quên mật khẩu
    public void NetAsyncForgetPass(View v){
        new NetCheckerFogretPass().execute();
    }
    private class NetCheckerFogretPass extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean==true){
                new ProcessForgetPass().execute();
            }else{
                Log.e("LOI","MAT KET NOI INTERNET");
                Toast.makeText(getApplicationContext(),"Không thể kết nối internet",Toast.LENGTH_LONG).show();
            }
        }
    }
    private class ProcessForgetPass extends AsyncTask<String,Void,JSONObject>{
        String email;
        ProgressDialog progressDialog;

        public ProcessForgetPass(){
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Gửi email");
            progressDialog.setMessage("Đang xử lý...");
        }

        @Override
        protected JSONObject doInBackground(String...args) {
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.sendResetPasswordEmail(email);
            return json;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            email=((EditText)findViewById(R.id.etEmail)).getText().toString();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            progressDialog.dismiss();
            try{
                if(json.getString(KEY_SUCCESS).equals("1")){
                    Toast.makeText(getApplicationContext(),"Kiểm tra email của bạn",Toast.LENGTH_LONG).show();
                    // Đăng nhập thành công, chuyển vào trong ứng dụng, kêu activity been trong
                    // hoặc có thể quay lại màn hình login để người dùng nhập lại
                    // ANH EM's CODE HERE:
                }else{
                    //Thông báo sai mật khẩu
                    Toast.makeText(getApplicationContext(),"Lỗi hệ thống",Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    //Lấy thông tin các task
    public void NetAsyncITasks(){
        new NetCheckerITasks().execute();
    }
    private class NetCheckerITasks extends AsyncTask<String,Void,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Boolean th){
            if(th == true){
                //Execute Login
                new getProjectProcess().execute();
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
    private class getProjectProcess extends AsyncTask<String,Void,JSONObject> {

        ProgressDialog progressDialog;

        @Override
        protected JSONObject doInBackground(String... params) {
            UserFunctions userfunction=new UserFunctions();
            JSONObject json = userfunction.getProjects(user);
            return json;
        }

        public getProjectProcess(){
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Lấy thông tin projects");
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
                if(json != null){
                    Toast.makeText(getApplicationContext(),"Lấy thông tin thành công",Toast.LENGTH_LONG).show();
                    // Có task
                    user.tasks = new ArrayList<Task>();
                    for(int i=1; i<= json.length(); i++ ) {
                        int id = json.getJSONObject(String.valueOf(i)).getInt("id");
                        String name = json.getJSONObject(String.valueOf(i)).getString("name");
                        int time = json.getJSONObject(String.valueOf(i)).getInt("time");
                        String proname = json.getJSONObject(String.valueOf(i)).getString("proname");
                        user.tasks.add(new Task(name, time, id, proname));
                    }
                    userLocalStore.storeITasks(user);
                }else{
                    //Không có task
                    Toast.makeText(getApplicationContext(),"Tài khoản chưa có task",Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            Intent main=new Intent(getApplicationContext(),MainActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
            finish();
        }

    }
}
