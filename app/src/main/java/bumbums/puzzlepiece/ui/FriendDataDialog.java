package bumbums.puzzlepiece.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by han sb on 2017-08-20.
 */

public class FriendDataDialog extends Dialog implements View.OnClickListener {
    private android.support.v7.app.AlertDialog mDialog;
    private Context mContext;
    private View mDialogView;
    private TextView mText;
    private TextView mTime;
    private ImageView mCall;
    private Realm realm;
    private int width;
    private LinearLayout mTextView,mEditView,mRemoveBtn,mEditBtn,mConfirm,mRemoveEdit;
    private EditText mEditText;
    private boolean isViewMode;
    private Toast mToast;
    private long mPuzzleId;
    public FriendDataDialog(Context context){
        super(context);
        mContext = context;
        LayoutInflater inflater  = getLayoutInflater();
        mDialogView = inflater.inflate(R.layout.dialog_puzzle_view,null);
        mText = (TextView)mDialogView.findViewById(R.id.tv_dialog_text);
        mTime = (TextView)mDialogView.findViewById(R.id.tv_dialog_time);
        mCall = (ImageView)mDialogView.findViewById(R.id.iv_call);
        mTextView = (LinearLayout)mDialogView.findViewById(R.id.ll_textView);
        mEditView = (LinearLayout)mDialogView.findViewById(R.id.ll_editView);
        mRemoveBtn= (LinearLayout)mDialogView.findViewById(R.id.ll_remove_btn);
        mEditBtn = (LinearLayout)mDialogView.findViewById(R.id.ll_edit_btn);
        mConfirm = (LinearLayout)mDialogView.findViewById(R.id.ll_confirm);
        mRemoveEdit =(LinearLayout)mDialogView.findViewById(R.id.ll_remove_edit_view);
        mEditText = (EditText)mDialogView.findViewById(R.id.dialog_edittext);
        mEditBtn.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mRemoveBtn.setOnClickListener(this);


        android.support.v7.app.AlertDialog.Builder builder=  new android.support.v7.app.AlertDialog.Builder(mContext,R.style.CustomDialog).
                setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            if(!isViewMode){
                                viewMode();
                                return true;

                            }
                            else {
                                dialog.cancel();
                                return true;
                            }
                        }
                        return false;
                    }
                });

        builder.setView(mDialogView);

        mDialog = builder.create();
        realm = Realm.getDefaultInstance();
        width = (int)(mContext.getResources().getDisplayMetrics().widthPixels*0.65); //<-- int width=400;

    }
    public void showData(final long puzzleId){
        mPuzzleId = puzzleId;
        viewMode();

        Puzzle puzzle = realm.where(Puzzle.class)
                .equalTo(Puzzle.PUZZLE_ID, puzzleId)
                .findFirst();

        mTime.setText(Utils.getFullFormatFromDate(puzzle.getDate()));
        String text = puzzle.getText();
        mText.setText(text);
        mTime.setText(Utils.getFullFormatFromDate(puzzle.getDate()));
        if(puzzle.getCallShow()){
            mCall.setImageResource(R.drawable.ic_phone_on);
        }
        else{
            mCall.setImageResource(R.drawable.ic_phone_off);

        }
        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final Puzzle puzzle = realm.where(Puzzle.class).equalTo(Puzzle.PUZZLE_ID, puzzleId).findFirst();
                        puzzle.setCallShow((puzzle.getCallShow())?false:true);
                        if(puzzle.getCallShow()) {

                            Activity activity = (Activity) mContext;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mToast = Toast.makeText(mContext, R.string.msg_call_setup, Toast.LENGTH_SHORT);
                                    mToast.show();
                                    mDialog.dismiss();
                                }
                            });
                        }
                        else {
                            Activity activity = (Activity)mContext;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mToast=Toast.makeText(mContext,R.string.msg_call_unsetup,Toast.LENGTH_SHORT);
                                    mToast.show();
                                    mDialog.dismiss();
                                }
                            });
                        }
                       }
                });

            }
        });

        mDialog.show();
        mDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

      //  mEditText.setText(text);
    }


    public void viewMode(){
        isViewMode = true;
        mEditView.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.VISIBLE);
        mConfirm.setVisibility(View.INVISIBLE);
        mCall.setVisibility(View.VISIBLE);
        mRemoveEdit.setVisibility(View.VISIBLE);
    }
    public void editMode(){
        isViewMode = false;
        mEditText.setText(mText.getText());
        mEditText.setSelection(mEditText.getText().length());
        mEditView.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
        mConfirm.setVisibility(View.VISIBLE);
        mCall.setVisibility(View.INVISIBLE);
        mRemoveEdit.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ll_remove_btn:
                AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                adb.setMessage(R.string.msg_del);
                adb.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Puzzle puzzle = realm.where(Puzzle.class).equalTo(Puzzle.PUZZLE_ID, mPuzzleId).findFirst();
                                puzzle.deleteFromRealm();
                            }
                        });
                        Toast.makeText(mContext,mContext.getString(R.string.deleted_memo),Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    } });
                adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    } });
                adb.show();

                break;
            case R.id.ll_edit_btn:
                Utils.showKeyboard(mContext,mEditText);
                editMode();
                break;
            case R.id.ll_confirm:
                if(mEditText.getText().toString().length()==0){
                    Toast.makeText(mContext,mContext.getString(R.string.dialog_add_hint),Toast.LENGTH_SHORT).show();
                }
                else{
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final Puzzle puzzle = realm.where(Puzzle.class).equalTo(Puzzle.PUZZLE_ID, mPuzzleId).findFirst();
                            puzzle.setText(mEditText.getText().toString());
                        }
                    });
                    mText.setText(mEditText.getText().toString());
                    viewMode();
                    Utils.hideKeyboard(mContext,mEditText);
                }

                break;
        }
    }

    public boolean getIsViewMode(){
        return isViewMode;
    }
}
