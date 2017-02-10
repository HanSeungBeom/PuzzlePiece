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
import bumbums.puzzlepiece.ui.EditFriendActivity;
import bumbums.puzzlepiece.ui.FriendDetailActivity;
import bumbums.puzzlepiece.ui.TabFriendsFragment;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by 한승범 on 2017-02-07.
 */

public class RecyclerViewAdapter extends
        RealmRecyclerViewAdapter<Friend, RecyclerViewAdapter.MyViewHolder> {
    private final TabFriendsFragment tabFriendsFragment;
    public static final String EXTRA_ID = "id";

    public RecyclerViewAdapter(TabFriendsFragment tabFriendsFragment, OrderedRealmCollection<Friend> data) {
        super(tabFriendsFragment.getContext(), data, true);
        this.tabFriendsFragment = tabFriendsFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_grid, parent, false);
        itemView.setMinimumWidth(parent.getMeasuredWidth()/2);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friend obj = getData().get(position);
        holder.data = obj;
        //holder.userProfileImage =
        holder.userName.setText(obj.getName());
        holder.userPuzzleNum.setText(String.valueOf(obj.getPuzzles().size()));

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener{
        public ImageView userProfileImage;
        public TextView userName;
        public TextView userPuzzleNum;
        public ImageView editFriend;
        public Friend data;

        public MyViewHolder(View view) {
            super(view);

            userProfileImage = (ImageView)view.findViewById(R.id.iv_row_grid_profile);
            userName = (TextView)view.findViewById(R.id.tv_row_grid_name);
            userPuzzleNum = (TextView)view.findViewById(R.id.tv_row_grid_puzzle_num);
            editFriend = (ImageView)view.findViewById(R.id.iv_row_grid_edit);
            editFriend.setOnClickListener(this);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_row_grid_edit: {
                    Intent intent = new Intent(tabFriendsFragment.getContext(), EditFriendActivity.class);
                    intent.putExtra(EXTRA_ID,data.getId());
                    tabFriendsFragment.getContext().startActivity(intent);
                    //Log.d("###","edit click");
                    break;
                }
                default:
                    Intent intent = new Intent(tabFriendsFragment.getContext(),FriendDetailActivity.class);
                    intent.putExtra(EXTRA_ID,data.getId());
                    tabFriendsFragment.getContext().startActivity(intent);
                    break;
            }


           // Log.d("###","click");
        }

        @Override
        public boolean onLongClick(View v) {
            tabFriendsFragment.deleteFriend(data.getId());
            return true;
        }



       /* @Override
        public void onClick(View v) {
            for(int i=0;i<data.getDogs().size();i++){
                Log.d("###",data.getDogs().get(i).getName());
            }
        }*/

    }
}
