package bumbums.puzzlepiece;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import bumbums.puzzlepiece.model.Friend;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    public static final int PICK_PHONE_DATA=1;
    private RecyclerView mRecyclerView;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_friends);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.dimen8);
        mRecyclerView.addItemDecoration(itemDecoration);
        realm = Realm.getDefaultInstance();
        setUpRecyclerView();
    }

    public void addFriend(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_PHONE_DATA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            switch (requestCode){
                case PICK_PHONE_DATA:
                    Cursor cursor = getContentResolver().query(data.getData(),
                            new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                    cursor.moveToFirst();
                    final String name = cursor.getString(0);     //0은 이름을 얻어옵니다.
                    final String number = cursor.getString(1);   //1은 번호를 받아옵니다.


                    final String timestamp = Long.toString(System.currentTimeMillis());
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Friend friend = realm.createObject(Friend.class,Utils.getNextKey(realm));
                            friend.setName(name);
                            friend.setPhoneNumber(number);
                            Log.d("###",friend.getId()+friend.getName()+friend.getPhoneNumber());
                        }
                    });
                    cursor.close();
                    break;
                default:

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setAdapter(new RecyclerViewAdapter(this, realm.where(Friend.class).findAllAsync()));
        mRecyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }



}
