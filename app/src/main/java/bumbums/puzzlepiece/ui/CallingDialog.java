package bumbums.puzzlepiece.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import io.realm.Realm;

public class CallingDialog extends AppCompatActivity {

    private TextView mName;
    private TextView mPhone;
    private Realm realm;
    private Friend mFriend;
    private FrameLayout mClear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling_dialog);

        mName=(TextView)findViewById(R.id.tv_name);
        mClear = (FrameLayout)findViewById(R.id.ll_cancel);
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();

    }


    private void init(){
        realm = Realm.getDefaultInstance();
        Intent i = getIntent();
        long id = i.getLongExtra(FriendDetailActivity.EXTRA_FRIENDID,-1);
        mFriend = realm.where(Friend.class).equalTo(Friend.FRIEND_ID,id).findFirst();
        mName.setText(mFriend.getName());
        mPhone.setText(mFriend.getPhoneNumber());

    }

    public Long getmFriendId(){
        Intent i = getIntent();
        long id = i.getLongExtra(FriendDetailActivity.EXTRA_FRIENDID,-1);
        return id;
    }


}
