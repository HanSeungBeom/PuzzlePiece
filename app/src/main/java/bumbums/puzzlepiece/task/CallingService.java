package bumbums.puzzlepiece.task;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.CallingDialog;
import bumbums.puzzlepiece.ui.FriendDetailActivity;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.internal.IOException;

/**
 * Created by han sb on 2017-02-21.
 */

public class CallingService extends IntentService {

    public static final String EXTRA_CALL_NUMBER = "call_number";


    public CallingService(){
        super(NotificationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String phoneNumber = intent.getStringExtra(EXTRA_CALL_NUMBER);
        Realm realm = Realm.getDefaultInstance();
        //Log.d("###","phone="+phoneNumber);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isEnable = sharedPreferences.getBoolean(getString(R.string.pref_calling),false);
        Log.d("###","BOOL="+isEnable);
        if(isEnable) {
            Friend friend = realm.where(Friend.class).equalTo("phoneNumber", phoneNumber).findFirst();
            if (friend != null) {
                RealmList<Puzzle> puzzles = friend.getPuzzles();
                if (puzzles.size() != 0) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {

                    }
                    Intent intent1 = new Intent(this, CallingDialog.class);
                    intent1.putExtra(FriendDetailActivity.EXTRA_FRIENDID, friend.getId());
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent1);
                }
            }
        }
    }
}
