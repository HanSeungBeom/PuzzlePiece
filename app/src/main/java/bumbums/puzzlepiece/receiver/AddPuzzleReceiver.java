package bumbums.puzzlepiece.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import bumbums.puzzlepiece.ui.AddPuzzleDirectActivity;

/**
 * Created by han sb on 2017-02-18.
 */

public class AddPuzzleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("###","ADDPUZLERECEIVER");
        /*Intent i = new Intent(context, AddPuzzleDirectActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(i);

        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));*/
    }
}
