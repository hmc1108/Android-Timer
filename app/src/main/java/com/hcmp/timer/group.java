package com.hcmp.timer;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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

import java.util.Timer;
import java.util.TimerTask;


public class group extends Activity {
    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    TextView hTextView;
    ImageButton hButton, hAnalyse;
    Button hTasks;
    boolean check = true;

    Spinner hSpinner;
    String arr[]={
            "HCMP > PA1 > Khảo sát Toggl",
            "HCMP > PA2 > Khảo sát Timer",
            "ACM > VK Cup > ProbA",
            "ACM > VK Cup > ProbB",
    };
    int task1=0, task2=0, task3=0, task4=0, pos = 0;

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
        setContentView(R.layout.activity_group);

        task1=getTime(0);
        task2=getTime(1);
        task3=getTime(2);
        task4=getTime(3);

        hSpinner = (Spinner) findViewById(R.id.gsSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arr);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        hSpinner.setAdapter(adapter);
        hSpinner.setOnItemSelectedListener(new ChooseTask());

        hTextView = (TextView) findViewById(R.id.gtvTime);

        hButton = (ImageButton) findViewById(R.id.gbPlay);
        hButton.setOnClickListener(mButtonStartListener);

        hAnalyse = (ImageButton) findViewById(R.id.gbAnalyse);
        hAnalyse.setOnClickListener(mButtonAnalyse);

        hTasks = (Button) findViewById(R.id.gbTasks);
        hTasks.setOnClickListener(mButtonTask);

        hTextView.setTextColor(Color.argb(255,44,62,80));
        hTextView.setText(convertToTime());
    } // end onCreate

    View.OnClickListener mButtonStartListener = new View.OnClickListener() {
        public void onClick(View v) {
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
        switch (pos){
            case 1:
                task1 = nCounter;
                break;
            case 2:
                task2 = nCounter;
                break;
            case 3:
                task3 = nCounter;
                break;
            case 4:
                task4 = nCounter;
                break;
        }


    }
    private class ChooseTask implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if((pos - 1) !=position && check == false)
                ForceStop();
            switch (position){
                case 0:
                    nCounter = task1;
                    pos = 1;
                    break;
                case 1:
                    nCounter = task2;
                    pos = 2;
                    break;
                case 2:
                    nCounter = task3;
                    pos = 3;
                    break;
                case 3:
                    nCounter = task4;
                    pos = 4;
                    break;
            }
            hTextView.setTextColor(Color.argb(255,44,62,80));       //midnight blue
            hTextView.setText(convertToTime());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            nCounter = task1;
            pos = 1;
        }
    }
    private void showMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(group.this);
        dialogBuilder.setMessage("Chức năng này đang được xây dựng!");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    //Lưu thời gian vô file share
    public void putTime(int pos, int val){
        // File chia sẻ sử dụng trong nội bộ ứng dụng, hoặc các ứng dụng được chia sẻ cùng User.
        SharedPreferences sharedPreferences= getSharedPreferences("group", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(arr[pos],val);
        editor.apply();
    }

    //Lấy thời gian trong file share
    public int getTime(int pos){
        SharedPreferences sharedPreferences= getSharedPreferences("group", Context.MODE_PRIVATE);

        if(sharedPreferences!= null) {
            int res = sharedPreferences.getInt(arr[pos],0);
            return res;
        } else {
            return 0;
        }
    }

    private void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(group.this);
        View promptView = layoutInflater.inflate(R.layout.fragment_edit_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(group.this);
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
        int sum = 0;
        switch (pos){
            case 1:
                text += "HCMP > PA1 > Khảo sát Toggl ";
                sum =task1;
                break;

            case 2:
                text += "HCMP > PA2 > Khảo sát Timer ";
                sum = task2;
                break;

            case 3:
                text += "ACM > VK Cup > ProbA ";
                sum = task3;
                break;

            case 4:
                text += "ACM > VK Cup > ProbB ";
                sum = task4;
                break;
        }
        text +="với thời gian:";
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
                        switch (pos){
                            case 1:
                                task1 = nCounter;
                                break;
                            case 2:
                                task2 = nCounter;
                                break;
                            case 3:
                                task3 = nCounter;
                                break;
                            case 4:
                                task4 = nCounter;
                                break;
                        }
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

