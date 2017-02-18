package bumbums.puzzlepiece.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.task.NotificationService;

/**
 * Created by han sb on 2017-02-19.
 */

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notificationSettings =pref.getBoolean(context.getString(R.string.pref_noti),false);
        if(notificationSettings){
            Intent notiIntent = new Intent(context, NotificationService.class);
            context.startService(notiIntent);
        }
    }
}
