package bumbums.puzzlepiece.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.ui.adapter.Pager;

public class MainActivity extends AppCompatActivity implements
TabLayout.OnTabSelectedListener{

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
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
        mTabLayout.addOnTabSelectedListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("지인");


    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()){
            case 0:
                getSupportActionBar().setTitle("지인");
                break;
            case 1:
                getSupportActionBar().setTitle("로그");
                break;
            case 2:
                getSupportActionBar().setTitle("통계");
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
