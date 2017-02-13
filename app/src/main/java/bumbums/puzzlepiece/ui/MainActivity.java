package bumbums.puzzlepiece.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.util.Utils;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.ui.adapter.Pager;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements
TabLayout.OnTabSelectedListener{

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mFriendNum;
    private TextView mTitle;
    private Realm realm;

    //FirebaseTest;
    private Button mTestBtn;
    private ImageView mtestIv;
    private StorageReference mStorage;
    public static final int GALLERY_PICK = 2;
    private ProgressDialog mProgreeDialog;
    public static final String EXTRA_PHOTO = "photo";

    FirebaseAuth mAuth;
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

    public void testFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();//이게 root 주소
        mProgreeDialog = new ProgressDialog(this);
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("we need permission for read contact, find your location and system alert window")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("setting")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case GALLERY_PICK:
                    Uri contentUri = data.getData();
                    String filePath = Utils.getFilePath(contentUri,this);
                    String newFilePath = Utils.decodeFile(filePath,80,80);
                    String fileName= new File(newFilePath).getName();
                    //Log.d("###","NEW="+newFilePath+"//NAME="+new File(newFilePath).getName());
                   // Log.d("###","UriPath="+Utils.getContentUri(this,newFilePath));

                    Uri newFileUri = Uri.fromFile(new File(newFilePath));
                    final StorageReference filePath_= mStorage.child("Photos/"+newFileUri.getLastPathSegment());
                    filePath_.putFile(newFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("###","uploadDone");
                            Log.d("###","SERVICE FILEPATH="+filePath_);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("###","uploadFailure");
                        }
                    });

                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testFirebase();
        mTestBtn = (Button)findViewById(R.id.test_button);
        mtestIv = (ImageView)findViewById(R.id.iv_testtest) ;
        mTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i,GALLERY_PICK);
            }
        });


        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);

        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.friends_selector));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.history_selector));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.statistics_selector));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mViewPager = (ViewPager)findViewById(R.id.pager);

        Pager adapter = new Pager(getSupportFragmentManager(),mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.friends_selector);
        mTabLayout.getTabAt(1).setIcon(R.drawable.history_selector);
        mTabLayout.getTabAt(2).setIcon(R.drawable.statistics_selector);

        mFriendNum =(TextView)findViewById(R.id.tv_friend_num);


        mTabLayout.addOnTabSelectedListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mTitle =(TextView)findViewById(R.id.tv_title);

        realm = Realm.getDefaultInstance();







    }

    @Override
    protected void onResume() {
        super.onResume();

        //onCreate 에 하면 이상하게 안됨. listener 를 못찾음

        final RealmResults<Friend> results = realm.where(Friend.class)
                .findAll();
        mFriendNum.setText(String.valueOf(results.size()));

        results.addChangeListener(new RealmChangeListener<RealmResults<Friend>>() {
            @Override
            public void onChange(RealmResults<Friend> element) {
                mFriendNum.setText(String.valueOf(element.size()));
            }
        });

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()){
            case 0:
                mTitle.setText("지인");
                mFriendNum.setVisibility(View.VISIBLE);
                break;
            case 1:
                mTitle.setText("로그");
                mFriendNum.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mTitle.setText("통계");
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


    public interface onKeyBackPressedListener {
        public void onBack();
    }
    private onKeyBackPressedListener mOnKeyBackPressedListener;

    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }
    @Override
    public void onBackPressed() {
        if (mOnKeyBackPressedListener != null) {
            mOnKeyBackPressedListener.onBack();
        } else {
            super.onBackPressed();
        }
    }
}
