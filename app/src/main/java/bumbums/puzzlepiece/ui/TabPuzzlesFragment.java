package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.adapter.PuzzleRecyclerViewAdpater;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by han sb on 2017-02-15.
 */

public class TabPuzzlesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private Realm realm;
    private PuzzleRecyclerViewAdpater mAdapter;
    private Long mFriendId;
    private LinearLayout mEmptyView;
    private RealmResults<Puzzle> puzzles;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
       // Log.d("###",mFriendId.toString());
        mAdapter =new PuzzleRecyclerViewAdpater(this, realm.where(Puzzle.class).equalTo(Puzzle.FRIEND_ID,mFriendId).findAllSortedAsync(Puzzle.DATE_TO_MILLISECONDS,Sort.DESCENDING));
        puzzles = realm.where(Puzzle.class).equalTo(Puzzle.FRIEND_ID,mFriendId).findAllAsync();
        puzzles.addChangeListener(new RealmChangeListener<RealmResults<Puzzle>>() {
            @Override
            public void onChange(RealmResults<Puzzle> element) {
                if(element.size()==0){
                    showEmptyView();
                }
                else{
                    hideEmptyView();
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FriendDetailActivity){
            FriendDetailActivity friendDetailActivity = (FriendDetailActivity)context;
            Intent intent = friendDetailActivity.getIntent();

            mFriendId = intent.getLongExtra(FriendDetailActivity.EXTRA_FRIENDID,-1);

        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_puzzles, container,false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rv_puzzles);
        mEmptyView = (LinearLayout)view.findViewById(R.id.empty_view);
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    public void deletePuzzle(final long id){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Puzzle puzzle = realm.where(Puzzle.class).equalTo(Puzzle.PUZZLE_ID,id).findFirst();
                puzzle.deleteFromRealm();
                //갱신
                Friend friend = realm.where(Friend.class).equalTo(Friend.FRIEND_ID,mFriendId).findFirst();
                friend.setPuzzleNum(friend.getPuzzles().size());
            }
        });

       // Toast.makeText(getContext(),"id="+id+" deleted",Toast.LENGTH_SHORT).show();
    }
    public void showEmptyView(){
        mEmptyView.setVisibility(View.VISIBLE);
    }
    public void hideEmptyView(){
        mEmptyView.setVisibility(View.INVISIBLE);
    }
}
