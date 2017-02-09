package bumbums.puzzlepiece;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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

import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import io.realm.Realm;
import io.realm.RealmResults;

import static bumbums.puzzlepiece.R.id.toolbar;

public class FriendDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton fab;
    private TextView mName,mRelation,mPhone,mPuzzle,mRank,mCalendar;

    private RecyclerView mRecyclerView;
    private Realm realm;
    private Friend mFriend;
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

        initData();
        realm = Realm.getDefaultInstance();
        setUpRecyclerView();
        //mCalendar.setText(String.valueOf(friend.get));
    }

    public void initData(){
        Intent intent = getIntent();
        long id = intent.getLongExtra(RecyclerViewAdapter.EXTRA_ID,-1);
        //Log.d("###","id="+id);

        Realm realm = Realm.getDefaultInstance();
        mFriend = realm.where(Friend.class)
                .equalTo("id",id)
                .findFirst();
        mName.setText(mFriend.getName());
        mRelation.setText("("+mFriend.getRelation()+")");
        mPhone.setText(mFriend.getPhoneNumber());
        mPuzzle.setText(String.valueOf(mFriend.getPuzzleNum()));
        mRank.setText(String.valueOf(mFriend.getRank()));
        getSupportActionBar().setTitle(mFriend.getName());
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mRecyclerView.setAdapter(new PuzzleRecyclerViewAdpater(this, realm.where(Puzzle.class).findAllAsync()));
        mRecyclerView.setHasFixedSize(true);
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
                puzzle.setFriendId(mFriend.getId());
                puzzle.setText(text);
                puzzle.setDate(date);
/*                RealmResults<Friend> data = realm.where(Friend.class)
                        .equalTo("id", mFriend.getId())
                        .findAll();
                data.get(0).getPuzzles().add(puzzle);*/
                mFriend.getPuzzles().add(puzzle);
                //Log.d("###",friend.getId()+friend.getName()+friend.getPhoneNumber());
            }
        });
        Friend friend = realm.where(Friend.class).equalTo("id",mFriend.getId()).findFirst();

        Toast.makeText(this,"id="+id+" created//size:"+friend.getPuzzles().size(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab:
                addPuzzle("1234","1234");
                /*Intent intent = new Intent(this, AddPuzzleActivity.class);
                startActivity(intent);
                Log.d("###","click");*/
                break;
            default:

        }
    }

    public void deletePuzzle(long id) {

    }
}
