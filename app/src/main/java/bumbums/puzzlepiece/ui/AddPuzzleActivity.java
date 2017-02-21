package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;

public class AddPuzzleActivity extends AppCompatActivity {

    public static final String EXTRA_PUZZLE_TEXT ="puzzle";
    public static final String EXTRA_PUZZLE_DATE ="date";
    public static final String EXTRA_PUZZLE_DATE_TO_MILLISECONDS = "date_to_milliseconds";
    private EditText mPuzzleText;
    private ImageView mPuzzle;
    private long mFriendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_puzzle);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mPuzzle = (ImageView)findViewById(R.id.iv_puzzle);
        mPuzzleText = (EditText)findViewById(R.id.et_add_puzzle);
        mPuzzleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mPuzzleText.length()!=0){
                    mPuzzle.setImageResource(R.drawable.puzzles_blue);
                }
                else{
                    mPuzzle.setImageResource(R.drawable.puzzles_gray);
                }
            }
        });
        Intent intent = getIntent();
        mFriendId = intent.getLongExtra(FriendDetailActivity.EXTRA_FRIENDID,-1);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_register:
                //Toast.makeText(this,"action_register_click",Toast.LENGTH_SHORT).show();
                if(mPuzzleText.length()>0) {
                    Realm realm = Realm.getDefaultInstance();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Friend friend = realm.where(Friend.class).equalTo(Friend.FRIEND_ID,mFriendId).findFirst();
                            Puzzle puzzle = realm.createObject(Puzzle.class, Utils.getNextKeyPuzzle(realm));
                            puzzle.setFriendId(mFriendId);
                            puzzle.setText(mPuzzleText.getText().toString());
                            puzzle.setFriendName(friend.getName());
                            puzzle.setDate(Utils.getNowDate());
                            puzzle.setDateToMilliSeconds(Utils.getNowDateToMilliSeconds());
                            friend.getPuzzles().add(puzzle);
                            //갱신
                            friend.setPuzzleNum(friend.getPuzzles().size());
                        }

                    });

                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    finish();
                }
                else{
                    Toast.makeText(this,"내용을 입력해 주세요",Toast.LENGTH_SHORT).show();
                }
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }
}
