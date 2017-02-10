package bumbums.puzzlepiece.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.adapter.LogRecyclerViewAdpater;
import io.realm.Realm;


/**
 * Created by han sb on 2017-02-08.
 */

public class TabPuzzleLogFragment extends android.support.v4.app.Fragment {

    private RecyclerView mRecyclerView;
    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_puzzle_log, container,false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rv_puzzle_log);
        realm = Realm.getDefaultInstance();
        setUpRecyclerView();


        return view;
    }
    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new LogRecyclerViewAdpater(this, realm.where(Puzzle.class).findAllAsync()));
        mRecyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }
}

