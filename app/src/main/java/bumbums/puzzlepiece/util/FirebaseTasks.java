package bumbums.puzzlepiece.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import bumbums.puzzlepiece.model.Friend;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by han sb on 2017-02-14.
 */

public class FirebaseTasks {


    public static void deletePhoto(Context context, StorageReference storageReference, final long friendId) {
        Realm realm = Realm.getDefaultInstance();
        //제거하기
        String profileUrl = realm.where(Friend.class).equalTo(Friend.USER_ID, friendId).findFirst().getProfileUrl();

        //DB 경로 제거하기
        File dir = context.getFilesDir();
        File file = new File(dir, "profile_pictures/" + profileUrl);
        boolean isDeleted = file.delete();
        Log.d("###", "isDeleted=" + isDeleted + "//filePath=" + file.getAbsolutePath());
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Friend friend = realm.where(Friend.class).equalTo(Friend.USER_ID, friendId).findFirst();
                friend.setProfilePath(null);
                friend.setProfileName(null);
            }
        });


        //Firebase에서 제거하기.
    }

    public static void uploadUriToFirebase(Context context, StorageReference storageReference, Uri contentUri, final long friendId, boolean isRegisterMode) {
        final Realm realm = Realm.getDefaultInstance();


        if (!isRegisterMode) {//modifyMode
            deletePhoto(context, storageReference, friendId);
        }

        //사진 생성(내부저장)
        String filePath = Utils.getFilePath(contentUri, context);
        //Log.d("###", context.getFilesDir().toString());
        final String newFilePath = Utils.decodeFile(context, filePath, 200, 200);
        final String fileName = new File(newFilePath).getName();
        Log.d("###", "NEW=" + newFilePath + "//NAME=" + new File(newFilePath).getName());
        // Log.d("###","UriPath="+Utils.getContentUri(this,newFilePath));
        Uri newFileUri = Uri.fromFile(new File(newFilePath));

        //DB에 경로저장
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Friend friend = realm.where(Friend.class).equalTo(Friend.USER_ID, friendId).findFirst();
                friend.setProfilePath(newFilePath);
                //Log.e("###","photoPath="+newFilePath);
                friend.setProfileName(fileName);

            }
        });


        //사진 업로드
        final StorageReference filePath_ = storageReference.child("Photos/" + newFileUri.getLastPathSegment());
        filePath_.putFile(newFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filePath_.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Friend friend = realm.where(Friend.class).equalTo(Friend.USER_ID, friendId).findFirst();
                                friend.setProfileUrl(uri.toString());

                            }
                        });
                    }
                });


                Log.d("###", "uploadDone");
                Log.d("###", "SERVICE FILEPATH=" + filePath_);
                //Log.d("###","DOWNLOADURL = "+filePath_.getDownloadUrl());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("###", "uploadFailure");

            }
        });


    }

    public static void downloadTolocalFileFromFirebase(Context context, final String fileName) {

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference targetPath_ = storage.child("Photos/" + fileName);


        Log.d("###","fileName="+fileName);



        File folder = new File(context.getFilesDir()+"/profile_pictures");
        if(!folder.exists())
            folder.mkdir();

        File file = new File(context.getFilesDir(), "/profile_pictures/" + fileName);
        try {
            file.createNewFile();
            targetPath_.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d("###", fileName + "추가완료");
                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("###", fileName + "추가실패");
                    exception.printStackTrace();
                    // Handle any errors
                }
            });
        }catch (IOException e){
            Log.d("###","에런=");
            e.printStackTrace();
        }

    }

/*    public static void uploadUriToFirebase(Context context, StorageReference storageReference, Uri contentUri){

        String filePath = Utils.getFilePath(contentUri, context);
        //Log.d("###", context.getFilesDir().toString());

        String newFilePath = Utils.decodeFile(context, filePath, 100, 100);
        String fileName = new File(newFilePath).getName();
        Log.d("###", "NEW=" + newFilePath + "//NAME=" + new File(newFilePath).getName());
        // Log.d("###","UriPath="+Utils.getContentUri(this,newFilePath));

        Uri newFileUri = Uri.fromFile(new File(newFilePath));
        final StorageReference filePath_ = storageReference.child("Photos/" + newFileUri.getLastPathSegment());
        filePath_.putFile(newFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("###", "uploadDone");
                Log.d("###", "SERVICE FILEPATH=" + filePath_);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("###", "uploadFailure");

            }
        });

    }*/

}
