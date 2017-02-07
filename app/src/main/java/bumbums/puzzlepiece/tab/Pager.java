package bumbums.puzzlepiece.tab;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import io.realm.Realm;

/**
 * Created by han sb on 2017-02-08.
 */

public class Pager extends FragmentStatePagerAdapter {
    int tabCount;

    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                TabFriends tabFriends = new TabFriends();
                return tabFriends;
            case 1:
                TabHistory tabHistory = new TabHistory();
                return tabHistory;
            case 2:
                TabStatistics tabStatistics = new TabStatistics();
                return tabStatistics;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
