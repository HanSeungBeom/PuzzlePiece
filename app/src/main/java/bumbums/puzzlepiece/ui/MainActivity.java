package bumbums.puzzlepiece.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.getbase.floatingactionbutton.FloatingActionsMenu;

import bumbums.puzzlepiece.R;

import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.task.FirebaseTasks;
import bumbums.puzzlepiece.task.RealmTasks;
import bumbums.puzzlepiece.ui.adapter.FriendRecyclerViewAdapter;
import bumbums.puzzlepiece.ui.adapter.TabAdapter;
import bumbums.puzzlepiece.util.BackPressCloseHandler;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  //  private TabLayout mTabLayout;
  //  private ViewPager mViewPager;
    private TextView mFriendNum;
    private TextView mTitle;
    private Realm realm;
  //  private TabAdapter mAdapter;
    private RealmResults<Friend> results;
    private ImageView mSettingBtn;
    public static MainActivity mMainActivity;
    private BackPressCloseHandler backPressCloseHandler;

    public static final int PICK_PHONE_DATA=1;
    private RecyclerView mRecyclerView;
    private FloatingActionsMenu fab;
    private com.getbase.floatingactionbutton.FloatingActionButton mFabNew,mFabLoadPhoneBook;
    private FriendRecyclerViewAdapter mAdapter;
    private EditText mSearchText;
    private ImageView mClear;
    private LinearLayout mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();
        results = realm.where(Friend.class)
                .findAllAsync();
        results.addChangeListener(new RealmChangeListener<RealmResults<Friend>>() {
            @Override
            public void onChange(RealmResults<Friend> element) {
                mFriendNum.setText(String.valueOf(element.size()));
                if(element.size()==0){
                    showEmptyView();
                }
                else{
                    hideEmptyView();
                }
            }
        });
        mSettingBtn= (ImageView)findViewById(R.id.iv_setting);
        mSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(i);
            }
        });

        mMainActivity = this;


        mAdapter =new FriendRecyclerViewAdapter(this, realm.where(Friend.class).findAllAsync());
        mClear = (ImageView)findViewById(R.id.clear);
        mClear.setOnClickListener(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_friends);
        mEmptyView = (LinearLayout)findViewById(R.id.empty_view);
        setUpRecyclerView();

        fab = (FloatingActionsMenu)findViewById(R.id.fab);
        mFabNew =(com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.fab_new_register);
        mFabLoadPhoneBook=(com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.fab_load_phonebook);
        mFabNew.setOnClickListener(this);
        mFabLoadPhoneBook.setOnClickListener(this);
        mSearchText = (EditText)findViewById(R.id.et_search);
        mSearchText.clearFocus();
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mSearchText.getText().toString().equals("")){
                    mAdapter.updateData(realm.where(Friend.class).contains(Friend.FRIEND_NAME, mSearchText.getText().toString()).findAllAsync());
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
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Utils.hideKeyboard(mMainActivity,mSearchText);
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        /* 뷰페이저 삭제 170825
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mAdapter = new TabAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new TabFriendsFragment());
        mAdapter.addFragment(new TabGraphFragment());
        mAdapter.addFragment(new TabMainRankFragment());
        mAdapter.addFragment(new TabReviewFragment());

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


        mTabLayout.getTabAt(0).setIcon(R.drawable.friends_selector);
        mTabLayout.getTabAt(1).setIcon(R.drawable.graph_selector);
        mTabLayout.getTabAt(2).setIcon(R.drawable.rank_selector);
        mTabLayout.getTabAt(3).setIcon(R.drawable.review_selector);

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.addOnTabSelectedListener(this);
        */

        mFriendNum = (TextView) findViewById(R.id.tv_friend_num);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mTitle = (TextView) findViewById(R.id.tv_title);
        backPressCloseHandler = new BackPressCloseHandler(this);
    }


    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }
    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab_new_register: {
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.activity_add_friend, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setView(dialogView)
                        .setCancelable(true)
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText name = (EditText) dialogView.findViewById(R.id.et_add_friend_name);
                                EditText phone = (EditText) dialogView.findViewById(R.id.et_add_friend_phone);
                                RealmTasks.addFriend(mMainActivity,name.getText().toString(), phone.getText().toString());
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }
            fab.collapse();
            break;
            case R.id.fab_load_phonebook:
                loadPhoneBook();
                fab.collapse();
                break;
            case R.id.clear:
                mSearchText.setText("");
                mClear.setVisibility(View.INVISIBLE);


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
                    Cursor cursor = getContentResolver().query(data.getData(),
                            new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                    cursor.moveToFirst();
                    String name = cursor.getString(0);     //0은 이름을 얻어옵니다.
                    String phone = cursor.getString(1);   //1은 번호를 받아옵니다.
                    RealmTasks.addFriend(this,name,phone);
                    cursor.close();
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadPhoneBook() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_PHONE_DATA);
    }


    @Override
    protected void onResume() {
        super.onResume();


    }
/*

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                mTitle.setText("지인");
                mFriendNum.setVisibility(View.VISIBLE);
                break;
            case 1:
                Utils.hideKeyboard(this);
                mTitle.setText("퍼즐 그래프");
                mFriendNum.setVisibility(View.INVISIBLE);
                break;
            case 2:
                Utils.hideKeyboard(this);
                mTitle.setText("퍼즐 랭킹");
                mFriendNum.setVisibility(View.INVISIBLE);
                break;
            case 3:
                Utils.hideKeyboard(this);
                mTitle.setText("오늘의 퍼즐");
                mFriendNum.setVisibility(View.INVISIBLE);
                break;

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
*/

    public interface onKeyBackPressedListener {
        public void onBack();
    }



    @Override
    public void onBackPressed() {
        if (fab.isExpanded()) {
            fab.collapse();
            return;
        } else {
            super.onBackPressed();
        }
    }

    public void deleteFriend(final long id){
        FirebaseTasks.deletePhoto(this,id);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<Friend> rows = realm.where(Friend.class).equalTo(Friend.FRIEND_ID,id).findAll();
                rows.deleteAllFromRealm();

                RealmResults<Puzzle> puzzlesWithFriend =realm.where(Puzzle.class).equalTo(Puzzle.FRIEND_ID,id).findAll();
                puzzlesWithFriend.deleteAllFromRealm();
            }
        });

        //Toast.makeText(getContext(),"id="+id+" deleted",Toast.LENGTH_SHORT).show();
    }

    public void showEmptyView(){
        mEmptyView.setVisibility(View.VISIBLE);
    }
    public void hideEmptyView(){
        mEmptyView.setVisibility(View.INVISIBLE);
    }
}
