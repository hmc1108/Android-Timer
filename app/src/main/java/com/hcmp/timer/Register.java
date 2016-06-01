package com.hcmp.timer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Register extends AppCompatActivity {
    private static String KEY_SUCCESS = "success";

    EditText inputEmail;
    EditText inputPassword;
    EditText confirmPassword;
    EditText inputFullname;
    EditText inputCompany;
    EditText inputPhone;
    Button signupbtn;

    @Override
    public void onBackPressed() {
        Intent login=new Intent(getApplicationContext(),Login.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail = (EditText)findViewById(R.id.etrEmail);
        inputPassword = (EditText)findViewById(R.id.etrPassword);
        confirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        inputFullname= (EditText)findViewById(R.id.etName);
        inputCompany= (EditText)findViewById(R.id.etCompany);
        inputPhone= (EditText)findViewById(R.id.etPhone);
        signupbtn = (Button)findViewById(R.id.bSignup);

        signupbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(checkEmailPassword())
                    NetAsync(v);
            }
        });
    }

    // Hàm kiểm tra các trường phải điền trong Register
    private boolean checkEmailPassword(){
        //Kiểm tra có trống không
        if (inputEmail.getText().toString().equals("")||inputPassword.getText().toString().equals("")
                || confirmPassword.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Hãy điền đủ email và mật khẩu",Toast.LENGTH_LONG).show();
            return false;
        }
        //Kiểm tra 2 lần nhập mật khẩu có giống ko
        if (!inputPassword.getText().toString().equals(confirmPassword.getText().toString())){
            Toast.makeText(getApplicationContext(),"Mật khẩu và xác nhận không trùng khớp",Toast.LENGTH_LONG).show();
            return false;
        }

        //Kiểm tra địa chỉ email hợp lệ
        if(!isValidEmailAddress(inputEmail.getText().toString())){
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

    //Kiểm tra tính hợp lệ của email
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

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
                new ProcessRegister(Register.this).execute();
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
    private class ProcessRegister extends AsyncTask<String,Void,JSONObject> {
        String email,password,fullname,phone,company;
        ProgressDialog progressDialog;

        public ProcessRegister(Context context){
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Đăng ký");
            progressDialog.setMessage("Đang xử lý...");
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            UserFunctions userfunction=new UserFunctions();
            JSONObject json = userfunction.registerUser(email,password,fullname,company,phone);
            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            email = ((EditText)findViewById(R.id.etrEmail)).getText().toString();
            password = ((EditText)findViewById(R.id.etrPassword)).getText().toString();
            fullname = ((EditText)findViewById(R.id.etName)).getText().toString();
            company = ((EditText)findViewById(R.id.etCompany)).getText().toString();
            phone = ((EditText)findViewById(R.id.etPhone)).getText().toString();

            progressDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            progressDialog.dismiss();
            try{
                if(json.getString(KEY_SUCCESS).equals("1")){
                    Toast.makeText(getApplicationContext(),"Đăng kí thành công",Toast.LENGTH_LONG).show();
                    // Đăng kí thành công, chuyển về activity login
                    // ANH EM's CODE HERE:
                    Intent login=new Intent(Register.this,Login.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    finish();
                }else{
                    //Đăng kí thất bại, tồn tại email
                    Toast.makeText(getApplicationContext(),"Email đã tồn tại",Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }
}
