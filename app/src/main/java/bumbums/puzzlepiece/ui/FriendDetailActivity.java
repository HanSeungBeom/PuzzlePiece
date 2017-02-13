package bumbums.puzzlepiece.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import bumbums.puzzlepiece.ui.adapter.FriendRecyclerViewAdapter;
import bumbums.puzzlepiece.ui.adapter.PuzzleRecyclerViewAdpater;
import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.util.CircleTransform;
import bumbums.puzzlepiece.util.FirebaseTasks;
import bumbums.puzzlepiece.util.Utils;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

public class FriendDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton fab;
    private TextView mName,mRelation,mPhone,mPuzzle,mRank,mCalendar;
    private ImageView mFriendImage,mFriendImageDefault;

    private RecyclerView mRecyclerView;
    private Realm realm;
    private Friend mFriend;
    private long mFriendId;
    private String mPhotoName,mPhotoUrl,mPhotoPath;
    private StorageReference mStorage;
    FirebaseAuth mAuth;
    private Context mContext;


    //startforActivityResult 용 변수
    public static final int REQUESTCODE_PUZZLE = 1;
    public static final int GALLERY_MODE = 2;
    public static final int CAMERA_MODE = 3;

    //사진을 등록하는지 변경하는지 구별해주는 변수
    private boolean isRegisterMode;


    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        mContext= this;

        mName=(TextView)findViewById(R.id.tv_detail_name);
        mRelation=(TextView)findViewById(R.id.tv_detail_relation);
        mPhone=(TextView)findViewById(R.id.tv_detail_phone);
        mPuzzle=(TextView)findViewById(R.id.tv_detail_puzzle);
        mRank=(TextView)findViewById(R.id.tv_detail_rank);
        mCalendar=(TextView)findViewById(R.id.tv_detail_calendar);
        mFriendImage = (ImageView)findViewById(R.id.iv_friend_photo);
        mFriendImage.setOnClickListener(this);
        mFriendImageDefault = (ImageView)findViewById(R.id.iv_friend_photo_default);
        mFriendImageDefault.setOnClickListener(this);
        mRecyclerView=(RecyclerView)findViewById(R.id.rv_friend_detail);



        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();

        initData();
        setUpFireBase();
        setUpTedPermission();
        setUpRecyclerView();
        //mCalendar.setText(String.valueOf(friend.get));
    }

    public void setUpFireBase(){
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();//이게 root 주소
    }
    public void setUpTedPermission(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(FriendDetailActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(FriendDetailActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("we need permission for read contact, find your location and system alert window")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("setting")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    public void initData(){
        Intent intent = getIntent();
        long id = intent.getLongExtra(FriendRecyclerViewAdapter.EXTRA_ID,-1);
        //Log.d("###","id="+id);

        mFriend = realm.where(Friend.class)
                .equalTo("id",id)
                .findFirst();
        mFriend.addChangeListener(new RealmChangeListener<Friend>() {
            @Override
            public void onChange(Friend friend) {
                mPuzzle.setText(String.valueOf(friend.getPuzzles().size()));
            }
        });
        mFriendId= mFriend.getId();
        mName.setText(mFriend.getName());
        mRelation.setText("("+mFriend.getRelation()+")");
        mPhone.setText(mFriend.getPhoneNumber());
        mPuzzle.setText(String.valueOf(mFriend.getPuzzles().size()));
        mRank.setText(String.valueOf(mFriend.getRank()));

        mPhotoName = mFriend.getProfileName();
        mPhotoUrl =mFriend.getProfileUrl();
        mPhotoPath = mFriend.getProfilePath();
        if(mPhotoName == null){
            mFriendImage.setVisibility(View.INVISIBLE);
        }
        else{
            mFriendImage.setVisibility(View.VISIBLE);
            mFriendImageDefault.setVisibility(View.INVISIBLE);
            Picasso.with(mContext).load(new File(mPhotoPath)).transform(new CircleTransform()).into(mFriendImage);
        }

        mFriend.addChangeListener(new RealmChangeListener<Friend>() {
            @Override
            public void onChange(Friend element) {
                mPhotoName = element.getProfileName();
                mPhotoPath = element.getProfilePath();
                if(mPhotoName==null){
                    showDefault();
                }
                else{
                    showPhoto();
                    Picasso.with(mContext).load(new File(mPhotoPath)).transform(new CircleTransform()).into(mFriendImage);
                }
            }
        });


        getSupportActionBar().setTitle(mFriend.getName());
    }

    public void showDefault(){
        mFriendImage.setVisibility(View.INVISIBLE);
        mFriendImageDefault.setVisibility(View.VISIBLE);
    }
    public void showPhoto(){
        mFriendImage.setVisibility(View.VISIBLE);
        mFriendImageDefault.setVisibility(View.INVISIBLE);
    }


    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mRecyclerView.setAdapter(new PuzzleRecyclerViewAdpater(this, realm.where(Puzzle.class).equalTo(Puzzle.FRIEND_ID,mFriendId).findAllAsync()));
        mRecyclerView.setHasFixedSize(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
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

    public void addPuzzle(final String text, final String date,final String dateToMilliSeconds){
        final long id = Utils.getNextKeyPuzzle(realm);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Puzzle puzzle = realm.createObject(Puzzle.class, id);
                puzzle.setFriendId(mFriendId);
                puzzle.setText(text);
                puzzle.setDate(date);
                puzzle.setDateToMilliSeconds(dateToMilliSeconds);
                puzzle.setFriendName(mName.getText().toString());

/*                RealmResults<Friend> data = realm.where(Friend.class)
                        .equalTo("id", mFriend.getId())
                        .findAll();
                data.get(0).getPuzzles().add(puzzle);*/

                Friend friend =realm.where(Friend.class).equalTo("id",mFriendId).findFirst();
                friend.getPuzzles().add(puzzle);

            }},new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d("##","success");
            }


        });
        Toast.makeText(this,"id="+id+" created",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab:

               Intent intent = new Intent(this, AddPuzzleActivity.class);
                startActivityForResult(intent,REQUESTCODE_PUZZLE);
                //Log.d("###","click");
                break;
            case R.id.iv_friend_photo:
                deleteFriendPhoto();
                //제거하기
                //변경하기 보여주기
                break;
            case R.id.iv_friend_photo_default:
                addFriendPhoto();
                //등록하기 보여주기
                break;
            default:

        }
    }

    public void deletePuzzle(final long id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Puzzle> rows = realm.where(Puzzle.class).equalTo(Puzzle.PUZZLE_ID,id).findAll();
                rows.deleteAllFromRealm();
            }
        });
       // Toast.makeText(this,"id="+id+" deleted",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case REQUESTCODE_PUZZLE:
                    String text = data.getStringExtra(AddPuzzleActivity.EXTRA_PUZZLE_TEXT);
                    String date = data.getStringExtra(AddPuzzleActivity.EXTRA_PUZZLE_DATE);
                    String dateToMilliSeconds = data.getStringExtra(AddPuzzleActivity.EXTRA_PUZZLE_DATE_TO_MILLISECONDS);
                    addPuzzle(text,date, dateToMilliSeconds);
                    break;
                case GALLERY_MODE:
                    FirebaseTasks.uploadUriToFirebase(this,mStorage,data.getData(),mFriendId,isRegisterMode);
                    break;
                case CAMERA_MODE:
                    FirebaseTasks.uploadUriToFirebase(this,mStorage,data.getData(),mFriendId,isRegisterMode);
                    break;
                default:

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addFriendPhoto(){
        final CharSequence[] items = { "사진촬영", "갤러리선택"};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("프로필 사진저장");
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {

                        if(id==0){ //사진촬영
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            isRegisterMode = true;
                            startActivityForResult(intent,CAMERA_MODE);
                        }
                        else if(id==1){ //갤러리 선택
                            Intent i = new Intent(Intent.ACTION_PICK);
                            isRegisterMode = true;
                            i.setType("image/*");
                            startActivityForResult(i,GALLERY_MODE);
                        }

                            Toast.makeText(getApplicationContext(),
                                    items[id] + " 선택했습니다.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                    }
                });

// 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

// 다이얼로그 보여주기
        alertDialog.show();
    }

    public void deleteFriendPhoto(){
       FirebaseTasks.deletePhoto(this,mStorage,mFriendId);
    }

    public void modifyFriendPhoto(){

    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("###", "signInAnonymously:FAILURE", exception);
                    }
                });
    }
}
