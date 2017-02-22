package bumbums.puzzlepiece.task;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.ui.AddPuzzleDirectActivity;
import bumbums.puzzlepiece.ui.MainActivity;
import bumbums.puzzlepiece.ui.ReviewActivity;

/**
 * Created by han sb on 2017-02-18.
 */

public class NotificationService extends IntentService {

    public static final int PENDING_REQUEST_CODE = 1;
    public static final int NOTIFICATION_CODE = 12;
    public NotificationService(){
        super(NotificationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notify_always);
        Intent addPuzzleintent = new Intent(this,AddPuzzleDirectActivity.class);
        addPuzzleintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        addPuzzleintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntentAddPuzzle = PendingIntent.getActivity(this,0,addPuzzleintent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent reviewPuzzleintent = new Intent(this,ReviewActivity.class);
        reviewPuzzleintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        reviewPuzzleintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntentReviewPuzzle = PendingIntent.getActivity(this,1,reviewPuzzleintent,PendingIntent.FLAG_UPDATE_CURRENT);



        android.support.v4.app.NotificationCompat.Builder builder  = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.puzzles_blue_small)
                .setContentIntent(pIntentAddPuzzle)
                .setContentIntent(pIntentReviewPuzzle)
                .setContent(remoteViews)
                .setOngoing(true);


        remoteViews.setImageViewResource(R.id.add_newpuzzle,R.drawable.ic_add_puzzle_on);
        remoteViews.setImageViewResource(R.id.review_today_puzzles,R.drawable.ic_review_puzzle_on);
        remoteViews.setOnClickPendingIntent(R.id.ll_new_puzzle,pIntentAddPuzzle);
        remoteViews.setOnClickPendingIntent(R.id.ll_review_puzzle,pIntentReviewPuzzle);
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(NOTIFICATION_CODE, builder.build());

    }
}
