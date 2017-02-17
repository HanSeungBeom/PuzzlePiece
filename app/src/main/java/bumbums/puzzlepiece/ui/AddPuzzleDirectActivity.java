package bumbums.puzzlepiece.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.ui.adapter.FriendAddDirectAdapter;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;

public class AddPuzzleDirectActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView mRecyclerView;
    private Realm realm;
    private FriendAddDirectAdapter mAdapter;
    private LinearLayout mSearchTab;
    private EditText mSearch;
    private TextView mName;
    private ImageView mClear;
    private EditText mText;
    private ImageView mPuzzle;
    private Friend mFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_puzzle_direct);
        hideKeyboard();

        realm = Realm.getDefaultInstance();
        mFriend = null;

        mRecyclerView =(RecyclerView)findViewById(R.id.rv_search_friend);
        mSearchTab = (LinearLayout)findViewById(R.id.ll_search_zone);
        mPuzzle = (ImageView)findViewById(R.id.iv_puzzle);
        mSearch=(EditText)findViewById(R.id.et_search);
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mSearch.getText().toString().equals("")){
                    mAdapter.updateData(realm.where(Friend.class).contains("name", mSearch.getText().toString()).findAllAsync());
                    mClear.setVisibility(View.VISIBLE);
                }
                else{
                    mAdapter.updateData(realm.where(Friend.class).findAllAsync());
                    mClear.setVisibility(View.INVISIBLE);
                }

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mText = (EditText) findViewById(R.id.et_add_puzzle);
        mText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mText.length()!=0){
                    mPuzzle.setImageResource(R.drawable.puzzles_blue);
                }
                else{
                    mPuzzle.setImageResource(R.drawable.puzzles_gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mName = (TextView)findViewById(R.id.tv_name);
        mName.setOnClickListener(this);
        mClear =(ImageView)findViewById(R.id.clear);
        mClear.setOnClickListener(this);
        mAdapter = new FriendAddDirectAdapter(this, realm.where(Friend.class).findAllAsync());
        setUpRecyclerView();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

public void hideKeyboard(){
    View view = this.getCurrentFocus();
    if (view != null) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_register:
                if(mFriend==null)
                    Toast.makeText(this,R.string.select_friend,Toast.LENGTH_SHORT).show();
                else{
                    if(mText.equals("")){
                        Toast.makeText(this,R.string.write_puzzle_text,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                Puzzle puzzle = realm.createObject(Puzzle.class,Utils.getNextKeyPuzzle(realm));
                                puzzle.setFriendId(mFriend.getId());
                                puzzle.setText(mText.getText().toString());
                                puzzle.setFriendName(mFriend.getName());
                                puzzle.setDate(Utils.getNowDate());
                                puzzle.setDateToMilliSeconds(Utils.getNowDateToMilliSeconds());
                                mFriend.getPuzzles().add(puzzle);
                                mFriend.setPuzzleNum(mFriend.getPuzzles().size());
                            }
                        });
                        Toast.makeText(this,R.string.add_puzzle,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_name:
                if(mFriend!=null){
                    mName.setText("");
                    mFriend = null;
                    openSearchTab();
                }
                break;
            case R.id.clear:
                mSearch.setText("");
                mClear.setVisibility(View.INVISIBLE);

                //TODO 키보드 숨기기 안됨..
                break;
        }
    }

    public void clickFriend(Friend friend){
        mFriend = friend;
        closeSearchTab(friend.getName());
        //mText.requestFocus();
    }

    public void openSearchTab(){
        mSearchTab.setVisibility(View.VISIBLE);
        mSearch.clearFocus();
    }
    public void closeSearchTab(String name){
        mSearch.setText("");
        mClear.setVisibility(View.INVISIBLE);
        mSearchTab.animate()
                .translationY(mSearchTab.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mSearchTab.setVisibility(View.GONE);
                    }
                });

        mName.setText(name);
    }
}
