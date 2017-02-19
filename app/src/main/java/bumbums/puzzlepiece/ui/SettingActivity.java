package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.task.NotificationService;
import bumbums.puzzlepiece.util.Utils;

public class SettingActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    SwitchCompat mLoginSwitch;
    SwitchCompat mNotiSwitch;
    Context mContext;


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private ProgressBar mLoginPb;
    private String userName;
    private String userToken;
    private LinearLayout mLoginDetailView;
    private TextView mLoginName;
    private String mGoogleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        mGoogleId = null;
        mLoginPb = (ProgressBar)findViewById(R.id.pb_login);
        mLoginSwitch = (SwitchCompat) findViewById(R.id.sw_login);
        mLoginName = (TextView)findViewById(R.id.tv_login_name);
        mNotiSwitch = (SwitchCompat) findViewById(R.id.sw_noti);
        mLoginDetailView = (LinearLayout)findViewById(R.id.ll_login_detail);

        mLoginSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                    boolean savedStatePref = pref.getBoolean(getString(R.string.pref_login), false);
                    if (savedStatePref) {
                        if (isChecked) {//값이 있는경우 그대로 출력
                            mGoogleId = pref.getString(getString(R.string.pref_googleidkey), "-1");
                            Log.d("###", "googleID=" + mGoogleId);
                            setLoginData(pref.getString(getString(R.string.pref_googleidName), "-1"));
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

        });
        configureGoogleSignIn();


        mNotiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = pref.edit();
                if (isChecked) {
                    editor.putBoolean(getString(R.string.pref_noti), true);
                    Intent i = new Intent(SettingActivity.this, NotificationService.class);
                    startService(i);
                } else {
                    editor.putBoolean(getString(R.string.pref_noti), false);
                    Utils.cancelNotification(mContext, NotificationService.NOTIFICATION_CODE);
                }
                editor.commit();
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    public void setLoginData(String name){
        mLoginName.setText(name);
    }

    public void init(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean(getString(R.string.pref_login),false)){
            mLoginSwitch.setChecked(true);
        }
        else{
            mLoginSwitch.setChecked(false);
            mLoginDetailView.setVisibility(View.GONE);
        }

        if(sharedPreferences.getBoolean(getString(R.string.pref_noti),false)){
            mNotiSwitch.setChecked(true);
        }
        else{
            mNotiSwitch.setChecked(false);
        }
    }

    public void configureGoogleSignIn(){
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


    public void logout(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                Log.d("###",account.getId()+"accountID");
                setGoogleIdToSharedPreference(account.getId(),account.getDisplayName());
                hidePbLogin();
                mGoogleId=account.getId();
                setLoginData(account.getDisplayName());
                mLoginDetailView.setVisibility(View.VISIBLE);
            } else {
                hidePbLogin();
                mGoogleId=null;
                mLoginSwitch.setChecked(false);
                mLoginDetailView.setVisibility(View.GONE);
                Log.e("###", "Google Sign In failed.");
            }
        }
    }

    public void setGoogleIdToSharedPreference(String id,String name){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putString(getString(R.string.pref_googleidkey),id);
        editor.putString(getString(R.string.pref_googleidName),name);
        editor.commit();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("###", "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        Log.d("###", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            Log.w("###", "signInWithCredential", task.getException());
                            Toast.makeText(SettingActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //setLoginData(mFirebaseUser.getDisplayName());
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("###", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void hidePbLogin(){
        mLoginPb.setVisibility(View.INVISIBLE);
        mLoginSwitch.setVisibility(View.VISIBLE);
    }
    public void showPbLogin(){
        mLoginPb.setVisibility(View.VISIBLE);
        mLoginSwitch.setVisibility(View.INVISIBLE);
    }
}
