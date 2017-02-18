package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.List;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import bumbums.puzzlepiece.util.AudioWriterPCM;
import bumbums.puzzlepiece.util.NaverRecognizer;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;

public class AddPuzzleActivity extends AppCompatActivity {

    //TODO PERMISSION
    private EditText mPuzzleText;
    private ImageView mPuzzle;
    private long mFriendId;
    private static final String CLIENT_ID = "g2FxRj3dmttVKEjkwG0Y";
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private Button btnStart;
    private String mResult;
    private TextView txtResult;
    private AudioWriterPCM writer;

    @Override
    protected void onStart() {
        super.onStart();
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        naverRecognizer.getSpeechRecognizer().release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_puzzle);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        txtResult = (TextView)findViewById(R.id.tv_record_result);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
        btnStart = (Button)findViewById(R.id.btn_record);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    btnStart.setTextColor(Color.YELLOW);
                    txtResult.setText("Connecting...");
                    naverRecognizer.recognize();
                } else {
                    Log.d("###", "stop and wait Final Result");
                    btnStart.setEnabled(false);

                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        });


        mPuzzle = (ImageView)findViewById(R.id.iv_puzzle);
        mPuzzleText = (EditText)findViewById(R.id.et_add_puzzle);
        mPuzzleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mPuzzleText.length()!=0){
                    mPuzzle.setImageResource(R.drawable.puzzles_blue);
                }
                else{
                    mPuzzle.setImageResource(R.drawable.puzzles_gray);
                }
            }
        });
        Intent intent = getIntent();
        mFriendId = intent.getLongExtra(FriendDetailActivity.EXTRA_FRIENDID,-1);


    }

    public void initNaverVoice(){

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
        switch (item.getItemId()){
            case R.id.action_register:
                //Toast.makeText(this,"action_register_click",Toast.LENGTH_SHORT).show();
                if(mPuzzleText.length()>0) {
                    Realm realm = Realm.getDefaultInstance();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Friend friend = realm.where(Friend.class).equalTo(Friend.USER_ID,mFriendId).findFirst();
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
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    finish();
                }
                else{
                    Toast.makeText(this,"내용을 입력해 주세요",Toast.LENGTH_SHORT).show();
                }
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    static class RecognitionHandler extends Handler {
        private final WeakReference<AddPuzzleActivity> mActivity;

        RecognitionHandler(AddPuzzleActivity activity) {
            mActivity = new WeakReference<AddPuzzleActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AddPuzzleActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                btnStart.setTextColor(Color.GREEN);
                //txtResult.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();

                /*for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                mResult = strBuf.toString();*/
                mResult = results.get(0);
                txtResult.setText(mResult);
                mPuzzleText.append(" "+txtResult.getText().toString());
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setTextColor(Color.RED);
                btnStart.setEnabled(true);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                btnStart.setTextColor(Color.RED);
                //btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
        }
    }
}
