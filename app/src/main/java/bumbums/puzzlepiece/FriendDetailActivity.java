package bumbums.puzzlepiece;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import bumbums.puzzlepiece.model.Friend;
import io.realm.Realm;
import io.realm.RealmResults;

import static bumbums.puzzlepiece.R.id.toolbar;

public class FriendDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton fab;
    private TextView mName,mRelation,mPhone,mPuzzle,mRank,mCalendar;

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


        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("한승범");
        Intent intent = getIntent();
        long id = intent.getLongExtra(RecyclerViewAdapter.EXTRA_ID,-1);
        //Log.d("###","id="+id);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Friend> data = realm.where(Friend.class)
                .equalTo("id", id)
                .findAll();
        Friend friend = data.get(0);
        mName.setText(friend.getName());
        mRelation.setText("("+friend.getRelation()+")");
        mPhone.setText(friend.getPhoneNumber());
        mPuzzle.setText(String.valueOf(friend.getPuzzleNum()));
        mRank.setText(String.valueOf(friend.getRank()));
        //mCalendar.setText(String.valueOf(friend.get));
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab:
                Intent intent = new Intent(this, AddPuzzleActivity.class);
                startActivity(intent);
                Log.d("###","click");
                break;
            default:

        }
    }
}
