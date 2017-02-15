package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.rubensousa.floatingtoolbar.FloatingToolbar;

import bumbums.puzzlepiece.R;

/**
 * Created by han sb on 2017-02-08.
 */

public class TabStatisticsFragment extends android.support.v4.app.Fragment {

    private FloatingToolbar mFabToolbar;
    private FloatingActionButton mFab;
    private LinearLayout mFragmentGraph,mFragmentRank,mFragmentLog;
    private RecyclerView mRankRecyclerView,mLogRecyclerView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_statistics, container,false);
        mFragmentGraph = (LinearLayout)view.findViewById(R.id.graph_fragment);
        mFragmentRank = (LinearLayout)view.findViewById(R.id.rank_fragment);
        mFragmentLog = (LinearLayout)view.findViewById(R.id.log_fragment);
        mFab = (FloatingActionButton)view.findViewById(R.id.fab);
        mRankRecyclerView = (RecyclerView)view.findViewById(R.id.rv_rank);
        mLogRecyclerView =(RecyclerView)view.findViewById(R.id.rv_puzzle_log);


        mFabToolbar = (FloatingToolbar)view.findViewById(R.id.floatingToolbar);
        mFabToolbar.attachFab(mFab);
        mFabToolbar.setClickListener(new FloatingToolbar.ItemClickListener(){
            @Override
            public void onItemClick(MenuItem item) {
               // Toast.makeText(getContext(),item.getTitle(),Toast.LENGTH_SHORT).show();
                //이곳에서 MainActivity로 값을 넘겨줘서 MainActivity에서 fragment 를 변경하게 한다.
                int id = item.getItemId();
                switch (id){
                    case R.id.action_graph:
                        mFab.setImageResource(R.drawable.ic_menu_graph);
                        setInvisibleFragment();
                        mFragmentGraph.setVisibility(View.VISIBLE);
                        //myhandler.clickIcon(id);
                        break;
                    case R.id.action_rank:
                        mFab.setImageResource(R.drawable.ic_menu_rank);
                        setInvisibleFragment();
                        mFragmentRank.setVisibility(View.VISIBLE);
                       // myhandler.clickIcon(id);
                        break;
                    case R.id.action_log:
                        mFab.setImageResource(R.drawable.ic_menu_log);
                        setInvisibleFragment();
                        mFragmentLog.setVisibility(View.VISIBLE);
                       // myhandler.clickIcon(id);
                        break;
                    default:

                }
            }
            @Override
            public void onItemLongClick(MenuItem item) {

            }
        });
        mFabToolbar.attachRecyclerView(mRankRecyclerView);
        mFabToolbar.attachRecyclerView(mLogRecyclerView);

        setInvisibleFragment();
        mFragmentGraph.setVisibility(View.VISIBLE);

        return view;
    }
    public void setInvisibleFragment(){
        mFragmentGraph.setVisibility(View.INVISIBLE);
        mFragmentRank.setVisibility(View.INVISIBLE);
        mFragmentLog.setVisibility(View.INVISIBLE);
    }



}
