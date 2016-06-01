package com.hcmp.timer;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 5/11/2016.
 */
public class UserFunctions {
    private JSONParser jsonParser;
    private static String loginURL = "http://hcmp-api.esy.es/";
    private static String registerURL = "http://hcmp-api.esy.es/";
    private static String resetURL="http://hcmp-api.esy.es/";

    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String sync_tag = "sync";
    private static String getPro_tag = "getpro";
    private static String reset_tag="resetPassword";
    private static String up_tag="upLoad";


    public UserFunctions(){
        jsonParser = new JSONParser();
    }

    public JSONObject loginUser(String email, String password){
        // Building Parameters
        Map<String,String> params = new HashMap();
        params.put("tag",login_tag);
        params.put("email",email);
        params.put("password",password);

        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }
    public JSONObject registerUser(String email, String password,String fullname,String company,String phone){
        // Building Parameters
        Map<String,String> params = new HashMap();
        params.put("tag",register_tag);
        params.put("email",email);
        params.put("password",password);
        params.put("fullname",fullname);
        params.put("company",company);
        params.put("phone",phone);

        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public JSONObject syncAccount(User user){
        // Building Parameters
        Map<String,String> params = new HashMap();
        params.put("tag",sync_tag);
        params.put("email",user.m_email);
        params.put("password",user.m_password);
        params.put("fullname",user.m_name);
        params.put("company",user.m_company);
        params.put("phone",user.m_phone);

        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public JSONObject getProjects(User user) {
        // Building Parameters
        Map<String,String> params = new HashMap();
        params.put("tag",getPro_tag);
        params.put("email",user.m_email);

        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public JSONObject sendResetPasswordEmail(String email){
        Map<String,String> params= new HashMap();
        params.put("tag",reset_tag);
        params.put("email",email);

        JSONObject json = jsonParser.getJSONFromUrl(resetURL,params);
        return json;
    }
    public JSONObject upData(ArrayList<Task> tasks){
        Map<String,String> params= new HashMap();
        params.put("tag",up_tag);
        int size = tasks.size();
        JSONObject json=null;
        for(int i=0; i<size; i++) {
            params.put("id", String.valueOf(tasks.get(i).m_id));
            params.put("time", String.valueOf(tasks.get(i).m_time));
            json = jsonParser.getJSONFromUrl(resetURL, params);
        }
        return json;
    }
}
