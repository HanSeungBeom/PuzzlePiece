package bumbums.puzzlepiece.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han sb on 2017-02-08.
 */

public class TabAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();


    public TabAdapter(FragmentManager fm) {
        super(fm);
        //Initializing tab count
    }

    public void addFragment(Fragment fragment){
        mFragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
