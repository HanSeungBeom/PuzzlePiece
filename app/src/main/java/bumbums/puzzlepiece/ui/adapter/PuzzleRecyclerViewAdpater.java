package bumbums.puzzlepiece.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.FriendDetailActivity;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by han sb on 2017-02-09.
 */

public class PuzzleRecyclerViewAdpater  extends
        RealmRecyclerViewAdapter<Puzzle, PuzzleRecyclerViewAdpater.MyViewHolder> {
    private final FriendDetailActivity friendDetailActivity;
    public static final String EXTRA_ID = "id";

    public PuzzleRecyclerViewAdpater(FriendDetailActivity friendDetailActivity, OrderedRealmCollection<Puzzle> data) {
        super(friendDetailActivity, data, true);
        this.friendDetailActivity = friendDetailActivity;
    }

    @Override
    public PuzzleRecyclerViewAdpater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_grid_friend_detail, parent, false);
        itemView.setMinimumWidth(parent.getMeasuredWidth()/3);


        return new PuzzleRecyclerViewAdpater.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PuzzleRecyclerViewAdpater.MyViewHolder holder, int position) {
        Puzzle obj = getData().get(position);
        holder.data = obj;
        //holder.userProfileImage =
        holder.puzzleText.setText(obj.getText());
        holder.puzzleDate.setText(obj.getDate());

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener{
        public TextView puzzleText;
        public TextView puzzleDate;
        public Puzzle data;

        public MyViewHolder(View view) {
            super(view);

            puzzleText = (TextView)view.findViewById(R.id.tv_row_grid_detail_text);
            puzzleDate = (TextView)view.findViewById(R.id.tv_row_grid_detail_date);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*Intent intent = new Intent(addPuzzleActivity,FriendDetailActivity.class);
            intent.putExtra(EXTRA_ID,data.getId());
            addPuzzleActivity.startActivity(intent);*/

            Log.d("###","click");
        }

        @Override
        public boolean onLongClick(View v) {
            friendDetailActivity.deletePuzzle(data.getId());
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
