package com.hcmp.timer;

import java.util.ArrayList;

/**
 * Created by Admin on 5/14/2016.
 */
public class User {
    String m_name, m_email, m_password, m_phone, m_company;
    ArrayList<Task> tasks;

    public User(String name, String password, String email, String phone, String company){
        this.m_name = name;
        this.m_password = password;
        this.m_email = email;
        this.m_phone = phone;
        this.m_company = company;
    }

    public User(String email, String password){
        this.m_name = "";
        this.m_company = this.m_phone = "";
        this.m_password = password;
        this.m_email = email;
    }
    public  User(){
        m_company=m_phone=m_email=m_password=m_name = "";
    }
}
