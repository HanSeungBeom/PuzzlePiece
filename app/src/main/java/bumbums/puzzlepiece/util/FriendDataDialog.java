package bumbums.puzzlepiece.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import io.realm.Realm;

/**
 * Created by han sb on 2017-08-20.
 */

public class FriendDataDialog extends Dialog {
    private AlertDialog mDialog;
    private Context mContext;
    private View mDialogView;
    private TextView mText;
    private TextView mTime;
    private Realm realm;
    private int width;
    public FriendDataDialog(Context context){
        super(context);
        mContext = context;
        LayoutInflater inflater  = getLayoutInflater();
        mDialogView = inflater.inflate(R.layout.dialog_puzzle_view,null);
        mText = (TextView)mDialogView.findViewById(R.id.tv_dialog_text);
        mTime = (TextView)mDialogView.findViewById(R.id.tv_dialog_time);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(mDialogView);
        mDialog = builder.create();
        realm = Realm.getDefaultInstance();
        width = (int)(mContext.getResources().getDisplayMetrics().widthPixels*0.75); //<-- int width=400;
    }
    public void showData(long puzzleId){

        Puzzle puzzle = realm.where(Puzzle.class)
                .equalTo(Puzzle.PUZZLE_ID, puzzleId)
                .findFirst();

        mTime.setText(Utils.getFullFormatFromDate(puzzle.getDate()));
        String text = puzzle.getText();
        mText.setText(text);
        mTime.setText(Utils.getFullFormatFromDate(puzzle.getDate()));
        mDialog.show();
        mDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
      //  mEditText.setText(text);
    }


}
