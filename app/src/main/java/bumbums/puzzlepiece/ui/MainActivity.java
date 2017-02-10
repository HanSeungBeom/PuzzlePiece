package bumbums.puzzlepiece.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.ui.adapter.Pager;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements
TabLayout.OnTabSelectedListener{

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mFriendNum;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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




        mTabLayout.addOnTabSelectedListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("지인");
        realm = Realm.getDefaultInstance();







    }

    @Override
    protected void onResume() {
        super.onResume();
        mFriendNum =(TextView)findViewById(R.id.tv_friend_num);
        //onCreate 에 하면 이상하게 안됨. listener 를 못찾음

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
                getSupportActionBar().setTitle("지인");
                mFriendNum.setVisibility(View.VISIBLE);
                break;
            case 1:
                getSupportActionBar().setTitle("로그");
                mFriendNum.setVisibility(View.INVISIBLE);
                break;
            case 2:
                getSupportActionBar().setTitle("통계");
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
