package com.hcmp.timer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Admin on 5/14/2016.
 */
public class UserLocalStore {
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("name", user.m_name);
        spEditor.putString("phone",user.m_phone);
        spEditor.putString("company",user.m_company);
        spEditor.putString("email", user.m_email);
        spEditor.putString("password", user.m_password);
        spEditor.commit();
    }

    public User getLoggedInUser(){
        String name = userLocalDatabase.getString("name","");
        String phone = userLocalDatabase.getString("phone","");
        String company = userLocalDatabase.getString("company","");
        String email = userLocalDatabase.getString("email","");
        String password = userLocalDatabase.getString("password","");

        User storedUser = new User(name, password, email,phone,company);
        return storedUser;
    }

    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean getUserLoggedIn(){
        if (userLocalDatabase.getBoolean("loggedIn",false) == true){
            return true;
        }
        else{
            return false;
        }

    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public void storeITasks(User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        //ArrayList<Task> ITasks = new ArrayList<Task>();
        //ITasks.addAll(user.tasks);
        //try {
           // spEditor.putString("tasks", ObjectSerializer.serialize(ITasks));
        //} catch (Exception e) {
            //e.printStackTrace();
        //}
        Gson gson = new Gson();
        String json = gson.toJson(user.tasks);
        spEditor.putString("tasks",json);
        spEditor.commit();
    }

    public ArrayList getITasks(){
        ArrayList ITasks = new ArrayList();
        Gson gson = new Gson();
        String json = userLocalDatabase.getString("tasks","");
        Type type = new TypeToken<ArrayList<Task>>(){}.getType();
        ITasks = gson.fromJson(json,type);
        return ITasks;
    }
}
