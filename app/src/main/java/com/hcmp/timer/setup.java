package com.hcmp.timer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class setup extends Activity {

    ImageButton ibAccount;
    ImageButton ibReminder;
    ImageButton ibInfo;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ibAccount = (ImageButton) findViewById(R.id.ibAccount);
        ibReminder = (ImageButton) findViewById(R.id.ibReminder);
        ibInfo = (ImageButton) findViewById(R.id.ibInfo);

        ibAccount.setOnClickListener(accountButton);
        ibReminder.setOnClickListener(remindButton);
        ibInfo.setOnClickListener(infoButton);
    }

    View.OnClickListener accountButton = new View.OnClickListener(){
        public void onClick(View v){
            Intent account=new Intent(getApplicationContext(),Account.class);
            account.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(account);
            finish();
        }

    };

    View.OnClickListener remindButton = new View.OnClickListener(){
        public void onClick(View v){
            Intent reminder=new Intent(getApplicationContext(),Reminder.class);
            reminder.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(reminder);
        }

    };

    View.OnClickListener infoButton = new View.OnClickListener(){
        public void onClick(View v){
            Intent info=new Intent(getApplicationContext(),Info.class);
            info.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(info);
        }

    };
}
