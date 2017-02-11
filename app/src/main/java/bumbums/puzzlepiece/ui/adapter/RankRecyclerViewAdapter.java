package bumbums.puzzlepiece.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.ui.RankFragment;
import bumbums.puzzlepiece.ui.TabStatisticsFragment;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by 한승범 on 2017-02-11.
 */

public class RankRecyclerViewAdapter  extends
        RealmRecyclerViewAdapter<Friend, RankRecyclerViewAdapter.MyViewHolder> {

    private RankFragment rankFragment;

    public RankRecyclerViewAdapter(RankFragment rankFragment, OrderedRealmCollection<Friend> data) {
        super(rankFragment.getContext(), data, true);
        this.rankFragment = rankFragment;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_friend_rank, parent, false);
        //itemView.setMinimumWidth(parent.getMeasuredWidth()/2);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friend obj = getData().get(position);
        holder.data = obj;

        holder.rank.setText(String.valueOf(position+1));
        holder.name.setText(obj.getName());
        holder.puzzleNum.setText(String.valueOf(obj.getPuzzleNum()));

     /*   holder.bg.setBackgroundResource(Utils.colors[((int)obj.getFriendId())%15]);

        holder.name.setText(obj.getFriendName());
        holder.time.setText(Utils.dateToCurrentFormat(obj.getDate()));
        holder.text.setText(obj.getText());*/

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener{

        public TextView rank;
        public TextView name;
        public TextView puzzleNum;
        public ImageView userprofile;
        public ImageView updownImage;
        public TextView rankdif;

        public Friend data;

        public MyViewHolder(View view) {
            super(view);

            rank = (TextView)view.findViewById(R.id.tv_row_rank);
            name = (TextView)view.findViewById(R.id.tv_row_rank_name);
            puzzleNum = (TextView)view.findViewById(R.id.tv_row_rank_puzzle_num);
            userprofile = (ImageView)view.findViewById(R.id.iv_rank_user_profile);
            updownImage = (ImageView)view.findViewById(R.id.iv_rank_updown);
            rankdif = (TextView)view.findViewById(R.id.tv_rank_updown);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
               /*  Intent intent = new Intent(tabFriendsFragment.getContext(),FriendDetailActivity.class);
                    intent.putExtra(EXTRA_ID,data.getId());
                    tabFriendsFragment.getContext().startActivity(intent);*/
            // Log.d("###","click");
        }
        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    }

}
