package bumbums.puzzlepiece.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.adapter.FriendRecyclerViewAdapter;
import bumbums.puzzlepiece.ui.adapter.FriendReviewRecyclerViewAdapter;
import bumbums.puzzlepiece.ui.adapter.ReviewRecyclerViewAdapter;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by 한승범 on 2017-02-16.
 */

public class TabReviewFragment extends Fragment{

    private RecyclerView mReviewRecyclerView;
    private RecyclerView mFriendRecyclerView;
    private Realm realm;
    private ReviewRecyclerViewAdapter mReviewAdapter;
    private FriendReviewRecyclerViewAdapter mFriendAdapter;
    private int mSelectedID=-1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        mFriendAdapter = new FriendReviewRecyclerViewAdapter(this, getTodayFriend());
        mReviewAdapter = new ReviewRecyclerViewAdapter(this, getTodayPuzzles());

    }

    public RealmResults<Friend> getTodayFriend(){

        RealmResults<Puzzle> todayPuzzles = getTodayPuzzles();

        List<Long> todayFriendId = new ArrayList<Long>();

        for(int i=0;i<todayPuzzles.size();i++){
            todayFriendId.add(todayPuzzles.get(i).getFriendId());
        }

        //오늘 추가된 friendlist (중복제거)

        List<Long> uniqueFriendId = new ArrayList<Long>(new HashSet<Long>(todayFriendId));

        //오늘 퍼즐등록한 친구들만 긁어온다.
        RealmQuery<Friend> query = realm.where(Friend.class);
        if(uniqueFriendId.size()>0) {
            query = query.equalTo(Friend.USER_ID,uniqueFriendId.get(0));
            for (int i = 1; i < uniqueFriendId.size(); i++) {
                query = query.or().equalTo(Friend.USER_ID, uniqueFriendId.get(i));
            }
        }
        //TODO 나중에 필드추가
        RealmResults<Friend> todayFriends = query.findAllSortedAsync("name",Sort.ASCENDING);
        Log.d("###",todayFriends.size()+"");
        return todayFriends;

    }

    public RealmResults<Puzzle> getTodayPuzzles(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);
        Calendar tomorrow = (Calendar)today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH,1);

        RealmResults<Puzzle> todayPuzzles = realm.where(Puzzle.class)
                .greaterThanOrEqualTo(Puzzle.DATE_TO_MILLISECONDS,today.getTimeInMillis())
                .lessThan(Puzzle.DATE_TO_MILLISECONDS,tomorrow.getTimeInMillis())
                .findAllSorted(Puzzle.DATE_TO_MILLISECONDS,Sort.ASCENDING);;

        return todayPuzzles;
    }


    public RealmResults<Puzzle> getTodayFriendPuzzles(long friendId){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);
        Calendar tomorrow = (Calendar)today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH,1);

        return  realm.where(Puzzle.class)
                .greaterThanOrEqualTo(Puzzle.DATE_TO_MILLISECONDS,today.getTimeInMillis())
                .lessThan(Puzzle.DATE_TO_MILLISECONDS,tomorrow.getTimeInMillis())
                .equalTo(Puzzle.FRIEND_ID, friendId)
                .findAllSortedAsync(Puzzle.DATE_TO_MILLISECONDS,Sort.ASCENDING);
    }

    public void settingClickedFriendLog(long friendId){
        mReviewAdapter.updateData(getTodayFriendPuzzles(friendId));

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_review, container,false);
        mReviewRecyclerView =(RecyclerView) view.findViewById(R.id.rv_review);
        mFriendRecyclerView =(RecyclerView) view.findViewById(R.id.rv_review_friend);
        setUpRecyclerView();
        return view;
    }
    private void setUpRecyclerView(){
        mFriendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        mFriendRecyclerView.setAdapter(mFriendAdapter);
        mFriendRecyclerView.setHasFixedSize(true);

        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mReviewRecyclerView.setHasFixedSize(true);

    }
}
