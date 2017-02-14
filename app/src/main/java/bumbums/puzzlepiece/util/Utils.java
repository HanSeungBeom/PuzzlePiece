package bumbums.puzzlepiece.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

        //Log.d("###","befor URI = "+ _uri);
        if (_uri != null && "content".equals(_uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = _uri.getPath();
        }
        //Log.d("###","Chosen path = "+ filePath);
        return filePath;
    }

    public static String decodeFile(Context context,String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;
        Boolean isEnterIf = false;
        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                isEnterIf = true;
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            }/* else {
                Log.d("###","unscaledBitmap");
                unscaledBitmap.recycle();
                return path;
            }*/

            File mFolder = new File(context.getFilesDir(),"/profile_pictures");


            //String extr = Environment.getExternalStorageDirectory().toString();
           // File mFolder = new File(extr + "/Pictures/ProfilePhoto");

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
                if(isEnterIf)
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                else
                    unscaledBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("###","filenot");
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("###","e");

                e.printStackTrace();
            }
            unscaledBitmap.recycle();
            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }


    //target to save
    private static Target getTarget(final Context context,final String url,final String fileName){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File( context.getFilesDir()+ "/profile_pictures/"+fileName);

                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                            Log.d("###","newfileCreatedPath="+file.getAbsolutePath());
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("###","onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    public static void deleteDir(String path)
    {
        File file = new File(path);
        File[] childFileList = file.listFiles();
        if(childFileList== null)return;
        for(File childFile : childFileList)
        {
            if(childFile.isDirectory()) {
                deleteDir(childFile.getAbsolutePath());     //하위 디렉토리 루프
            }
            else {
                childFile.delete();    //하위 파일삭제
            }
        }
        file.delete();    //root 삭제
    }


    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }



}
