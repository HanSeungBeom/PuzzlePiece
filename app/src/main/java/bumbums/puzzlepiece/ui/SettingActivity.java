package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.task.NotificationService;
import bumbums.puzzlepiece.util.Utils;

public class SettingActivity extends AppCompatActivity {

    SwitchCompat mSwitch;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        mSwitch = (SwitchCompat) findViewById(R.id.sw_noti);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = pref.edit();
                if (isChecked) {
                    editor.putBoolean(getString(R.string.pref_noti), true);
                    Intent i = new Intent(SettingActivity.this, NotificationService.class);
                    startService(i);
                } else {
                    editor.putBoolean(getString(R.string.pref_noti), false);
                    Utils.cancelNotification(mContext, NotificationService.NOTIFICATION_CODE);
                }
                editor.commit();
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}
