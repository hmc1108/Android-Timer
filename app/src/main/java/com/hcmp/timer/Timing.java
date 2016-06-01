package com.hcmp.timer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.hcmp.timer.R.drawable.pause0;
import static com.hcmp.timer.R.drawable.play;
import static com.hcmp.timer.R.drawable.play0;


public class Timing extends Activity {
    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    TextView hTextView;
    ImageButton hButton, hAnalyse;
    Button hTasks;
    boolean check = true;

    Spinner hSpinner;
    String arrTasks[];
    int ITasks[];
    int pos =0;
    User user;
    UserLocalStore userLocalStore;

    private int nCounter = 0;

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Thoát ứng dụng?");
        alertDialogBuilder
                .setMessage("Bấm Thoát để thoát!")
                .setCancelable(false)
                .setPositiveButton("Thoát",
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

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new User();
        userLocalStore = new UserLocalStore(Timing.this);
        getTime();
        setContentView(R.layout.activity_timing);

        hSpinner = (Spinner) findViewById(R.id.sSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arrTasks);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        hSpinner.setAdapter(adapter);
        hSpinner.setOnItemSelectedListener(new ChooseTask());

        hTextView = (TextView) findViewById(R.id.tvTime);

        hButton = (ImageButton) findViewById(R.id.bPlay);
        hButton.setOnClickListener(mButtonStartListener);

        hAnalyse = (ImageButton) findViewById(R.id.bAnalyse);
        hAnalyse.setOnClickListener(mButtonAnalyse);

        hTasks = (Button) findViewById(R.id.bTasks);
        hTasks.setOnClickListener(mButtonTask);

        hTextView.setTextColor(Color.argb(255,44,62,80));
        hTextView.setText(convertToTime());
    } // end onCreate

    View.OnClickListener mButtonStartListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (ITasks == null){
                Toast.makeText(getApplicationContext(),
                        "Chưa có task nào được tạo !!!", Toast.LENGTH_LONG).show();
                return;
            }
            if (check) {
                doTimerTask();
            } else {
                stopTask();
            }
        }
    };
    View.OnClickListener mButtonAnalyse = new View.OnClickListener() {
        public void onClick(View v) {
            showMessage();
        }
    };
    View.OnClickListener mButtonTask = new View.OnClickListener() {
        public void onClick(View v) {
            showMessage();
        }
    };
    public void doTimerTask() {
        hTextView.setTextColor(Color.argb(255,44,62,80));       //midnight blue
        hButton.setBackgroundResource(R.drawable.pause1);
        check = false;
        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        nCounter++;
                        // update TextView
                        hTextView.setText(convertToTime());

                        Log.d("TIMER", "TimerTask run");
                    }
                });
            }
        };

        // public void schedule (TimerTask task, long delay, long period)
        t.schedule(mTimerTask, 500, 1000);  //

    }

    public String convertToTime() {
        int hour = 0, minute = 0, sec = 0;
        String result = "";
        hour = nCounter / 3600;
        minute = (nCounter % 3600) / 60;
        sec = (nCounter % 3600) % 60;

        if (hour < 10 )
            result += "0";
        result += hour;
        result += ":";

        if (minute <10)
            result += "0";
        result += minute;
        result += ":";

        if (sec < 10)
            result += "0";
        result += sec;

        return result;
    }

    public void stopTask() {
        hTextView.setTextColor(Color.argb(255,192,57,43));      //pomegranate
        check = true;
        hButton.setBackgroundResource(R.drawable.play1);
        if (mTimerTask != null) {
            hTextView.setText(convertToTime());

            Log.d("TIMER", "timer canceled");
            mTimerTask.cancel();
        }

        showInputDialog();
    }

    public void ForceStop() {
        hTextView.setTextColor(Color.argb(255,192,57,43));      //pomegranate
        check = true;
        hButton.setBackgroundResource(R.drawable.play1);
        if (mTimerTask != null) {
            hTextView.setText(convertToTime());

            Log.d("TIMER", "timer canceled");
            mTimerTask.cancel();
        }
        putTime(pos-1,nCounter);
        ITasks[pos-1] = nCounter;
    }

    private class ChooseTask implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (ITasks != null) {
                if ((pos - 1) != position && check == false)
                    ForceStop();

                nCounter = ITasks[position];
                pos = position + 1;


                hTextView.setTextColor(Color.argb(255, 44, 62, 80));       //midnight blue
                hTextView.setText(convertToTime());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            nCounter = ITasks[0];
            pos = 1;
        }
    }
    private void showMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Timing.this);
        dialogBuilder.setMessage("Chức năng này đang được xây dựng!");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    //Lưu thời gian các task file share
    public void putTime(int pos, int nCounter){
        Task temp = user.tasks.get(pos);
        temp.m_time = nCounter;
        user.tasks.set(pos,temp);
        userLocalStore.storeITasks(user);
    }

    //Lấy thời gian trong file share
    public void getTime(){
        user.tasks = userLocalStore.getITasks();
        int size = 0;
        if(user.tasks != null ) {
            size = user.tasks.size();
            ITasks = new int[size];
            arrTasks = new String[size];
        }
        else{
            arrTasks = new String[1];
            arrTasks[0] = "Chưa có task nào được tạo...";
        }
        if (size >1 )
            for(int i =0; i<size; i++){
                ITasks[i] = user.tasks.get(i).m_time;
                arrTasks[i]=user.tasks.get(i).m_proname + " > " + user.tasks.get(i).m_name;
            }
    }

    private void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Timing.this);
        View promptView = layoutInflater.inflate(R.layout.fragment_edit_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Timing.this);
        alertDialogBuilder.setView(promptView);

        final TextView tvTask = (TextView) promptView.findViewById(R.id.tvTask);
        final NumberPicker editHour = (NumberPicker) promptView.findViewById(R.id.npHour);
        final NumberPicker editMinute = (NumberPicker) promptView.findViewById(R.id.npMinute);
        final NumberPicker editSec = (NumberPicker) promptView.findViewById(R.id.npSec);

        editHour.setMaxValue(300);
        editHour.setMinValue(0);
        editHour.setWrapSelectorWheel(true);

        editMinute.setMaxValue(59);
        editMinute.setMinValue(0);
        editMinute.setWrapSelectorWheel(true);

        editSec.setMaxValue(59);
        editSec.setMinValue(0);
        editSec.setWrapSelectorWheel(true);

        String text = "Kết thúc phiên làm việc của task ";
        int sum = ITasks[pos-1];
        text += arrTasks[pos-1];
        text +=" với thời gian:";
        tvTask.setText(text);

        final int curTime = nCounter - sum;
        int aHour = curTime/3600;
        int aMinute = (curTime%3600)/60;
        int aSec = (curTime%3600)%60;

        editHour.setValue(aHour);
        editMinute.setValue(aMinute);
        editSec.setValue(aSec);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int hour = editHour.getValue();
                        int minute = editMinute.getValue();
                        int sec = editSec.getValue();
                        int change = sec + minute * 60 + hour * 3600;
                        if (change > curTime){
                            Toast.makeText(getApplicationContext(),"Chỉ có thể giảm thời gian của task",Toast.LENGTH_LONG).show();
                            showInputDialog();
                        }
                        else{
                            nCounter += (change - curTime);
                            hTextView.setText(convertToTime());
                        }
                        putTime(pos-1,nCounter);
                        ITasks[pos-1]=nCounter;
                    }
                })
                .setNegativeButton("Tiếp tục",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                doTimerTask();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}

