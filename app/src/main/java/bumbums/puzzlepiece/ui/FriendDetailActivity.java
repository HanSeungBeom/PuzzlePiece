package bumbums.puzzlepiece.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.task.RealmTasks;
import bumbums.puzzlepiece.ui.adapter.TabAdapter;
import bumbums.puzzlepiece.util.AppPermissions;
import bumbums.puzzlepiece.task.FirebaseTasks;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class FriendDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton fab;
    private TextView mName, mRelation, mPhone;
    private ImageView mFriendImage, mFriendImageDefault;

    private RecyclerView mRecyclerView;
    private Realm realm;
    private Friend mFriend;
    private long mFriendId;
    private String mPhotoName, mPhotoUrl, mPhotoPath;
    private StorageReference mStorage;
    FirebaseAuth mAuth;
    private Context mContext;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabAdapter mAdapter;
    private LinearLayout mFriendInfo;
    //
    public static final String EXTRA_FRIENDID = "friend_id";

    //startforActivityResult 용 변수
    public static final int GALLERY_MODE = 2;
    public static final int CAMERA_MODE = 3;

    //사진을 등록하는지 변경하는지 구별해주는 변수
    private boolean mIsNewPhotoMode;


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
        mContext = this;
        mFriendInfo = (LinearLayout) findViewById(R.id.ll_friend_info);
        mFriendInfo.setOnClickListener(this);
        mName = (TextView) findViewById(R.id.tv_detail_name);
        mRelation = (TextView) findViewById(R.id.tv_detail_relation);
        mPhone = (TextView) findViewById(R.id.tv_detail_phone);
        mFriendImage = (ImageView) findViewById(R.id.iv_friend_photo);
        mFriendImage.setOnClickListener(this);
        mFriendImageDefault = (ImageView) findViewById(R.id.iv_friend_photo_default);
        mFriendImageDefault.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();

        initData();
        setUpFireBase();
        setUpTabLayout();
    }

    public void setUpTabLayout() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mAdapter = new TabAdapter(getSupportFragmentManager());

        //setFragment
        mAdapter.addFragment(new TabPuzzlesFragment());
        mAdapter.addFragment(new TabRankFragment());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        //setIcon
     mTabLayout.getTabAt(0).setIcon(R.drawable.puzzles_selector);
        mTabLayout.getTabAt(1).setIcon(R.drawable.rank_selector);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    }

    public void setUpFireBase() {
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();//이게 root 주소
    }


    public void initData() {
        Intent intent = getIntent();
        long id = intent.getLongExtra(FriendDetailActivity.EXTRA_FRIENDID, -1);
        //Log.d("###","id="+id);

        mFriend = realm.where(Friend.class)
                .equalTo("id", id)
                .findFirst();
        mFriendId = mFriend.getId();

        syncFriendData(mFriend);
        syncPhoto(mFriend);

        mFriend.addChangeListener(new RealmChangeListener<Friend>() {
            @Override
            public void onChange(Friend element) {
                syncFriendData(element);
                syncPhoto(element);

            }
        });
        getSupportActionBar().setTitle("");
    }

    public void syncFriendData(Friend friend) {
        mName.setText(friend.getName());
        mPhone.setText(friend.getPhoneNumber());
        if(!mFriend.getRelation().equals("")) {
            mRelation.setText("(" + mFriend.getRelation() + ")");
        }
        else {
            mRelation.setText("("+getString(R.string.relation_is_empty)+")");
        }
        if(!mFriend.getPhoneNumber().equals("")){
            mPhone.setText(mFriend.getPhoneNumber());
        }
        else{
            mPhone.setText(getString(R.string.phone_number_is_empty));
        }
    }

    public void syncPhoto(Friend friend) {
        mPhotoName = friend.getProfileName();
        mPhotoUrl = friend.getProfileUrl();
        mPhotoPath = friend.getProfilePath();
        if (mPhotoName == null) {
            showDefault();
        } else {
            showPhoto();
        }
        FirebaseTasks.loadFriendPhoto(mContext, friend, mFriendImage);
    }

    public void showDefault() {
        mFriendImage.setVisibility(View.INVISIBLE);
        mFriendImageDefault.setVisibility(View.VISIBLE);
    }

    public void showPhoto() {
        mFriendImage.setVisibility(View.VISIBLE);
        mFriendImageDefault.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_detail, menu);
        return true;
    }

    public void modifyFriend() {
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_edit_friend, null);
        final EditText name = (EditText) dialogView.findViewById(R.id.et_edit_friend_name);
        name.setText(mFriend.getName());
        final EditText phone = (EditText) dialogView.findViewById(R.id.et_edit_friend_phone);
        phone.setText(mFriend.getPhoneNumber());
        final EditText relation = (EditText) dialogView.findViewById(R.id.et_edit_friend_relation);
        relation.setText(mFriend.getRelation());

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("지인 정보")
                .setIcon(R.drawable.ic_user_puzzle)
                .setView(dialogView)
                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RealmTasks.modifyFriend(mFriend,name.getText().toString(),phone.getText().toString(),relation.getText().toString());
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        android.app.AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setLayout(600, 900);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_edit:
                modifyFriend();
                //Toast.makeText(this,"action_edit_click",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("지인 삭제")
                        .setMessage(R.string.friend_del)
                        .setIcon(R.drawable.tab_friends_on)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        mFriend.deleteFromRealm();
                                        RealmResults<Puzzle> puzzlesWithFriend = realm.where(Puzzle.class).equalTo(Puzzle.FRIEND_ID, mFriendId).findAll();
                                        puzzlesWithFriend.deleteAllFromRealm();
                                    }
                                });

                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            // 취소 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();


                break;
            default:

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_friend_info:
                modifyFriend();
                break;

            case R.id.fab:
                Intent intent = new Intent(this, AddPuzzleActivity.class);
                intent.putExtra(EXTRA_FRIENDID, mFriendId);
                startActivity(intent);
                //startActivityForResult(intent,REQUESTCODE_PUZZLE);
                //Log.d("###","click");
                break;
            case R.id.iv_friend_photo:
                //제거하기
                //변경하기 중 선택하게
            {

                final CharSequence[] items = {"사진 변경", "사진 삭제"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("프로필 사진");
                alertDialogBuilder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                if (id == 0) { //사진 변경
                                    mIsNewPhotoMode = false;
                                    addFriendPhoto();
                                } else if (id == 1) { //사진 삭제
                                    deleteFriendPhoto();
                                }
                             /*Toast.makeText(getApplicationContext(),
                                        items[id] + " .선택했습니다",
                                        Toast.LENGTH_SHORT).show();*/
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
            break;
            case R.id.iv_friend_photo_default:
                //등록하기
                mIsNewPhotoMode = true;
                addFriendPhoto();

                break;
            default:

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_MODE:
                    FirebaseTasks.registerPhoto(this, data.getData(), mFriendId, mIsNewPhotoMode);
                    break;
                case CAMERA_MODE:
                    FirebaseTasks.registerPhoto(this, data.getData(), mFriendId, mIsNewPhotoMode);
                    break;
                default:

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addFriendPhoto() {

        if (AppPermissions.hasPhotoPermissionsGranted(this)) {
            final CharSequence[] items = {"사진촬영", "갤러리선택"};
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("프로필 사진");
            alertDialogBuilder.setItems(items,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            if (id == 0) { //사진촬영
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, CAMERA_MODE);
                            } else if (id == 1) { //갤러리 선택
                                Intent i = new Intent(Intent.ACTION_PICK);
                                i.setType("image/*");
                                startActivityForResult(i, GALLERY_MODE);
                            }

                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            Toast.makeText(this, getString(R.string.permission_deny), Toast.LENGTH_SHORT).show();
            setUpTedPermission();
        }
    }

    public void deleteFriendPhoto() {
        FirebaseTasks.deletePhoto(this, mFriendId);
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
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

    public void setUpTedPermission() {
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
                .setRationaleMessage("you need permission external storage for photo.")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("setting")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }
}
