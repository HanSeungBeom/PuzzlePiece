package bumbums.puzzlepiece.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bumbums.puzzlepiece.R;

/**
 * Created by han sb on 2017-02-08.
 */

public class TabStatisticsFragment extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_history, container,false);
        return view;
    }
}
