package bumbums.puzzlepiece.ui.adapter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.ui.FriendDataDialog;
import bumbums.puzzlepiece.util.Utils;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.FriendDetailActivity;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by han sb on 2017-02-09.
 */

public class PuzzleRecyclerViewAdpater  extends
        RealmRecyclerViewAdapter<Puzzle, PuzzleRecyclerViewAdpater.MyViewHolder>  {
    private final FriendDetailActivity friendDetailActivity;
    public static final String EXTRA_PUZZLE_ID = "puzzle_id";
    private FriendDataDialog mDialog;


    public PuzzleRecyclerViewAdpater(FriendDetailActivity friendDetailActivity , OrderedRealmCollection<Puzzle> data) {
        super(friendDetailActivity, data, true);
        this.friendDetailActivity = friendDetailActivity;
        mDialog = new FriendDataDialog(friendDetailActivity);
    }

    @Override
    public PuzzleRecyclerViewAdpater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_grid_puzzle, parent, false);

        itemView.setMinimumWidth(parent.getMeasuredWidth()/3);


        return new PuzzleRecyclerViewAdpater.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PuzzleRecyclerViewAdpater.MyViewHolder holder, int position) {
        Puzzle obj = getData().get(position);
        holder.data = obj;
        //holder.userProfileImage =
        holder.puzzleText.setText(obj.getText());
        holder.puzzleDate.setText(Utils.dateToCurrentFormat(obj.getDate()));
        if(!obj.getCallShow()){
            holder.call.setVisibility(View.INVISIBLE);
        }
        else
            holder.call.setVisibility(View.VISIBLE);
        //날짜 별로 색깔 보여주기 위함.
        //holder.colorView.setBackgroundResource(Utils.colors[(Utils.getDayFromDate(obj.getDate())%15)]);

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener{
        public TextView puzzleText;
        public TextView puzzleDate;
        public View colorView;
        public Puzzle data;
        public ImageView call;

        public MyViewHolder(View view) {
            super(view);
            colorView = (View)view.findViewById(R.id.color_view);
            puzzleText = (TextView)view.findViewById(R.id.tv_row_grid_detail_text);
            puzzleDate = (TextView)view.findViewById(R.id.tv_row_grid_detail_date);
            call = (ImageView)view.findViewById(R.id.iv_call);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mDialog.showData(data.getId());

        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(friendDetailActivity);
            builder
                    .setCancelable(true)
                      .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton){
                            friendDetailActivity.deletePuzzle(data.getId());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                        // 취소 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton){
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();
            return true;
        }



    }
}
