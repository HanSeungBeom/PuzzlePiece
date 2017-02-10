package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.ui.adapter.FriendRecyclerViewAdapter;
import bumbums.puzzlepiece.Utils;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

/**
 * Created by han sb on 2017-02-08.
 */

public class TabFriendsFragment extends android.support.v4.app.Fragment implements View.OnClickListener,
MainActivity.onKeyBackPressedListener{
    public static final int PICK_PHONE_DATA=1;
    public static final int ADD_FRIEND =2;
    private RecyclerView mRecyclerView;
    private Realm realm;
    private FloatingActionsMenu fab;
    private com.getbase.floatingactionbutton.FloatingActionButton mFabNew,mFabLoadPhoneBook;
    private FriendRecyclerViewAdapter mAdapter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        mAdapter =new FriendRecyclerViewAdapter(this, realm.where(Friend.class).findAllAsync());
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_friends, container,false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rv_friends);
        setUpRecyclerView();
        //ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.dimen4);
       // mRecyclerView.addItemDecoration(itemDecoration);
        fab = (FloatingActionsMenu)view.findViewById(R.id.fab);
        mFabNew =(com.getbase.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.fab_new_register);
        mFabLoadPhoneBook=(com.getbase.floatingactionbutton.FloatingActionButton)view.findViewById(R.id.fab_load_phonebook);
        mFabNew.setOnClickListener(this);
        mFabLoadPhoneBook.setOnClickListener(this);

        return view;
    }

    private void setUpRecyclerView() {


        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    public void loadPhoneBook() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_PHONE_DATA);
    }

    public void deleteFriend(final long id){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Friend> rows = realm.where(Friend.class).equalTo(Friend.USER_ID,id).findAll();
                rows.deleteAllFromRealm();

                RealmResults<Puzzle> puzzlesWithFriend =realm.where(Puzzle.class).equalTo(Puzzle.FRIEND_ID,id).findAll();
                puzzlesWithFriend.deleteAllFromRealm();
            }
        });

        Toast.makeText(getContext(),"id="+id+" deleted",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab_new_register:
                Intent intent = new Intent(getContext(),AddFriendActivity.class);
                startActivityForResult(intent,ADD_FRIEND);
                fab.collapse();
                break;
            case R.id.fab_load_phonebook:
                loadPhoneBook();
                fab.collapse();
                break;
            default:

        }
    }

    public void addFriend(final String name, final String phone, final String relation){
        final long id = Utils.getNextKeyFriend(realm);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Friend friend = realm.createObject(Friend.class, id);
                friend.setName(name);
                friend.setPhoneNumber(phone);
                friend.setRelation(relation);

                //Log.d("###",friend.getId()+friend.getName()+friend.getPhoneNumber());
            }
        });
        Toast.makeText(getContext(),"id="+id+" created",Toast.LENGTH_SHORT).show();
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
                    String name = cursor.getString(0);     //0은 이름을 얻어옵니다.
                    String phone = cursor.getString(1);   //1은 번호를 받아옵니다.
                    addFriend(name,phone,"null");
                    cursor.close();
                    break;
                case ADD_FRIEND:
                    String addname= data.getStringExtra(AddFriendActivity.NAME);
                    String addphone= data.getStringExtra(AddFriendActivity.PHONE);
                    String addrelation= data.getStringExtra(AddFriendActivity.RELATION);
                    addFriend(addname,addphone,addrelation);
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onPause() {
        fab.collapse();

        super.onPause();
    }

    @Override
    public void onBack() {
        if (fab.isExpanded()) {
            fab.collapse();
        } else {
            MainActivity activity = (MainActivity) getActivity();
            activity.setOnKeyBackPressedListener(null);
            activity.onBackPressed();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).setOnKeyBackPressedListener(this);
    }


}
