package bumbums.puzzlepiece.tab;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bumbums.puzzlepiece.ItemOffsetDecoration;
import bumbums.puzzlepiece.MainActivity;
import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.RecyclerViewAdapter;
import bumbums.puzzlepiece.Utils;
import bumbums.puzzlepiece.model.Friend;
import io.realm.Realm;

import static android.app.Activity.RESULT_OK;

/**
 * Created by han sb on 2017-02-08.
 */

public class TabFriends extends android.support.v4.app.Fragment implements View.OnClickListener {
    public static final int PICK_PHONE_DATA=1;
    private RecyclerView mRecyclerView;
    private Realm realm;
    private FloatingActionButton mFab;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_friends, container,false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.rv_friends);
        //ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.dimen4);
       // mRecyclerView.addItemDecoration(itemDecoration);
        mFab = (FloatingActionButton)view.findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        realm = Realm.getDefaultInstance();
        setUpRecyclerView();


        return view;
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.setAdapter(new RecyclerViewAdapter((MainActivity)getActivity(), realm.where(Friend.class).findAllAsync()));
        mRecyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    public void addFriend() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_PHONE_DATA);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
                addFriend();
                break;
            default:

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            switch (requestCode){
                case PICK_PHONE_DATA:
                    Cursor cursor = getActivity().getContentResolver().query(data.getData(),
                            new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                    cursor.moveToFirst();
                    final String name = cursor.getString(0);     //0은 이름을 얻어옵니다.
                    final String number = cursor.getString(1);   //1은 번호를 받아옵니다.

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Friend friend = realm.createObject(Friend.class, Utils.getNextKey(realm));
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

}
