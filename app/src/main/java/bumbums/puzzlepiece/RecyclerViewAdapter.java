package bumbums.puzzlepiece;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.tab.TabFriends;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by 한승범 on 2017-02-07.
 */

public class RecyclerViewAdapter extends
        RealmRecyclerViewAdapter<Friend, RecyclerViewAdapter.MyViewHolder> {
    private final TabFriends tabFriends;

    public RecyclerViewAdapter(TabFriends tabFriends, OrderedRealmCollection<Friend> data) {
        super(tabFriends.getContext(), data, true);
        this.tabFriends = tabFriends;
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
        public Friend data;

        public MyViewHolder(View view) {
            super(view);

            userProfileImage = (ImageView)view.findViewById(R.id.iv_row_grid_profile);
            userName = (TextView)view.findViewById(R.id.tv_row_grid_name);
            userPuzzleNum = (TextView)view.findViewById(R.id.tv_row_grid_puzzle_num);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
           Log.d("###","click");
        }

        @Override
        public boolean onLongClick(View v) {
            tabFriends.deleteFriend(data.getId());
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
