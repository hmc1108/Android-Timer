package com.hcmp.timer;

/**
 * Created by Admin on 5/15/2016.
 */
public class Task {
    String m_name;
    int m_time;
    int m_id;
    String m_proname;


    public Task(String name, int time, int id, String proname){
        m_name = name;
        m_time = time;
        m_proname = proname;
        m_id = id;
    }

    public void updateTime(int time){
        m_time = time;
    }
}
