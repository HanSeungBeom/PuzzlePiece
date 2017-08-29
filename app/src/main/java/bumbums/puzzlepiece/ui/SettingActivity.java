package bumbums.puzzlepiece.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.task.FirebaseTasks;
import bumbums.puzzlepiece.util.AppPermissions;
import bumbums.puzzlepiece.util.RealmBackupRestore;
import bumbums.puzzlepiece.util.Utils;

public class SettingActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener,
        OnSuccessListener<UploadTask.TaskSnapshot>
{

    SwitchCompat mLoginSwitch;
    SwitchCompat mCallingSwitch;
    Context mContext;
    Button mBackupBtn, mRestoreBtn;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private ProgressBar mLoginPb;
    private LinearLayout mLoginDetailView;
    private TextView mLoginName;
    private String mGoogleId;
    private TextView mLastBackupDate;
    private LinearLayout mVersionLayout;

    private RealmBackupRestore mRealmBackupRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        mGoogleId = null;
        mLastBackupDate = (TextView) findViewById(R.id.tv_last_backup_date);
        mLoginPb = (ProgressBar) findViewById(R.id.pb_login);
        mLoginSwitch = (SwitchCompat) findViewById(R.id.sw_login);
        mLoginName = (TextView) findViewById(R.id.tv_login_name);
        mCallingSwitch = (SwitchCompat)findViewById(R.id.sw_calling);

        mLoginDetailView = (LinearLayout) findViewById(R.id.ll_login_detail);
        mBackupBtn = (Button) findViewById(R.id.btn_backup);
        mRestoreBtn = (Button) findViewById(R.id.btn_restore);
        mVersionLayout = (LinearLayout)findViewById(R.id.ll_version);


        configureGoogleSignIn();
        mLoginSwitch.setOnCheckedChangeListener(this);
        mCallingSwitch.setOnCheckedChangeListener(this);
        mBackupBtn.setOnClickListener(this);
        mRestoreBtn.setOnClickListener(this);
        mVersionLayout.setOnClickListener(this);


        mRealmBackupRestore = new RealmBackupRestore(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    public void setLoginData(String name) {
        mLoginName.setText(name);
    }

    public void setUpdateData(Long updatedTime) {
        if (updatedTime != null) {
            //Log.d("###", "updatedTime=" + Utils.getDateFromMilli(updatedTime));
            mBackupBtn.setEnabled(true);//인터넷을 껏다가 연결하는경우 여기서 켜지게 하기 위해서.
            mRestoreBtn.setEnabled(true);
            mLastBackupDate.setText("마지막 백업날짜 : " + Utils.getDateFromMilli(updatedTime));

        } else {
            mRestoreBtn.setEnabled(false);
            if (Utils.isInternetConnected(this)) {
                //Log.d("###", "updatedTime=null");

                mLastBackupDate.setText(getString(R.string.no_backup_data));
            } else {
                mLastBackupDate.setText(getString(R.string.internet_disconnection));
                Toast.makeText(this, getString(R.string.internet_disconnection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void init() {
        if(!Utils.isInternetConnected(this)){
            mRestoreBtn.setEnabled(false);
            mBackupBtn.setEnabled(false);
            mLastBackupDate.setText(getString(R.string.internet_disconnection));
        }
        else{
            mRestoreBtn.setEnabled(false);
            mBackupBtn.setEnabled(true);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean(getString(R.string.pref_login), false)) {
            mLoginSwitch.setChecked(true);
        } else {
            mLoginSwitch.setChecked(false);
            mLoginDetailView.setVisibility(View.GONE);
        }

        if(sharedPreferences.getBoolean(getString(R.string.pref_calling),false)){
            mCallingSwitch.setChecked(true);
        }
        else{
            mCallingSwitch.setChecked(false);
        }

    }

    public void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void requestSignIn() {
        showPbLogin();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void logout() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }
    //TODO 인터넷 연결 체크해서 값닫기.

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                //Log.d("###", account.getId() + "accountID");
                setGoogleIdToSharedPreference(account.getId(), account.getDisplayName());
                hidePbLogin();
                mGoogleId = account.getId();
                setLoginData(account.getDisplayName());
                getLastBackupDate();
                mLoginDetailView.setVisibility(View.VISIBLE);
            } else {
                hidePbLogin();
                mGoogleId = null;
                mLoginSwitch.setChecked(false);
                mLoginDetailView.setVisibility(View.GONE);
                Log.e("###", "Google Sign In failed.");
            }
        }
    }

    public void setGoogleIdToSharedPreference(String id, String name) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_googleidkey), id);
        editor.putString(getString(R.string.pref_googleidName), name);
        editor.commit();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("###", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void hidePbLogin() {
        mLoginPb.setVisibility(View.INVISIBLE);
        mLoginSwitch.setVisibility(View.VISIBLE);
    }

    public void showPbLogin() {
        mLoginPb.setVisibility(View.VISIBLE);
        mLoginSwitch.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_login: {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean savedStatePref = pref.getBoolean(getString(R.string.pref_login), false);
                if (savedStatePref) {
                    if (isChecked) {//값이 있는경우 그대로 출력
                        mGoogleId = pref.getString(getString(R.string.pref_googleidkey), "-1");
                        Log.d("###", "googleID=" + mGoogleId);
                        setLoginData(pref.getString(getString(R.string.pref_googleidName), "-1"));
                        getLastBackupDate();
                        mLoginDetailView.setVisibility(View.VISIBLE);
                    } else {
                        logout();
                        mLoginDetailView.setVisibility(View.GONE);
                    }
                } else {
                    if (isChecked) {
                        //사용자가 누른경우
                        requestSignIn();
                    } else {
                        logout();
                        mLoginDetailView.setVisibility(View.GONE);
                    }
                }

                SharedPreferences.Editor editor = pref.edit();
                if (isChecked) {
                    editor.putBoolean(getString(R.string.pref_login), true);
                } else {
                    editor.putBoolean(getString(R.string.pref_login), false);
                }
                editor.commit();
            }
            break;

            case R.id.sw_calling:{
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = pref.edit();
                Boolean isHasPermissionForAlertWindow =false;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                     isHasPermissionForAlertWindow = Settings.canDrawOverlays(this);
                }
                else{
                    isHasPermissionForAlertWindow = true;
                }

                if (isChecked) {
                    if(AppPermissions.hasCallingPermissionsGranted(this) && isHasPermissionForAlertWindow){
                        editor.putBoolean(getString(R.string.pref_calling), true);
                    }
                      else{
                        setUpTedPermissionForCalling();
                    }
                } else {
                    editor.putBoolean(getString(R.string.pref_calling), false);
                }
                editor.commit();
            }
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_backup:
                if (AppPermissions.hasBackupPermissionsGranted(this)) {
                    Uri backupFileUri = mRealmBackupRestore.backup(mGoogleId);
                    upLoadRealmFile(backupFileUri);
                } else {
                    setUpTedPermissionForBackup();
                }

                break;
            case R.id.btn_restore:
                if (AppPermissions.hasBackupPermissionsGranted(this)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("백업 파일 불러오기")
                            .setMessage(getString(R.string.restore))
                            .setCancelable(true)
                            .setIcon(R.drawable.ic_user_puzzle)

                            .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton){
                                    MainActivity.mMainActivity.finish();
                                    FirebaseTasks.loadBackupDataFromFirebase(SettingActivity.this, mGoogleId, mRealmBackupRestore);
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                // 취소 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton){
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();



                } else {
                    setUpTedPermissionForBackup();
                }

                break;
            case R.id.ll_version:
                Log.d("######","here!");
                Intent i = new Intent(SettingActivity.this, OSSPIPPActivity.class);
                startActivity(i);
                break;
        }
    }

    public void setUpTedPermissionForBackup() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(SettingActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(SettingActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("you need permission external storage for backup.")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("setting")
                .setPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    public void setUpTedPermissionForCalling() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(SettingActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(getString(R.string.pref_calling), true);
                editor.commit();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(SettingActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                mCallingSwitch.setChecked(false);
            }
        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("you need permission call state for calling option.")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("setting")
                .setPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.SYSTEM_ALERT_WINDOW
                )
                .check();
    }


    private void getLastBackupDate() {
        final StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference targetPath_ = storage.child("UserRealm/" + mGoogleId + "/" + RealmBackupRestore.EXPORT_REALM_FILE_NAME);
        targetPath_.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                setUpdateData(storageMetadata.getUpdatedTimeMillis());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                setUpdateData(null);
            }
        });
    }

    public void upLoadRealmFile(Uri realmFileUri) {
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        final StorageReference filePath_ = storage.child("UserRealm/" + mGoogleId + "/" + RealmBackupRestore.EXPORT_REALM_FILE_NAME);

        filePath_.putFile(realmFileUri).addOnSuccessListener(this);

    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        Log.d("###", "uploadDone");

        getLastBackupDate();
    }

}
