package bumbums.puzzlepiece.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import bumbums.puzzlepiece.R;

public class AddFriendActivity extends AppCompatActivity {

    private EditText mName, mPhone;
    public static final String NAME = "name";
    public static final String PHONE= "phone";
    public static final String RELATION = "relation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mName = (EditText)findViewById(R.id.et_add_friend_name);
        mPhone =(EditText)findViewById(R.id.et_add_friend_phone);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_register:
                if(     mName.getText().toString().equals("")
                        || mPhone.getText().toString().equals("")
                        ){
                    Toast.makeText(this,"빈 항목이 있어요~",Toast.LENGTH_SHORT).show();
                }
                else{
                    String name = mName.getText().toString();
                    String phone = mPhone.getText().toString();
                    Intent intent = getIntent();
                    intent.putExtra(NAME,name);
                    intent.putExtra(PHONE,phone);
                    setResult(RESULT_OK,intent);
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    finish();
                }
                break;
            default:

        }

        return super.onOptionsItemSelected(item);
    }
}
