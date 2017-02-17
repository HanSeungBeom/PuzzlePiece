package bumbums.puzzlepiece.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;


/**
 * Created by han sb on 2017-02-17.
 */

public class AppPermissions {
    public static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static boolean hasPermissionsGranted(Context context) {
        for (String permission : VIDEO_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
