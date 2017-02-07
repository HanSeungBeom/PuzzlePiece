package bumbums.puzzlepiece;

import bumbums.puzzlepiece.model.Friend;
import io.realm.Realm;

/**
 * Created by 한승범 on 2017-02-07.
 */

public class Utils {
    public static int getNextKey(Realm realm)
    {
   
            if(realm.where(Friend.class).max("id") == null)return 1;
            else
             return realm.where(Friend.class).max("id").intValue() + 1;

    }
}
