package bumbums.puzzlepiece.ui.adapter;

/**
 * Created by han sb on 2017-02-16.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.task.FirebaseTasks;
import bumbums.puzzlepiece.ui.TabRankFragment;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by 한승범 on 2017-02-11.
 */

public class FriendRankRecyclerViewAdapter extends
        RealmRecyclerViewAdapter<Friend, RecyclerView.ViewHolder> {

    public static final int ITEM_TYPE_NORMAL = 0;
    public static final int ITEM_TYPE_HEADER = 1;

    private TabRankFragment tabRankFragment;
    private long mFriendId;
    public FriendRankRecyclerViewAdapter(TabRankFragment tabRankFragment, OrderedRealmCollection<Friend> data,long friendId) {

        super(tabRankFragment.getContext(), data, true);
        this.tabRankFragment = tabRankFragment;
        this.mFriendId = friendId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_TYPE_NORMAL) {
            View normalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friend_myrank,parent,false);
            return new MyNormalViewHolder(normalView); // view holder for normal items
        } else{//  (viewType == ITEM_TYPE_HEADER) {
            View headerRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friend_myrank_header, parent,false);
            return new MyHeaderViewHolder(headerRow); // view holder for header items
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Friend obj = getData().get(position);

        final int itemType = getItemViewType(position);
        if (itemType == ITEM_TYPE_NORMAL) {
            ((MyNormalViewHolder)holder).data = obj;
            ((MyNormalViewHolder)holder).rank.setText(String.valueOf(position + 1));
            ((MyNormalViewHolder)holder).name.setText(obj.getName());
            ((MyNormalViewHolder)holder).puzzleNum.setText(String.valueOf(obj.getPuzzles().size()));
            FirebaseTasks.loadFriendPhoto(tabRankFragment.getContext(), obj, ((MyNormalViewHolder)holder).photo);

        } else if (itemType == ITEM_TYPE_HEADER) {
            ((MyHeaderViewHolder)holder).data= obj;
            ((MyHeaderViewHolder)holder).rank.setText(String.valueOf(position + 1));
            ((MyHeaderViewHolder)holder).puzzleNum.setText(String.valueOf(obj.getPuzzles().size()));
            ((MyHeaderViewHolder)holder).name.setText(obj.getName());
            FirebaseTasks.loadFriendPhoto(tabRankFragment.getContext(), obj, ((MyHeaderViewHolder)holder).photo);
        }



    }
    class MyHeaderViewHolder extends RecyclerView.ViewHolder
        {
        public Friend data;
        public TextView puzzleNum;
        public TextView name;
        public ImageView photo;
        public TextView rank;

        public MyHeaderViewHolder(View view) {
            super(view);
            rank = (TextView) view.findViewById(R.id.tv_row_rank);
            name = (TextView) view.findViewById(R.id.tv_row_rank_name);
            puzzleNum = (TextView) view.findViewById(R.id.tv_puzzle_num);
            photo = (ImageView) view.findViewById(R.id.iv_rank_user_profile);

    }

    }
    class MyNormalViewHolder extends RecyclerView.ViewHolder
           {

        public TextView rank;
        public TextView name;
        public TextView puzzleNum;
        public ImageView photo;


        public Friend data;

        public MyNormalViewHolder(View view) {
            super(view);
            rank = (TextView) view.findViewById(R.id.tv_row_rank);
            name = (TextView) view.findViewById(R.id.tv_row_rank_name);
            puzzleNum = (TextView) view.findViewById(R.id.tv_puzzle_num);
            photo = (ImageView) view.findViewById(R.id.iv_rank_user_profile);
        }


    }
    @Override
    public int getItemViewType(int position) {
        Friend friend = getData().get(position);
        if (friend.getId()==mFriendId) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }

}
