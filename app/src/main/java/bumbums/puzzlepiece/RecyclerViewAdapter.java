package bumbums.puzzlepiece;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bumbums.puzzlepiece.model.Friend;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by 한승범 on 2017-02-07.
 */

public class RecyclerViewAdapter extends
        RealmRecyclerViewAdapter<Friend, RecyclerViewAdapter.MyViewHolder> {
    private final MainActivity activity;

    public RecyclerViewAdapter(MainActivity activity, OrderedRealmCollection<Friend> data) {
        super(activity, data, true);
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_grid, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friend obj = getData().get(position);
        holder.data = obj;
        //holder.userProfileImage =
        holder.userName.setText(obj.getName());
        holder.userPuzzleNum.setText(String.valueOf(obj.getPuzzles().size()));
        holder.userPhoneNumber.setText(obj.getPhoneNumber());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView userProfileImage;
        public TextView userName;
        public TextView userPuzzleNum;
        public TextView userPhoneNumber;
        public Friend data;

        public MyViewHolder(View view) {
            super(view);

            userProfileImage = (ImageView)view.findViewById(R.id.iv_row_grid_profile);
            userName = (TextView)view.findViewById(R.id.tv_row_grid_name);
            userPuzzleNum = (TextView)view.findViewById(R.id.tv_row_grid_puzzle_num);
            userPhoneNumber = (TextView)view.findViewById(R.id.tv_row_grid_phone_number);
            //view.setOnClickListener(this);
        }

       /* @Override
        public void onClick(View v) {
            for(int i=0;i<data.getDogs().size();i++){
                Log.d("###",data.getDogs().get(i).getName());
            }
        }*/

    }
}