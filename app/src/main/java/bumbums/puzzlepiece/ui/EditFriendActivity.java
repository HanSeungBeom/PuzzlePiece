package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import io.realm.Realm;

public class EditFriendActivity extends AppCompatActivity {
    public static final int PICK_PHONE_DATA=1;
    private EditText mName, mPhone;
    private LinearLayout mPhoneBook;

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
        mPhoneBook = (LinearLayout)findViewById(R.id.ll_phonebook);
        mPhoneBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("######","#######here");
                loadPhoneBook();
            }
        });
        realm = Realm.getDefaultInstance();
        initData();
    }

    public void initData() {
        Intent intent = getIntent();
        mId = intent.getLongExtra(FriendDetailActivity.EXTRA_FRIENDID, -1);
        //Log.d("###","id="+mId);
        Friend friend = realm.where(Friend.class)
                .equalTo(Friend.FRIEND_ID, mId)
                .findFirst();
        mName.setText(friend.getName());
        mPhone.setText(friend.getPhoneNumber());
    }

    public void loadPhoneBook() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_PHONE_DATA);
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
                      ) {
                    Toast.makeText(this, "빈 항목이 있어요~", Toast.LENGTH_SHORT).show();
                } else {
                    final String name = mName.getText().toString();
                    final String phone = mPhone.getText().toString();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Friend friend = realm.where(Friend.class).equalTo("id", mId).findFirst();
                            friend.setName(name);
                            friend.setPhoneNumber(phone);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            switch (requestCode){
                case PICK_PHONE_DATA:
                    Cursor cursor = getContentResolver().query(data.getData(),
                            new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                    cursor.moveToFirst();
                    String name = cursor.getString(0);     //0은 이름을 얻어옵니다.
                    String phone = cursor.getString(1);   //1은 번호를 받아옵니다.
                    mName.setText(name);
                    mPhone.setText(phone);
                    cursor.close();
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
