package bumbums.puzzlepiece.ui.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.ui.MainActivity;
import bumbums.puzzlepiece.util.CircleTransform;
import bumbums.puzzlepiece.task.FirebaseTasks;
import bumbums.puzzlepiece.util.Utils;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.ui.EditFriendActivity;
import bumbums.puzzlepiece.ui.FriendDetailActivity;
import bumbums.puzzlepiece.ui.TabFriendsFragment;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by 한승범 on 2017-02-07.
 */

public class FriendRecyclerViewAdapter  extends
        RealmRecyclerViewAdapter<Friend, FriendRecyclerViewAdapter.MyViewHolder>{
    private final MainActivity mainActivity;


    public FriendRecyclerViewAdapter(MainActivity mainActivity, OrderedRealmCollection<Friend> data) {
        super(mainActivity, data, true);
        this.mainActivity = mainActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_grid_friend, parent, false);
        itemView.setMinimumWidth(parent.getMeasuredWidth()/2);
        return new MyViewHolder(itemView);
    }

    @Override
    public void updateData(@Nullable OrderedRealmCollection<Friend> data) {
        super.updateData(data);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friend obj = getData().get(position);
        holder.data = obj;
        FirebaseTasks.loadFriendPhoto(mainActivity,holder.data,holder.userProfileImage);
       // holder.colorView.setBackgroundResource(Utils.colors[((int)obj.getId()%15)]);
        holder.userName.setText(obj.getName());
        int puzzleCount= obj.getPuzzles().size();

        //holder.lastPuzzleText.setText(lastPuzzleText);
       holder.userPuzzleNum.setText(String.valueOf(obj.getPuzzles().size()));

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener{
        public ImageView userProfileImage;
        public TextView userName;
        //public TextView lastPuzzleText;
        public CardView friendView;

       public TextView userPuzzleNum;
       // public ImageView editFriend;
        public View colorView;
        public Friend data;
        public LinearLayout selectorBack;

        public MyViewHolder(View view) {
            super(view);
            friendView = (CardView)view.findViewById(R.id.cv_friend);
            selectorBack = (LinearLayout)view.findViewById(R.id.ll_friend_back);
            userProfileImage = (ImageView)view.findViewById(R.id.iv_row_grid_profile);
            userName = (TextView)view.findViewById(R.id.tv_row_grid_name);
            userPuzzleNum = (TextView)view.findViewById(R.id.tv_row_grid_puzzle_num);
            //colorView = (View)view.findViewById(R.id.view_friend);
            //lastPuzzleText = (TextView)view.findViewById(R.id.tv_last_puzzle_text);
           // editFriend = (ImageView)view.findViewById(R.id.iv_row_grid_edit);
          //  editFriend.setOnClickListener(this);
            friendView.setOnClickListener(this);
            friendView.setOnLongClickListener(this);
            //selectorBack.setOnClickListener(this);
            //selectorBack.setOnLongClickListener(this);
            //view.setOnClickListener(this);
            //view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
              default: {
                  Intent intent = new Intent(mainActivity, FriendDetailActivity.class);
                  intent.putExtra(FriendDetailActivity.EXTRA_FRIENDID, data.getId());
                  mainActivity.startActivity(intent);
                  //Toast.makeText(tabFriendsFragment.getContext(),"click",Toast.LENGTH_SHORT).show();
                  break;
              }
            }

        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

            builder .setMessage(R.string.friend_del)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton){
                            mainActivity.deleteFriend(data.getId());
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





       /* @Override
        public void onClick(View v) {
            for(int i=0;i<data.getDogs().size();i++){
                Log.d("###",data.getDogs().get(i).getName());
            }
        }*/

    }
}
