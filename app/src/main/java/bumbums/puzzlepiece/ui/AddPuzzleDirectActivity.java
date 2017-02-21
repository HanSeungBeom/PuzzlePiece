package bumbums.puzzlepiece.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.task.RealmTasks;
import bumbums.puzzlepiece.ui.adapter.FriendAddDirectAdapter;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static bumbums.puzzlepiece.ui.TabFriendsFragment.PICK_PHONE_DATA;

public class AddPuzzleDirectActivity extends AppCompatActivity implements View.OnClickListener {

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
    private LinearLayout mEmptyView;
    private RealmResults<Friend> friends;
    private Intent recordIntent;
    private SpeechRecognizer mRecognizer;
    private ImageView mRecord;
    private ImageView mNewFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_puzzle_direct);
        hideKeyboard();

        realm = Realm.getDefaultInstance();
        mFriend = null;
        mRecord = (ImageView) findViewById(R.id.iv_stt);
        mNewFriend = (ImageView)findViewById(R.id.iv_new_friend);
        mNewFriend.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_search_friend);
        mSearchTab = (LinearLayout) findViewById(R.id.ll_search_zone);
        mPuzzle = (ImageView) findViewById(R.id.iv_puzzle);
        mSearch = (EditText) findViewById(R.id.et_search);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mSearch.getText().toString().equals("")) {
                    mAdapter.updateData(realm.where(Friend.class).contains("name", mSearch.getText().toString()).findAllAsync());
                    mClear.setVisibility(View.VISIBLE);
                } else {
                    mAdapter.updateData(realm.where(Friend.class).findAllAsync());
                    mClear.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mText = (EditText) findViewById(R.id.et_add_puzzle);
        mName = (TextView) findViewById(R.id.tv_name);
        mName.setOnClickListener(this);
        mClear = (ImageView) findViewById(R.id.clear);
        mClear.setOnClickListener(this);
        mAdapter = new FriendAddDirectAdapter(this, realm.where(Friend.class).findAllAsync());
        friends = realm.where(Friend.class).findAllAsync();
        friends.addChangeListener(new RealmChangeListener<RealmResults<Friend>>() {
            @Override
            public void onChange(RealmResults<Friend> element) {
                if (element.size() == 0) {
                    showEmptyView();
                } else {
                    hideEmptyView();
                }
            }
        });

        setUpRecyclerView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        initRecord();
    }

    public void initRecord() {
        recordIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recordIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        recordIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
    }


    public void startRecord(View view) {
        mRecord.setImageResource(R.drawable.record_yellow);
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(recordIntent);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
        switch (item.getItemId()) {
            case R.id.action_register:
                if (mFriend == null)
                    Toast.makeText(this, R.string.select_friend, Toast.LENGTH_SHORT).show();
                else {
                    if (mText.equals("")) {
                        Toast.makeText(this, R.string.write_puzzle_text, Toast.LENGTH_SHORT).show();
                    } else {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                Puzzle puzzle = realm.createObject(Puzzle.class, Utils.getNextKeyPuzzle(realm));
                                puzzle.setFriendId(mFriend.getId());
                                puzzle.setText(mText.getText().toString());
                                puzzle.setFriendName(mFriend.getName());
                                puzzle.setDate(Utils.getNowDate());
                                puzzle.setDateToMilliSeconds(Utils.getNowDateToMilliSeconds());
                                mFriend.getPuzzles().add(puzzle);
                                mFriend.setPuzzleNum(mFriend.getPuzzles().size());
                            }
                        });
                        Toast.makeText(this, R.string.add_puzzle, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_name:
                if (mFriend != null) {
                    mName.setText("");
                    mFriend = null;
                    openSearchTab();
                }
                break;
            case R.id.clear:
                mSearch.setText("");
                mClear.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_new_friend:
                final CharSequence[] items = {"새로 등록하기", "전화번호부로 등록하기"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("지인 추가");
                alertDialogBuilder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                if (id == 0) { //새로등록하기
                                    {
                                        LayoutInflater inflater = getLayoutInflater();
                                        final View dialogView = inflater.inflate(R.layout.activity_add_friend, null);
                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddPuzzleDirectActivity.this);
                                        builder.setTitle("지인 추가")
                                                .setIcon(R.drawable.ic_user_puzzle)
                                                .setView(dialogView)
                                                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        EditText name = (EditText) dialogView.findViewById(R.id.et_add_friend_name);
                                                        EditText phone = (EditText) dialogView.findViewById(R.id.et_add_friend_phone);
                                                        EditText relation = (EditText) dialogView.findViewById(R.id.et_add_friend_relation);
                                                        RealmTasks.addFriend(AddPuzzleDirectActivity.this, name.getText().toString(), phone.getText().toString(), relation.getText().toString());
                                                    }
                                                })
                                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });

                                        android.app.AlertDialog dialog1 = builder.create();
                                        dialog1.setCanceledOnTouchOutside(false);
                                        dialog1.show();
                                        dialog1.getWindow().setLayout(600, 900);
                                    }
                                } else if (id == 1) { //전화번호부 등록하기
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                                    startActivityForResult(intent, PICK_PHONE_DATA);
                                }
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;

        }
    }

    public void clickFriend(Friend friend) {
        mFriend = friend;
        closeSearchTab(friend.getName());
        mText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mText, 0);
    }

    public void openSearchTab() {
        mSearchTab.setVisibility(View.VISIBLE);
        mSearch.clearFocus();
    }

    public void closeSearchTab(String name) {
        mSearch.setText("");
        mClear.setVisibility(View.INVISIBLE);
        mSearchTab.setVisibility(View.GONE);
        mName.setText(name);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_PHONE_DATA:
                    Cursor cursor = getContentResolver().query(data.getData(),
                            new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                    cursor.moveToFirst();
                    String name = cursor.getString(0);     //0은 이름을 얻어옵니다.
                    String phone = cursor.getString(1);   //1은 번호를 받아옵니다.
                    RealmTasks.addFriend(AddPuzzleDirectActivity.this, name, phone, "");
                    cursor.close();
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
    }

    public void hideEmptyView() {
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            mRecord.setImageResource(R.drawable.record_red);

        }

        @Override
        public void onBeginningOfSpeech() {
            mRecord.setImageResource(R.drawable.record_green);
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            mRecord.setImageResource(R.drawable.record_black1);
        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            mText.append(" " + rs[0]);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };
}
