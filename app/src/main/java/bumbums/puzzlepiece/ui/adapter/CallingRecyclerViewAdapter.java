package bumbums.puzzlepiece.ui.adapter;

import android.content.Context;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;

import bumbums.puzzlepiece.util.Utils;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by han sb on 2017-08-25.
 */

public class CallingRecyclerViewAdapter  extends
        RealmRecyclerViewAdapter<Puzzle, CallingRecyclerViewAdapter.MyViewHolder> {
    private final Context mContext;
    public CallingRecyclerViewAdapter(Context context, OrderedRealmCollection<Puzzle> data){
        super(context,data,true);
        this.mContext = context;
    }
    @Override
    public void updateData(@Nullable OrderedRealmCollection<Puzzle> data) {
        super.updateData(data);
    }

    @Override
    public CallingRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_calling_view, parent, false);
        //itemView.setMinimumWidth(parent.getMeasuredWidth()/2);
        return new CallingRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CallingRecyclerViewAdapter.MyViewHolder holder, int position) {
        Puzzle obj = getData().get(position);
        holder.data = obj;
        Realm realm = Realm.getDefaultInstance();
//        holder.colorView.setBackgroundResource(Utils.colors[((int)obj.getFriendId())%15]);

        holder.text.setText(obj.getText());
        holder.time.setText(Utils.dateToCurrentFormat(obj.getDate()));

    }
    class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener{

        public TextView text;
        public TextView time;
        public Puzzle data;

        //public View colorView;
        public MyViewHolder(View view) {
            super(view);

            text = (TextView)view.findViewById(R.id.tv_row_text);
            time = (TextView)view.findViewById(R.id.tv_row_time);



        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {

            return true;
        }




    }
}
