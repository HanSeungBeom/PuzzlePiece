package bumbums.puzzlepiece.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;

import bumbums.puzzlepiece.R;

import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.ui.adapter.Pager;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements
TabLayout.OnTabSelectedListener{

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mFriendNum;
    private TextView mTitle;
    private Realm realm;

    //FirebaseTest;
    private Button mTestBtn;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testFirebase();
        mTestBtn = (Button)findViewById(R.id.test_button);

        mTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.deleteDir(v.getContext().getFilesDir()+"/profile_pictures");
            }
        });



        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);

        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.friends_selector));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.history_selector));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.statistics_selector));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mViewPager = (ViewPager)findViewById(R.id.pager);

        Pager adapter = new Pager(getSupportFragmentManager(),mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.friends_selector);
        mTabLayout.getTabAt(1).setIcon(R.drawable.history_selector);
        mTabLayout.getTabAt(2).setIcon(R.drawable.statistics_selector);

        mFriendNum =(TextView)findViewById(R.id.tv_friend_num);


        mTabLayout.addOnTabSelectedListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mTitle =(TextView)findViewById(R.id.tv_title);




    }

    @Override
    protected void onResume() {
        super.onResume();

        //onCreate 에 하면 이상하게 안됨. listener 를 못찾음
        realm = Realm.getDefaultInstance();
        final RealmResults<Friend> results = realm.where(Friend.class)
                .findAll();
        mFriendNum.setText(String.valueOf(results.size()));

        results.addChangeListener(new RealmChangeListener<RealmResults<Friend>>() {
            @Override
            public void onChange(RealmResults<Friend> element) {
                mFriendNum.setText(String.valueOf(element.size()));
            }
        });

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()){
            case 0:
                mTitle.setText("지인");
                mFriendNum.setVisibility(View.VISIBLE);
                break;
            case 1:
                mTitle.setText("로그");
                mFriendNum.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mTitle.setText("통계");
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
}
