package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;

public class AddPuzzleActivity extends AppCompatActivity {

    private EditText mPuzzleText;
    private ImageView mPuzzle;
    private long mFriendId;
    private Intent recordIntent;
    private SpeechRecognizer mRecognizer;
    private ImageView mRecord;
    private Toast mToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_puzzle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mPuzzle = (ImageView) findViewById(R.id.iv_puzzle);
        mRecord = (ImageView)findViewById(R.id.iv_stt);
        mPuzzleText = (EditText) findViewById(R.id.et_add_puzzle);
        mPuzzleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mPuzzleText.length() != 0) {
                    mPuzzle.setImageResource(R.drawable.puzzles_blue);
                } else {
                    mPuzzle.setImageResource(R.drawable.puzzles_gray);
                }
            }
        });
        Intent intent = getIntent();
        mFriendId = intent.getLongExtra(FriendDetailActivity.EXTRA_FRIENDID, -1);
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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_register:
                //Toast.makeText(this,"action_register_click",Toast.LENGTH_SHORT).show();
                if (mPuzzleText.length() > 0) {
                    Realm realm = Realm.getDefaultInstance();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Friend friend = realm.where(Friend.class).equalTo(Friend.FRIEND_ID, mFriendId).findFirst();
                            Puzzle puzzle = realm.createObject(Puzzle.class, Utils.getNextKeyPuzzle(realm));
                            puzzle.setFriendId(mFriendId);
                            puzzle.setText(mPuzzleText.getText().toString());
                            puzzle.setFriendName(friend.getName());
                            puzzle.setDate(Utils.getNowDate());
                            puzzle.setDateToMilliSeconds(Utils.getNowDateToMilliSeconds());
                            friend.getPuzzles().add(puzzle);
                            //갱신
                            friend.setPuzzleNum(friend.getPuzzles().size());
                        }

                    });

                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    finish();
                } else {
                    Toast.makeText(this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            mRecord.setImageResource(R.drawable.record_red);
            if(mToast!=null)
                mToast.cancel();
            mToast=Toast.makeText(AddPuzzleActivity.this,"말씀해주세요~",Toast.LENGTH_SHORT);
            mToast.show();
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
            if(mToast!=null)
                mToast.cancel();
            mToast=Toast.makeText(AddPuzzleActivity.this,"에러가 발생하였습니다. 다시 시도해주세요.",Toast.LENGTH_SHORT);
            mToast.show();
        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            mPuzzleText.append(" " + rs[0]);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };

}
