package bumbums.puzzlepiece.task;

import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;

/**
 * Created by han sb on 2017-02-20.
 */

public class RealmTasks {
    public static void addFriend(final String name, final String phone, final String relation){
        Realm realm = Realm.getDefaultInstance();
        final long id = Utils.getNextKeyFriend(realm);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Friend friend = realm.createObject(Friend.class, id);
                friend.setName(name);
                friend.setPhoneNumber(phone);
                friend.setRelation(relation);
            }
        });
    }
}
