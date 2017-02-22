package bumbums.puzzlepiece.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;


/**
 * Created by han sb on 2017-02-17.
 */

public class AppPermissions {
    public static final String[] PHOTO_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final String[] BACKUP_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String[] CALLING_PERMISSIONS={
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.SYSTEM_ALERT_WINDOW

    };

    public static final String[] STT_PERMISSIONS={
            Manifest.permission.RECORD_AUDIO
    };



    public static boolean hasPhotoPermissionsGranted(Context context) {
        for (String permission : PHOTO_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasBackupPermissionsGranted(Context context){
        for (String permission : BACKUP_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public static boolean hasCallingPermissionsGranted(Context context){
        for (String permission : CALLING_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


}
