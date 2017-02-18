package bumbums.puzzlepiece.task;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.ui.AddPuzzleDirectActivity;
import bumbums.puzzlepiece.ui.ReviewActivity;

/**
 * Created by han sb on 2017-02-18.
 */

public class NotificationService extends IntentService {

    public static final int PENDING_REQUEST_CODE = 1;
    public static final int NOTIFICATION_CODE = 1;
    public NotificationService(){
        super(NotificationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent addPuzzleIntent = new Intent(this,AddPuzzleDirectActivity.class);
        Intent reviewPuzzleIntent = new Intent(this,ReviewActivity.class);
         android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.puzzles_white)
                .addAction(R.drawable.ic_noti_puzzle1,"퍼즐등록", PendingIntent.getActivity(this,PENDING_REQUEST_CODE,addPuzzleIntent,0))
                .addAction(R.drawable.ic_noti_review1,"리뷰",PendingIntent.getActivity(this,PENDING_REQUEST_CODE,reviewPuzzleIntent,0))
                .setOngoing(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_CODE, mBuilder.build());

    }
}
