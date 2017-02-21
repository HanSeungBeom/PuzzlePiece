package bumbums.puzzlepiece.task;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import bumbums.puzzlepiece.R;
import bumbums.puzzlepiece.model.Friend;
import bumbums.puzzlepiece.util.CircleTransform;
import bumbums.puzzlepiece.util.RealmBackupRestore;
import bumbums.puzzlepiece.util.Utils;
import io.realm.Realm;
import io.realm.RealmObjectSchema;
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
        //Log.d("###", "isDeleted=" + isDeleted + "//filePath=" + file.getAbsolutePath());
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
               // Log.d("###", "FIREBASE에서 파일삭제=" + fileName);
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
              //  Log.d("###", "FIREBASE에서 파일삭제 실패=" + fileName);

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
        final String newFilePath = Utils.decodeFile(context, filePath, 400, 400);
        final String fileName = new File(newFilePath).getName();
        //Log.d("###", "NEW=" + newFilePath + "//NAME=" + new File(newFilePath).getName());
        Uri newFileUri = Uri.fromFile(new File(newFilePath));

        //DB에 경로저장
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Friend friend = realm.where(Friend.class).equalTo(Friend.USER_ID, friendId).findFirst();
                friend.setProfilePath(newFilePath);
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
               // Log.d("###", "SERVICE FILEPATH=" + filePath_);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("###", "uploadFailure");

            }
        });
    }

    public static void downloadPhotoFromFirebase(final Context context, final String fileName, final Friend friend) {
        //파이어베이스로 부터
        // *사진이 있으면
        //  내부메모리에 저장한다.
        // *사진이 없거나 오류가 생기면
        // (네트워크가 연결되어 있는 상태라면 파일이 없는것이므로)
        //  friend 객체의  name, fileName, profileUrl 을 null 로 만들어 준다.
        final Realm realm = Realm.getDefaultInstance();

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference targetPath_ = storage.child("Photos/" + fileName);

      //  Log.d("###", "fileName=" + fileName);


        File folder = new File(context.getFilesDir() + "/profile_pictures");
        if (!folder.exists())
            folder.mkdir();

        final File file = new File(context.getFilesDir(), "/profile_pictures/" + fileName);
        try {
            file.createNewFile();
            targetPath_.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                   // Log.d("###", fileName + "추가완료");
                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                  //  Log.d("###", fileName + "추가실패");
                 //   Log.d("###",fileName+"의 photo 정보를 삭제합니다");

                    if(Utils.isInternetConnected(context)) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                friend.setProfileName(null);
                                friend.setProfileUrl(null);
                                friend.setProfilePath(null);
                            }
                        });
                    }
                    else{
                        Toast.makeText(context,"인터넷 연결이 안되어있습니다. 다시 시도해주세요",Toast.LENGTH_SHORT)
                                .show();
                    }
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadFriendPhoto(Context context,Friend friend,ImageView iv){
        String fileName = friend.getProfileName();
        if(fileName!=null) {
            File file = new File(friend.getProfilePath());
            if (file.exists()) {
                Picasso.with(context)
                        .load(file)
                        .transform(new CircleTransform())
                        .into(iv);
            } else {
                //데이터가 날라가서 DB만 복구한 경우
                //파일이 FIREBASE 서버에 있으면 내장메모리에 다운로드하고
                //없을경우 default 이미지를 보여준다.
                FirebaseTasks.downloadPhotoFromFirebase(context, fileName, friend);
                Picasso.with(context)
                        .load(friend.getProfileUrl())
                        .transform(new CircleTransform())
                        .error(R.drawable.default_user1)
                        .into(iv);
            }
        }else{ //파일이 아예 없는 경우
            Picasso.with(context)
                    .load(R.drawable.default_user1)
                    .transform(new CircleTransform())
                    .into(iv);
        }

    }

    public static void upLoadRealmFile(String googleId,Uri realmFileUri){
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        final StorageReference filePath_ = storage.child("UserRealm/" + googleId+"/"+RealmBackupRestore.EXPORT_REALM_FILE_NAME);

        filePath_.putFile( realmFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

               // Log.d("###", "uploadDone");
               // Log.d("###", "SERVICE FILEPATH=" + filePath_);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("###", "uploadFailure");

            }
        });
    }


   public static void loadBackupDataFromFirebase(final Context context, final String googleId, final RealmBackupRestore realmBackupRestore){
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference targetPath_ = storage.child("UserRealm/" + googleId+"/"+RealmBackupRestore.EXPORT_REALM_FILE_NAME);

        final File file = new File(context.getFilesDir(),"puzzle_piece.realm");
        try {
            file.createNewFile();
            targetPath_.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //Log.d("###", googleId + "폰으로 저장완료");
                    //저장 성공했을시 복구하고 앱 재시작.
                    realmBackupRestore.restore();
                    Utils.restartApp(context);
                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                   // Log.d("###", googleId + "폰으로 저장실패");
                    Toast.makeText(context,context.getString(R.string.restore_failure),Toast.LENGTH_SHORT).show();
                    if(!Utils.isInternetConnected(context)) {
                        Toast.makeText(context,"인터넷 연결이 안되어있습니다. 다시 시도해주세요",Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
