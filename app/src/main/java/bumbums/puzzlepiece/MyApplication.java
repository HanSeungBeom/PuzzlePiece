package bumbums.puzzlepiece;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by han sb on 2017-02-07.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
