package bumbums.puzzlepiece.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import bumbums.puzzlepiece.ui.ReviewActivity;

/**
 * Created by han sb on 2017-02-18.
 */

public class ReviewReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("###","ReviewPUZLERECEIVER");
      /*  Intent i = new Intent(context, ReviewActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(i);
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));*/
    }
}
