package bumbums.puzzlepiece.ui.adapter;

/**
 * Created by han sb on 2017-02-17.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.ui.TabReviewFragment;
import bumbums.puzzlepiece.util.CircleTransform;
import bumbums.puzzlepiece.task.FirebaseTasks;
import bumbums.puzzlepiece.util.Utils;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.ui.EditFriendActivity;
import bumbums.puzzlepiece.ui.FriendDetailActivity;
import bumbums.puzzlepiece.ui.TabFriendsFragment;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by 한승범 on 2017-02-07.
 */

public class FriendReviewRecyclerViewAdapter  extends
        RealmRecyclerViewAdapter<Friend, FriendReviewRecyclerViewAdapter.MyViewHolder>{
    private final TabReviewFragment tabReviewFragment;
    private long mSelectedId;


    public FriendReviewRecyclerViewAdapter(TabReviewFragment tabReviewFragment, OrderedRealmCollection<Friend> data) {
        super(tabReviewFragment.getContext(), data, true);
        this.tabReviewFragment = tabReviewFragment;
        mSelectedId =-1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_review_friend, parent, false);

        //  itemView.setMinimumWidth(parent.getMeasuredWidth()/2);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friend obj = getData().get(position);
        holder.data = obj;
        FirebaseTasks.loadFriendPhoto(tabReviewFragment.getContext(),holder.data,holder.userProfileImage);
        // holder.colorView.setBackgroundResource(Utils.colors[((int)obj.getId()%15)]);
        holder.userName.setText(obj.getName());
        if(mSelectedId == obj.getId())
            holder.border.setBackgroundResource(R.drawable.border_yellow);
        else
            holder.border.setBackgroundResource(R.drawable.border);

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {

        public ImageView userProfileImage;
        public TextView userName;
        public LinearLayout border;

        // public TextView userPuzzleNum;
        // public ImageView editFriend;
        public View colorView;
        public Friend data;

        public MyViewHolder(View view) {
            super(view);

            userProfileImage = (ImageView)view.findViewById(R.id.iv_row_grid_profile);
            userName = (TextView)view.findViewById(R.id.tv_row_grid_name);
            border = (LinearLayout)view.findViewById(R.id.ll_friendreview);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(mSelectedId == data.getId()) {
                mSelectedId = -1;
            }
            else{
                mSelectedId = data.getId();
            }


        }






    }
}
