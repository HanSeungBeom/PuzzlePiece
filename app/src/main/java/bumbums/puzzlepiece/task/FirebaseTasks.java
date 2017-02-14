package bumbums.puzzlepiece.task;

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
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by han sb on 2017-02-14.
 */

public class FirebaseTasks {


    public static void deletePhoto(Context context, final long friendId) {
        final Realm realm = Realm.getDefaultInstance();
        //제거하기
        String profileUrl = realm.where(Friend.class).equalTo(Friend.USER_ID, friendId).findFirst().getProfileUrl();
        final String fileName = realm.where(Friend.class).equalTo(Friend.USER_ID, friendId).findFirst().getProfileName();

        if (fileName == null) return;

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

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference targetPath_ = storage.child("Photos/" + fileName);
        targetPath_.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("###", "FIREBASE에서 파일삭제=" + fileName);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Friend friend = realm.where(Friend.class).equalTo(Friend.USER_ID, friendId).findFirst();
                        friend.setProfileUrl(null);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("###", "FIREBASE에서 파일삭제 실패=" + fileName);

            }
        });
        //Firebase에서 제거하기.

    }

    public static void registerPhoto(Context context, Uri contentUri, final long friendId, boolean isRegisterMode) {
        //isRegisterMode : 새로 등록하는지, 사진 수정인지 알려주는 변수.


        final Realm realm = Realm.getDefaultInstance();
        StorageReference storage = FirebaseStorage.getInstance().getReference();

        if (!isRegisterMode) {//modifyMode
            deletePhoto(context, friendId);
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
        final StorageReference filePath_ = storage.child("Photos/" + newFileUri.getLastPathSegment());
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

    public static void downloadPhotoFromFirebase(Context context, final String fileName) {

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference targetPath_ = storage.child("Photos/" + fileName);


        Log.d("###", "fileName=" + fileName);


        File folder = new File(context.getFilesDir() + "/profile_pictures");
        if (!folder.exists())
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
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

}
