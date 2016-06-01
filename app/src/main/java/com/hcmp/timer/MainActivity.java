package com.hcmp.timer;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = getTabHost();

        // Tab for Timing
        TabHost.TabSpec timingspec = tabHost.newTabSpec("Individual");
        timingspec.setIndicator("", getResources().getDrawable(R.drawable.individual_tabspec));
        Intent timingIntent = new Intent(this, Timing.class);
        timingspec.setContent(timingIntent);

        // Tab for Group
        TabHost.TabSpec groupspec = tabHost.newTabSpec("Group");
        // setting Title and Icon for the Tab
        groupspec.setIndicator("", getResources().getDrawable(R.drawable.group_tabspec));
        Intent groupIntent = new Intent(this, group.class);
        groupspec.setContent(groupIntent);

        // Tab for Setup
        TabHost.TabSpec setupspec = tabHost.newTabSpec("Setup");
        setupspec.setIndicator("", getResources().getDrawable(R.drawable.setting_tabspec));
        Intent setupIntent = new Intent(this, setup.class);
        setupspec.setContent(setupIntent);

        // Adding all TabSpec to TabHost
        tabHost.addTab(timingspec); // Adding photos tab
        tabHost.addTab(groupspec); // Adding songs tab
        tabHost.addTab(setupspec); // Adding videos tab
    }

}
