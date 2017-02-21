package bumbums.puzzlepiece.ui.adapter;

/**
 * Created by han sb on 2017-02-18.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.task.FirebaseTasks;
import bumbums.puzzlepiece.ui.AddPuzzleDirectActivity;
import bumbums.puzzlepiece.ui.TabReviewFragment;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


/**
 * Created by han sb on 2017-02-17.
 */

/**
 * Created by 한승범 on 2017-02-07.
 */

public class FriendAddDirectAdapter  extends
        RealmRecyclerViewAdapter<Friend, FriendAddDirectAdapter.MyViewHolder> {
    private final AddPuzzleDirectActivity addPuzzleDirectActivity;
    public FriendAddDirectAdapter(AddPuzzleDirectActivity addPuzzleDirectActivity, OrderedRealmCollection<Friend> data) {
        super(addPuzzleDirectActivity, data, true);
        this.addPuzzleDirectActivity = addPuzzleDirectActivity;

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
        FirebaseTasks.loadFriendPhoto(addPuzzleDirectActivity,holder.data,holder.userProfileImage);
        // holder.colorView.setBackgroundResource(Utils.colors[((int)obj.getId()%15)]);
        holder.userName.setText(obj.getName());

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {

        public ImageView userProfileImage;
        public TextView userName;
        public LinearLayout border;


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
            addPuzzleDirectActivity.clickFriend(data);

        }






    }
}
