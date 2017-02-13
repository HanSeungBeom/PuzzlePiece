package bumbums.puzzlepiece.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.UUID;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.model.Puzzle;
import io.realm.Realm;

/**
 * Created by 한승범 on 2017-02-07.
 */

public class Utils {
    public static final int[] colors={
            //16개
            R.color.material_light_green,
            R.color.material_indigo,
            R.color.material_orange,
            R.color.material_teal,
            R.color.material_lime,
            R.color.material_amber,
            R.color.material_green,
            R.color.material_red,
            R.color.material_pupple,
            R.color.material_grey,
            R.color.material_yello,
            R.color.material_depp_orange,
            R.color.material_deep_purple,
            R.color.material_pink,
            R.color.material_brown,
            R.color.material_blue_grey
    };
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
        int day = cal.get ( cal.DAY_OF_MONTH ) ;
        int hour = cal.get (cal.HOUR_OF_DAY);
        int minutes = cal.get (cal.MINUTE);
        int seconds = cal.get(cal.SECOND);
        return ""+year+"/"+month+"/"+day+"/"+hour+":"+minutes+":"+seconds;
    }

    public static String getNowDateToMilliSeconds(){
        Calendar cal = java.util.Calendar.getInstance();
        return String.valueOf(cal.getTimeInMillis());
    }

    public static String dateToCurrentFormat(String dateStr){
        //2017/2/10/11:48 같은 문자열을 -> 오늘날짜면 시간만, 어제부터는 날짜로만 표시.
        boolean isToday = false;

        StringTokenizer stringTokenizer = new StringTokenizer(dateStr,"/");
        String year = stringTokenizer.nextToken();
        String month = stringTokenizer.nextToken();
        String day =stringTokenizer.nextToken();
        String time = stringTokenizer.nextToken();
        stringTokenizer = new StringTokenizer(time,":");
        int hourOfDay = Integer.parseInt(stringTokenizer.nextToken());
        int minutes = Integer.parseInt(stringTokenizer.nextToken());

        Calendar cal = java.util.Calendar.getInstance();
        String today_year =String.valueOf(cal.get(cal.YEAR ));
        String today_month = String.valueOf((cal.get(cal.MONTH)+1));
        String today_day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

        if(year.equals(today_year)
                && month.equals(today_month)
                && day.equals(today_day)) isToday = true;
        String hour,min,ampm;

        if (hourOfDay >= 12) ampm = "오후";
        else ampm = "오전";


        if (ampm.equals("오후")) {
            if (hourOfDay != 12) hourOfDay = hourOfDay - 12;
        }

        if (hourOfDay < 10) {
            hour = "0" + String.valueOf(hourOfDay);
        }
        else{
            hour = String.valueOf(hourOfDay);
        }
        if (minutes < 10) {
            min = "0" + String.valueOf(minutes);
        }
        else{
            min = String.valueOf(minutes);
        }

        if(isToday){
            return ampm+" "+hour+":"+min;
        }
        else{
            return String.format("%2d/%02d/%2d",Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
        }





    }

    public static String getUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }


    public static String getFilePath(Uri _uri,Context context){
        String filePath = null;

        Log.d("###","befor URI = "+ _uri);
        if (_uri != null && "content".equals(_uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = _uri.getPath();
        }
        Log.d("###","Chosen path = "+ filePath);
        return filePath;
    }

    public static String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file


            //String extr = Environment.getExternalStorageDirectory().toString();

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/Pictures/ProfilePhoto");

            //File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = Utils.getUUID()+".jpg";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("###","filenot");
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("###","e");

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

    public static Uri getContentUri(Context context,String fileName){
        Uri fileUri = Uri.fromFile(new File(fileName));
        String filePath = fileUri.getPath();
        Cursor c = context.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, "_data = '" + filePath + "'", null, null );
        if(c.moveToNext()) {
              // 예외처리는 생략했습니다. 실제 코드에서는 예외처리를 잘 해주세요.
            int id = c.getInt(c.getColumnIndex("_id"));
            Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            return uri;
        }
        else
            return null;
    }
    public static Uri getContentUriFromRealPath(Context context, String realFilePath){
        Uri fileUri = Uri.parse(realFilePath);
        String uriFilePath = fileUri.getPath();
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , null
                , MediaStore.Audio.Media.DATA+"='" + uriFilePath + "'"
                , null
                , null);
        if(c.moveToNext()){
            int id = c.getInt(0);
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            return uri;
        }
        return null;
    }
}
