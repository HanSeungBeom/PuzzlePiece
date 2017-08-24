package bumbums.puzzlepiece.task;

import android.content.Context;
import android.widget.Toast;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;

/**
 * Created by han sb on 2017-02-20.
 */

public class RealmTasks {
    //시간 날때 모든 realm 함수들 여기다 모아놓기.


    public static void addFriend(Context context, final String name, final String phone){
        if(name.equals("")){
            Toast.makeText(context,context.getString(R.string.name_empty),Toast.LENGTH_LONG).show();
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        final long id = Utils.getNextKeyFriend(realm);
        final String newPhoneNumber = phone.replace("-","");


        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Friend friend = realm.createObject(Friend.class, id);
                friend.setName(name);
                friend.setPhoneNumber(newPhoneNumber);
                    }
        });
        realm.close();
    }

    public static void modifyFriend(final Friend friend, final String name, final String phone){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                friend.setName(name);
                friend.setPhoneNumber(phone);
              }
        });
        realm.close();
    }
}
