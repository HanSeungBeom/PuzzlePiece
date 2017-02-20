package bumbums.puzzlepiece.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Locale;

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


       // Log.d(TAG, "onReceive()");
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (action.equals("android.intent.action.PHONE_STATE")) {
            String state = bundle.getString(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

               // Log.d(TAG, " EXTRA_STATE_IDLE ");

            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                phone_number= bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //Log.d(TAG, " EXTRA_STATE_RINGING INCOMMING NUMBER : " + bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Intent serviceIntent = new Intent(context, CallingService.class);
                serviceIntent.putExtra(CallingService.EXTRA_CALL_NUMBER,phone_number);
                context.startService(serviceIntent);
               // Log.d(TAG, " EXTRA_STATE_OFFHOOK ");
            }
        } else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            Intent serviceIntent = new Intent(context, CallingService.class);
            serviceIntent.putExtra(CallingService.EXTRA_CALL_NUMBER, bundle.getString(Intent.EXTRA_PHONE_NUMBER));
            context.startService(serviceIntent);
          //  Log.d(TAG, " OUTGOING CALL : " + bundle.getString(Intent.EXTRA_PHONE_NUMBER));
        }
    }


}