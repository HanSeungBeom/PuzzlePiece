package bumbums.puzzlepiece.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import bumbums.puzzlepiece.ui.adapter.FriendRecyclerViewAdapter;
import bumbums.puzzlepiece.ui.adapter.PuzzleRecyclerViewAdpater;
import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.Utils;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class FriendDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton fab;
    private TextView mName,mRelation,mPhone,mPuzzle,mRank,mCalendar;

    private RecyclerView mRecyclerView;
    private Realm realm;
    private Friend mFriend;
    private long mFriendId;

    public static final int REQUESTCODE_PUZZLE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        mName=(TextView)findViewById(R.id.tv_detail_name);
        mRelation=(TextView)findViewById(R.id.tv_detail_relation);
        mPhone=(TextView)findViewById(R.id.tv_detail_phone);
        mPuzzle=(TextView)findViewById(R.id.tv_detail_puzzle);
        mRank=(TextView)findViewById(R.id.tv_detail_rank);
        mCalendar=(TextView)findViewById(R.id.tv_detail_calendar);
        mRecyclerView=(RecyclerView)findViewById(R.id.rv_friend_detail);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        realm = Realm.getDefaultInstance();

        initData();

        setUpRecyclerView();
        //mCalendar.setText(String.valueOf(friend.get));
    }

    public void initData(){
        Intent intent = getIntent();
        long id = intent.getLongExtra(FriendRecyclerViewAdapter.EXTRA_ID,-1);
        //Log.d("###","id="+id);

        mFriend = realm.where(Friend.class)
                .equalTo("id",id)
                .findFirst();
        mFriend.addChangeListener(new RealmChangeListener<Friend>() {
            @Override
            public void onChange(Friend friend) {
                mPuzzle.setText(String.valueOf(friend.getPuzzles().size()));
            }
        });
        mFriendId= mFriend.getId();
        mName.setText(mFriend.getName());
        mRelation.setText("("+mFriend.getRelation()+")");
        mPhone.setText(mFriend.getPhoneNumber());
        mPuzzle.setText(String.valueOf(mFriend.getPuzzles().size()));
        mRank.setText(String.valueOf(mFriend.getRank()));
        getSupportActionBar().setTitle(mFriend.getName());
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mRecyclerView.setAdapter(new PuzzleRecyclerViewAdpater(this, realm.where(Puzzle.class).equalTo(Puzzle.FRIEND_ID,mFriendId).findAllAsync()));
        mRecyclerView.setHasFixedSize(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_edit:
                Toast.makeText(this,"action_edit_click",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_delete:
                Toast.makeText(this,"action_edit_click",Toast.LENGTH_SHORT).show();
                break;
            default:

        }

        return super.onOptionsItemSelected(item);
    }

    public void addPuzzle(final String text, final String date){
        final long id = Utils.getNextKeyPuzzle(realm);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Puzzle puzzle = realm.createObject(Puzzle.class, id);
                puzzle.setFriendId(mFriendId);
                puzzle.setText(text);
                puzzle.setDate(date);
                puzzle.setFriendName(mName.getText().toString());
/*                RealmResults<Friend> data = realm.where(Friend.class)
                        .equalTo("id", mFriend.getId())
                        .findAll();
                data.get(0).getPuzzles().add(puzzle);*/

                Friend friend =realm.where(Friend.class).equalTo("id",mFriendId).findFirst();
                friend.getPuzzles().add(puzzle);

            }},new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d("##","success");
            }


        });
        Toast.makeText(this,"id="+id+" created",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab:

               Intent intent = new Intent(this, AddPuzzleActivity.class);
                startActivityForResult(intent,REQUESTCODE_PUZZLE);
                //Log.d("###","click");
                break;
            default:

        }
    }

    public void deletePuzzle(final long id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Puzzle> rows = realm.where(Puzzle.class).equalTo(Puzzle.PUZZLE_ID,id).findAll();
                rows.deleteAllFromRealm();
            }
        });
       // Toast.makeText(this,"id="+id+" deleted",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case REQUESTCODE_PUZZLE:
                    String text = data.getStringExtra(AddPuzzleActivity.EXTRA_PUZZLE_TEXT);
                    String date = data.getStringExtra(AddPuzzleActivity.EXTRA_PUZZLE_DATE);
                    addPuzzle(text,date);
                    break;
                default:

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
