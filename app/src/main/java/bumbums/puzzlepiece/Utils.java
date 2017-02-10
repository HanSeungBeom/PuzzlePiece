package bumbums.puzzlepiece;

import java.util.Calendar;

import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import io.realm.Realm;

/**
 * Created by 한승범 on 2017-02-07.
 */

public class Utils {
    synchronized public static int getNextKeyFriend(Realm realm)
    {
            if(realm.where(Friend.class).max("id") == null)return 1;
            else
             return realm.where(Friend.class).max("id").intValue() + 1;
    }
    synchronized public static int getNextKeyPuzzle(Realm realm)
    {
        if(realm.where(Puzzle.class).max("id") == null)return 1;
        else
            return realm.where(Puzzle.class).max("id").intValue() + 1;
    }

    public static String getNowDate(){
        Calendar cal = java.util.Calendar.getInstance();

        int year = cal.get ( cal.YEAR );
        int month = cal.get ( cal.MONTH ) + 1 ;
        int date = cal.get ( cal.DATE ) ;
        int hour = cal.get (cal.HOUR_OF_DAY);
        int minutes = cal.get (cal.MINUTE);

        return ""+year+"/"+month+"/"+date+"/"+hour+":"+minutes;
    }

}
