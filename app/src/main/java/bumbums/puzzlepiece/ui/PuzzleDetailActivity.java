package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.adapter.PuzzleRecyclerViewAdpater;
import io.realm.Realm;

public class PuzzleDetailActivity extends AppCompatActivity {

    private Realm realm;
    private long mFriendId,mPuzzleId;
    private TextView mTextView;
    private EditText mEditText;
    private TextView mName;
    private TextView mTime;

    private MenuItem mModify, mCommit;
    private boolean isViewMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mTextView = (TextView)findViewById(R.id.tv_view_puzzle);
        mEditText = (EditText)findViewById(R.id.et_edit_puzle);
        mName = (TextView)findViewById(R.id.tv_puzzle_name);
        mTime = (TextView)findViewById(R.id.tv_puzzle_time);

        realm = Realm.getDefaultInstance();
        isViewMode = true;
        initData();


    }

    public void initData() {
        Intent intent = getIntent();
        mFriendId = intent.getLongExtra(PuzzleRecyclerViewAdpater.EXTRA_FRIEND_ID,-1);
       // Log.d("###","id="+mFriendId);
        mPuzzleId = intent.getLongExtra(PuzzleRecyclerViewAdpater.EXTRA_PUZZLE_ID,-1);
        //Log.d("###","id="+mId);
        Friend friend = realm.where(Friend.class)
                .equalTo(Friend.USER_ID, mFriendId)
                .findFirst();

        Puzzle puzzle = realm.where(Puzzle.class)
                .equalTo(Puzzle.PUZZLE_ID, mPuzzleId)
                .findFirst();

        mName.setText(friend.getName());
        mTime.setText(puzzle.getDate());
        String text = puzzle.getText();
        mTextView.setText(text);
        mEditText.setText(text);
    }
    public void setModeView(){
        mTextView.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.GONE);
        mModify.setVisible(true);
        mCommit.setVisible(false);
        isViewMode = true;

    }
    public void setModeEdit(){
        mTextView.setVisibility(View.GONE);
        mEditText.setText(mTextView.getText().toString());
        mEditText.setVisibility(View.VISIBLE);

        mModify.setVisible(false);
        mCommit.setVisible(true);
        isViewMode = false;

        //키보드 보여주기
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, 0);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_puzzle_detail, menu);
        mModify = menu.findItem(R.id.action_modify);
        mCommit = menu.findItem(R.id.action_commit);
        setModeView();
        return true;
    }

    public void modifyPuzzle(){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Puzzle puzzle = realm.where(Puzzle.class).equalTo(Puzzle.PUZZLE_ID,mPuzzleId).findFirst();
                String modifyText = mEditText.getText().toString();
                puzzle.setText(modifyText);
            }
        });
        mTextView.setText(mEditText.getText().toString());
        Toast.makeText(this,"내용을 변경하였습니다.",Toast.LENGTH_SHORT).show();

        //키보드 숨기기
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
           case R.id.action_modify:
               setModeEdit();
               break;
            case R.id.action_commit:
                modifyPuzzle();
                setModeView();
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!isViewMode){
            setModeView();
            return;
        }
        super.onBackPressed();
    }
}
