package bumbums.puzzlepiece.ui.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.task.FirebaseTasks;
import bumbums.puzzlepiece.ui.FriendDetailActivity;
import bumbums.puzzlepiece.ui.RankFragment;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by 한승범 on 2017-02-11.
 */

public class RankRecyclerViewAdapter extends
        RealmRecyclerViewAdapter<Friend, RecyclerView.ViewHolder> {

    public static final int ITEM_TYPE_NORMAL = 0;
    public static final int ITEM_TYPE_HEADER = 1;

    private RankFragment rankFragment;

    public RankRecyclerViewAdapter(RankFragment rankFragment, OrderedRealmCollection<Friend> data) {
        super(rankFragment.getContext(), data, true);
        this.rankFragment = rankFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_TYPE_NORMAL) {
            View normalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friend_rank,parent,false);
            return new MyNormalViewHolder(normalView); // view holder for normal items
        } else{//  (viewType == ITEM_TYPE_HEADER) {
            View headerRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friend_rank_header, parent,false);
            return new MyHeaderViewHolder(headerRow); // view holder for header items
        }

        /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_friend_rank, parent, false);
        //itemView.setMinimumWidth(parent.getMeasuredWidth()/2);
        return new MyViewHolder(itemView);*/
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
            FirebaseTasks.loadFriendPhoto(rankFragment.getContext(), obj, ((MyNormalViewHolder)holder).photo);

        } else if (itemType == ITEM_TYPE_HEADER) {
            ((MyHeaderViewHolder)holder).data= obj;
            ((MyHeaderViewHolder)holder).puzzleNum.setText(String.valueOf(obj.getPuzzles().size()));
            ((MyHeaderViewHolder)holder).name.setText(obj.getName());
            FirebaseTasks.loadFriendPhoto(rankFragment.getContext(), obj, ((MyHeaderViewHolder)holder).photo);
        }


    }
    class MyHeaderViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        public Friend data;
        public TextView puzzleNum;
        public TextView name;
        public ImageView photo;

        public MyHeaderViewHolder(View view) {
            super(view);

            puzzleNum = (TextView)view.findViewById(R.id.tv_puzzle_num);
            name = (TextView)view.findViewById(R.id.tv_row_rank_name);
            photo = (ImageView)view.findViewById(R.id.iv_rank_user_profile);
          /*  rank = (TextView) view.findViewById(R.id.tv_row_rank);
            name = (TextView) view.findViewById(R.id.tv_row_rank_name);
            puzzleNum = (TextView) view.findViewById(R.id.tv_puzzle_num);
            photo = (ImageView) view.findViewById(R.id.iv_rank_user_profile);
            viewDetail = (ImageView) view.findViewById(R.id.iv_friend_detail);*/
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(rankFragment.getContext(), FriendDetailActivity.class);
            intent.putExtra(FriendDetailActivity.EXTRA_FRIENDID, data.getId());
            rankFragment.getContext().startActivity(intent);
        }
    }
    class MyNormalViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener {

        public TextView rank;
        public TextView name;
        public TextView puzzleNum;
        public ImageView photo;
        public ImageView viewDetail;


        public Friend data;

        public MyNormalViewHolder(View view) {
            super(view);

            rank = (TextView) view.findViewById(R.id.tv_row_rank);
            name = (TextView) view.findViewById(R.id.tv_row_rank_name);
            puzzleNum = (TextView) view.findViewById(R.id.tv_puzzle_num);
            photo = (ImageView) view.findViewById(R.id.iv_rank_user_profile);
            viewDetail = (ImageView) view.findViewById(R.id.iv_friend_detail);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(rankFragment.getContext(), FriendDetailActivity.class);
            intent.putExtra(FriendDetailActivity.EXTRA_FRIENDID, data.getId());
            rankFragment.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (position==0) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }

}
