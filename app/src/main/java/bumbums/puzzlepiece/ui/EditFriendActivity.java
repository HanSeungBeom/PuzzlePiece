package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.ui.adapter.FriendRecyclerViewAdapter;
import bumbums.puzzlepiece.model.Friend;
import io.realm.Realm;

public class EditFriendActivity extends AppCompatActivity {

    private EditText mName, mPhone, mRelation;
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String RELATION = "relation";

    private Realm realm;
    private long mId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mName = (EditText) findViewById(R.id.et_edit_friend_name);
        mPhone = (EditText) findViewById(R.id.et_edit_friend_phone);
        mRelation = (EditText) findViewById(R.id.et_edit_friend_relation);

        realm = Realm.getDefaultInstance();
        initData();
    }

    public void initData() {
        Intent intent = getIntent();
        mId = intent.getLongExtra(FriendRecyclerViewAdapter.EXTRA_ID, -1);
        //Log.d("###","id="+mId);
        Friend friend = realm.where(Friend.class)
                .equalTo(Friend.USER_ID, mId)
                .findFirst();
        mName.setText(friend.getName());
        mPhone.setText(friend.getPhoneNumber());
        mRelation.setText(friend.getRelation());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_save:
                if (mName.getText().toString().equals("")
                        || mPhone.getText().toString().equals("")
                        || mRelation.getText().toString().equals("")) {
                    Toast.makeText(this, "빈 항목이 있어요~", Toast.LENGTH_SHORT).show();
                } else {
                    final String name = mName.getText().toString();
                    final String phone = mPhone.getText().toString();
                    final String relation = mRelation.getText().toString();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Friend friend = realm.where(Friend.class).equalTo("id", mId).findFirst();
                            friend.setName(name);
                            friend.setPhoneNumber(phone);
                            friend.setRelation(relation);
                        }
                    });

                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    finish();
                }
                break;
            default:

        }

        return super.onOptionsItemSelected(item);
    }
}
