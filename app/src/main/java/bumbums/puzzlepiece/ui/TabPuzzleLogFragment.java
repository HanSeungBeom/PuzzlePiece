package bumbums.puzzlepiece.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.adapter.LogRecyclerViewAdpater;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Created by han sb on 2017-02-08.
 */

public class TabPuzzleLogFragment extends android.support.v4.app.Fragment {

    private RecyclerView mRecyclerView;
    private Realm realm;
    private LogRecyclerViewAdpater mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        mAdapter =new LogRecyclerViewAdpater(this, realm.where(Puzzle.class).findAllAsync().sort(Puzzle.DATE_TO_MILLISECONDS, Sort.DESCENDING));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_puzzle_log, container,false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rv_puzzle_log);
        setUpRecyclerView();


        return view;
    }
    private void setUpRecyclerView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}

