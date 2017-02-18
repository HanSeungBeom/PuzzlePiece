package bumbums.puzzlepiece.ui;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;


import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import bumbums.puzzlepiece.R;

import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.task.NotificationService;
import bumbums.puzzlepiece.ui.adapter.TabAdapter;
import bumbums.puzzlepiece.util.AppPermissions;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mFriendNum;
    private TextView mTitle;
    private Realm realm;
    private TabAdapter mAdapter;

    private Button mTestBtn;
    /*
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }
        super.onStart();
    }
    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("###", "signInAnonymously:FAILURE", exception);
                    }
                });
    }
    public void testFirebase(){
        mAuth = FirebaseAuth.getInstance();
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //testFirebase();

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTestBtn = (Button) findViewById(R.id.testBtn);
        mTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MainActivity.this, AddPuzzleDirectActivity.class);
                startActivity(intent);*/
                test();
            }
        });
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mAdapter = new TabAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new TabFriendsFragment());
        mAdapter.addFragment(new TabMainRankFragment());
        mAdapter.addFragment(new TabGraphFragment());
        mAdapter.addFragment(new TabReviewFragment());

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


        mTabLayout.getTabAt(0).setIcon(R.drawable.friends_selector);
        mTabLayout.getTabAt(1).setIcon(R.drawable.rank_selector);
        mTabLayout.getTabAt(2).setIcon(R.drawable.graph_selector);
        mTabLayout.getTabAt(3).setIcon(R.drawable.review_selector);

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mFriendNum = (TextView) findViewById(R.id.tv_friend_num);

        mTabLayout.addOnTabSelectedListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mTitle = (TextView) findViewById(R.id.tv_title);


    }


    @Override
    protected void onStart() {
        super.onStart();
        //onCreate 에 하면 이상하게 안됨. listener 를 못찾음
        realm = Realm.getDefaultInstance();

        final RealmResults<Friend> results = realm.where(Friend.class)
                .findAllAsync();

        results.addChangeListener(new RealmChangeListener<RealmResults<Friend>>() {
            @Override
            public void onChange(RealmResults<Friend> element) {
                mFriendNum.setText(String.valueOf(element.size()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                mTitle.setText("지인");
                mFriendNum.setVisibility(View.VISIBLE);
                break;
            case 1:
                Utils.hideKeyboard(this);
                mTitle.setText("랭킹");
                mFriendNum.setVisibility(View.INVISIBLE);
                break;
            case 2:
                Utils.hideKeyboard(this);
                mTitle.setText("통계");
                mFriendNum.setVisibility(View.INVISIBLE);
                break;
            case 3:
                Utils.hideKeyboard(this);
                mTitle.setText("오늘 등록한 퍼즐");
                mFriendNum.setVisibility(View.INVISIBLE);
                break;

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    public interface onKeyBackPressedListener {
        public void onBack();
    }

    private onKeyBackPressedListener mOnKeyBackPressedListener;

    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (mOnKeyBackPressedListener != null) {
            mOnKeyBackPressedListener.onBack();
        } else {
            super.onBackPressed();
        }
    }

    public void test() {
        Intent i = new Intent(this, NotificationService.class);
        startService(i);



    }
}
