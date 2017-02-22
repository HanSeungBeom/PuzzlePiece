package bumbums.puzzlepiece.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;


import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.task.CallingService;

/**
 * Created by han sb on 2017-02-21.
 */


public class IncomingCallBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "PHONE STATE";
    private static String mLastState;
    public static String phone_number;
    private final Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    public void onReceive(final Context context, Intent intent) {


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isCallingOn = sharedPreferences.getBoolean(context.getString(R.string.pref_calling), false);

        if (isCallingOn) {
            // Log.d(TAG, "onReceive()");
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            if (action.equals("android.intent.action.PHONE_STATE")) {
                String state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    phone_number = "";
                    Log.d("###", " EXTRA_STATE_IDLE ");
                    Intent Service = new Intent(context, CallingService.class);
                    context.stopService(Service);

                } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                    phone_number = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Intent serviceIntent = new Intent(context, CallingService.class);
                    serviceIntent.putExtra(CallingService.EXTRA_CALL_NUMBER, phone_number);
                    context.startService(serviceIntent);
                    Log.d("###", " EXTRA_STATE_RINGING INCOMMING NUMBER : " + bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                    Log.d("###", " EXTRA_STATE_OFFHOOK ");
                }
            } else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                phone_number = bundle.getString(Intent.EXTRA_PHONE_NUMBER);
                Intent serviceIntent = new Intent(context, CallingService.class);
                serviceIntent.putExtra(CallingService.EXTRA_CALL_NUMBER, phone_number);
                context.startService(serviceIntent);

                Log.d("###", " OUTGOING CALL : " + bundle.getString(Intent.EXTRA_PHONE_NUMBER));
            }
        }
    }

}