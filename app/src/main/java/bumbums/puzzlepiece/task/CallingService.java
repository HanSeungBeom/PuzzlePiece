package bumbums.puzzlepiece.task;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.CallingDialog;
import bumbums.puzzlepiece.ui.FriendDetailActivity;
import bumbums.puzzlepiece.ui.adapter.CallingRecyclerViewAdapter;
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
    private FrameLayout clear;

    String call_number;

    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private CallingRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Realm realm;

    public static int PARAM_DEFAULT_INT = -11111;
    private Boolean mIsRemoved ;
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

        int width = (int) (screenWidth * 0.6);
        //int height = (int) (screenHeight * 0.3);//Display 사이즈의 90%


        params = new WindowManager.LayoutParams(
                width,
                WindowManager.LayoutParams.WRAP_CONTENT,
                //height,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        settingParamXY();

        params.windowAnimations = android.R.style.Animation_Toast;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rootView = layoutInflater.inflate(R.layout.activity_calling_dialog, null);

        realm = Realm.getDefaultInstance();

        name = (TextView) rootView.findViewById(R.id.tv_name);
        clear = (FrameLayout) rootView.findViewById(R.id.ll_cancel);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_calling);
        mAdapter = new CallingRecyclerViewAdapter(this, null);
        mIsRemoved = false;
        setUpRecyclerView();

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePopup();
            }
        });

        setDraggable();


    }

    private void settingParamXY() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int savedX = pref.getInt(getString(R.string.pref_calling_param_x),PARAM_DEFAULT_INT);
        int savedY = pref.getInt(getString(R.string.pref_calling_param_y),PARAM_DEFAULT_INT);
        if(savedX!=PARAM_DEFAULT_INT && savedY!=PARAM_DEFAULT_INT){
            //저장된 값이 있으면
            params.x = savedX;
            params.y = savedY;
        }
        else{
            params.gravity = Gravity.TOP;
            params.y =20;
        }
    }

    private void setDraggable() {

        rootView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        if (rootView != null)
                            windowManager.updateViewLayout(rootView, params);
                        return true;
                }
                return false;
            }
        });

    }



    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
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

            if(rootView.getWindowToken()!=null){
                windowManager.removeView(rootView);
            }
            windowManager.addView(rootView, params);


            if (!TextUtils.isEmpty(call_number)) {
                name.setText(friend.getName());
                //전화번호가 DB에 있는 경우만.
                RealmList<Puzzle> puzzles = friend.getPuzzles();
                if (puzzles.size() != 0) {
                    mAdapter.updateData(realm.where(Puzzle.class)
                            .equalTo(Puzzle.FRIEND_ID, friend.getId())
                            .findAllSorted(Puzzle.DATE_TO_MILLISECONDS, Sort.DESCENDING));

                } else {
                    removePopup();
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
        removePopup();
        super.onDestroy();

    }


    //Onclick
    public void removePopup() {

        if(!mIsRemoved) {
            if (rootView != null && windowManager != null) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(getString(R.string.pref_calling_param_x), params.x);
                editor.putInt(getString(R.string.pref_calling_param_y), params.y);
                editor.commit();


                windowManager.removeView(rootView);
                mIsRemoved = true;
            }
        }
    }


}
