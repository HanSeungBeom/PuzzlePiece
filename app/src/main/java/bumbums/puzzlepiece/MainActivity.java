package bumbums.puzzlepiece;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addFriend(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
            cursor.moveToFirst();
            //name = cursor.getString(0);        //0은 이름을 얻어옵니다.
            //number = cursor.getString(1);   //1은 번호를 받아옵니다.
            Log.d("###","name="+cursor.getString(0)+"/number="+cursor.getString(1)) ;
            cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
