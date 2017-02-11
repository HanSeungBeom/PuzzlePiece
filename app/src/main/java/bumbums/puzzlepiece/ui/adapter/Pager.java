package bumbums.puzzlepiece.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import bumbums.puzzlepiece.ui.RankFragment;
import bumbums.puzzlepiece.ui.TabFriendsFragment;
import bumbums.puzzlepiece.ui.TabPuzzleLogFragment;
import bumbums.puzzlepiece.ui.TabStatisticsFragment;

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
                TabFriendsFragment tabFriendsFragment = new TabFriendsFragment();
                return tabFriendsFragment;

            case 1:
                TabPuzzleLogFragment tabPuzzleLogFragment = new TabPuzzleLogFragment();
                return tabPuzzleLogFragment;
            case 2:
                RankFragment rankFragment = new RankFragment();
                return rankFragment;
                /*TabStatisticsFragment tabStatisticsFragment = new TabStatisticsFragment();
                return tabStatisticsFragment;
            */
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
