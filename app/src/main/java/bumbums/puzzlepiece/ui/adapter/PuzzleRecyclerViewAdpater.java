package bumbums.puzzlepiece.ui.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.ui.TabPuzzlesFragment;
import bumbums.puzzlepiece.util.Utils;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.FriendDetailActivity;
import bumbums.puzzlepiece.ui.PuzzleDetailActivity;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by han sb on 2017-02-09.
 */

public class PuzzleRecyclerViewAdpater  extends
        RealmRecyclerViewAdapter<Puzzle, PuzzleRecyclerViewAdpater.MyViewHolder> {
    private final TabPuzzlesFragment tabPuzzlesFragment;
    public static final String EXTRA_PUZZLE_ID = "puzzle_id";


    public PuzzleRecyclerViewAdpater(TabPuzzlesFragment tabPuzzlesFragment, OrderedRealmCollection<Puzzle> data) {
        super(tabPuzzlesFragment.getContext(), data, true);
        this.tabPuzzlesFragment = tabPuzzlesFragment;
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
        holder.colorView.setBackgroundResource(Utils.colors[((int)obj.getId()%15)]);

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener{
        public TextView puzzleText;
        public TextView puzzleDate;
        public View colorView;
        public Puzzle data;

        public MyViewHolder(View view) {
            super(view);
            colorView = (View)view.findViewById(R.id.color_view);
            puzzleText = (TextView)view.findViewById(R.id.tv_row_grid_detail_text);
            puzzleDate = (TextView)view.findViewById(R.id.tv_row_grid_detail_date);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(tabPuzzlesFragment.getContext(),PuzzleDetailActivity.class);
            intent.putExtra(EXTRA_PUZZLE_ID,data.getId());
            intent.putExtra(FriendDetailActivity.EXTRA_FRIENDID,data.getFriendId());
            tabPuzzlesFragment.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(tabPuzzlesFragment.getContext());
            builder.setTitle("퍼즐 삭제")
                    .setMessage("지인의 퍼즐을 삭제 하시겠습니까?")
                    .setCancelable(false)
                    .setIcon(R.drawable.puzzles_black)

                    .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton){
                            tabPuzzlesFragment.deletePuzzle(data.getId());
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener(){
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
