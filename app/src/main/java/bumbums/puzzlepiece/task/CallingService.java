package bumbums.puzzlepiece.task;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.CallingDialog;
import bumbums.puzzlepiece.ui.FriendDetailActivity;
import bumbums.puzzlepiece.ui.adapter.ReviewRecyclerViewAdapter;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;

/**
 * Created by han sb on 2017-02-22.
 */

public class CallingService extends Service {
    public static final String EXTRA_CALL_NUMBER = "call_number";
    public static final long TIME_POPUP_SHOW = 1500;
    protected View rootView;

    private TextView name;
    private TextView phone;
    private ImageView clear;

    String call_number;

    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private ReviewRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Realm realm;
    private LinearLayout mEmptyView;

    @Override
    public IBinder onBind(Intent intent) {

        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("###", "COMECOME");


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;

        int width = (int) (screenWidth * 0.9);
        int height = (int) (screenHeight * 0.5);//Display 사이즈의 90%


        params = new WindowManager.LayoutParams(
                width,
                //WindowManager.LayoutParams.WRAP_CONTENT,
                height,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;
        params.y =20;

        params.windowAnimations = android.R.style.Animation_Toast;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rootView = layoutInflater.inflate(R.layout.activity_calling_dialog, null);
        name = (TextView) rootView.findViewById(R.id.tv_name);
        phone = (TextView) rootView.findViewById(R.id.tv_phone_number);
        clear = (ImageView) rootView.findViewById(R.id.iv_clear);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_calling);
        mEmptyView = (LinearLayout) rootView.findViewById(R.id.empty_view);
        realm = Realm.getDefaultInstance();
        mAdapter = new ReviewRecyclerViewAdapter(this, null);

        setUpRecyclerView();

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePopup();
            }
        });
        // setDraggable();


    }

    private void setUpRecyclerView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setExtra(intent);
        Friend friend = realm.where(Friend.class).equalTo(Friend.FRIEND_PHONE_NUMBER, call_number).findFirst();
        if (friend != null) {
            try {
                Thread.sleep(TIME_POPUP_SHOW);
            }catch (InterruptedException ie){

            }
            windowManager.addView(rootView, params);
            if (!TextUtils.isEmpty(call_number)) {
                phone.setText(call_number);
                name.setText(friend.getName());
                //전화번호가 DB에 있는 경우만.
                RealmList<Puzzle> puzzles = friend.getPuzzles();
                if (puzzles.size() != 0) {
                    mAdapter.updateData(realm.where(Puzzle.class)
                            .equalTo(Puzzle.FRIEND_ID, friend.getId())
                            .findAllSorted(Puzzle.DATE_TO_MILLISECONDS, Sort.DESCENDING));
                    hideEmptyView();
                } else {
                    showEmptyView();
                }
            }

        }


        return START_REDELIVER_INTENT;
    }


    private void setExtra(Intent intent) {

        if (intent == null) {
            Log.d("###", "NULL");
            removePopup();
            return;
        }

        call_number = intent.getStringExtra(EXTRA_CALL_NUMBER);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        removePopup();
    }


    //Onclick
    public void removePopup() {
        if (rootView != null && windowManager != null) windowManager.removeView(rootView);
    }

    public void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
    }

    public void hideEmptyView() {
        mEmptyView.setVisibility(View.INVISIBLE);
    }


}
