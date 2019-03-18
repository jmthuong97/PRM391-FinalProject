package jmt.com.myapplication.helpers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jmt.com.myapplication.BuildConfig;
import jmt.com.myapplication.R;
import jmt.com.myapplication.models.Group;
import jmt.com.myapplication.models.Message;
import jmt.com.myapplication.models.User;
import jmt.com.myapplication.models.UserToken;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Helper {
    private static final String GOOGLE = "google.com";
    private static final String FACEBOOK = "facebook.com";
    private static final String LEGACY_SERVER_KEY = "AIzaSyC9XSD_4d8sPvgHysifZDVXDXY3deoI2CA";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static void sendNotification(final String regToken, final String group, final String sender, final String msg) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Log.i("testsend", "sending to token: " + regToken);
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", sender + ": " + msg);
                    dataJson.put("title", group);
                    json.put("notification", dataJson);
                    json.put("to", regToken);
                    json.put("priority", "high");

                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + LEGACY_SERVER_KEY)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.d("testsend", "response: " + finalResponse);

                } catch (Exception e) {
                    Log.d("nothing", "failed to send cause: " + e);
                }
                return null;
            }
        }.execute();

    }

    static DatabaseReference databaseReference;

    public static void sendMessage(final String content, String type, String fileURL, String displayName, String gID) {
        Log.d("bigboy", "sending bigboi msg");

        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        String idMessage = databaseReference.push().getKey();

        Message message = new Message();
        message.setContent(content);
        message.setFileURL(fileURL);
        message.setType(type);
        message.setId(idMessage);
        message.setSender(getCurrentUser());
        Log.d("bigboy", "bigboi msg sent with ID" + gID);

        databaseReference.child(gID).child(idMessage).setValue(message);

        sendNoti(content, gID, displayName);
    }

    public static void sendNoti(final String contentNoti, final String gID, final String displayName) {
        Log.d("bigboy", "sending bigboi noti");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference searchRef = rootRef.child("groups");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> listUser = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String groupID = ds.getValue(Group.class).getId();
                    Log.d("test send", "current groups:" + groupID);
                    Log.d("test send", gID);

                    if (groupID.equalsIgnoreCase(gID)) {
                        List<String> membersID = ds.getValue(Group.class).getMembers();
                        Log.d("test send", "list of member from group:" + membersID.toString());
                        for (int i = 0; i < membersID.size(); i++) {
                            if (membersID.get(i).equalsIgnoreCase(getCurrentUser().getUid())) {
                                Log.d("test send", " member send group:" + getCurrentUser().getUid());
                                membersID.remove(i);
                            }
                        }
                        Log.d("test send", "list of member from group:" + membersID.toString());


                        for (int i = 0; i < membersID.size(); i++) {
                            final String currentMem = membersID.get(i);
                            Log.d("test send", "current member: " + currentMem);

                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference searchRef = rootRef.child("userToken");
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        String value = ds.getValue(UserToken.class).getUid();
                                        Log.d("test send", "user id in token tbl: " + value);

                                        if (value.equalsIgnoreCase(currentMem)) {
                                            Log.d("test send", "found user id and sending to token:" + ds.getValue(UserToken.class).getToken());

                                            Helper.sendNotification(ds.getValue(UserToken.class).getToken(),
                                                    displayName, Helper.getCurrentUser().getDisplayName(),
                                                    contentNoti);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            };
                            searchRef.addListenerForSingleValueEvent(valueEventListener);
                        }
                        break;
                    }
                }
                Log.d("TAG", listUser.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        searchRef.addListenerForSingleValueEvent(valueEventListener);
    }

    static List<String> listUser = new ArrayList<>();
    static List<String> listToken = new ArrayList<>();
    static boolean doneAdd = false;
    static boolean add = true;

    public static void initToken() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference searchRef = rootRef.child("userToken");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (doneAdd == false) {
                        String value = ds.getValue(UserToken.class).getUid();
                        String token = ds.getValue(UserToken.class).getToken();

                        listUser.add(value);
                        listToken.add(token);
                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        if (!task.isSuccessful()) {
                                            return;
                                        }
                                        // Get new Instance ID token
                                        final String tokenStr = task.getResult().getToken();
                                        Log.d("mytoken", tokenStr);
                                        for (int i = 0; i < listUser.size(); i++) {
                                            Log.d("special", listUser.toString());
                                            if (listUser.get(i).equalsIgnoreCase(getCurrentUser().getUid()) && listToken.get(i).equalsIgnoreCase(tokenStr)) {
                                                add = false;
                                                break;
                                            }
                                        }
                                        Log.d("TAG", getCurrentUser().getUid() + ": " + add);
                                        if (add == true && doneAdd == false) {
                                            doneAdd = true;
                                            DatabaseReference databaseReference;
                                            databaseReference = FirebaseDatabase.getInstance().getReference("userToken");
                                            String idToken = databaseReference.push().getKey();
                                            UserToken token = new UserToken();
                                            token.setToken(tokenStr);
                                            token.setUid(getCurrentUser().getUid());
                                            databaseReference.child(idToken).setValue(token);
                                            return;
                                        }
                                    }
                                });
                    }
                }
                Log.d("TAG", listUser.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        searchRef.addListenerForSingleValueEvent(valueEventListener);
    }

    static String tempGID;

    public static void notiRep(final String msg, final String displayName) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference searchRef = rootRef.child("groups");
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String value = ds.getValue(Group.class).getDisplayName();
                    Log.d("bigboy", "bigboi Name " + value);

                    if (value.equalsIgnoreCase(displayName)) {
                        Log.d("bigboy", "bigboi ding ding ding: " + value + " = " + displayName);
                        tempGID = ds.getValue(Group.class).getId();
                        Log.d("bigboy", "bigboi ding ding ding ID: " + tempGID);
                        sendMessage(msg, Message.TEXT, "NONE", displayName, tempGID);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        searchRef.addListenerForSingleValueEvent(valueEventListener);

    }


    public static void getAccessToken(final IAccessTokenCallback iAccessTokenCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("ACCESS_TOKEN", task.getResult().getToken());
                                iAccessTokenCallback.onSuccessGetAccessToken(task.getResult().getToken());
                            }
                        }
                    });
        }
    }

    // get information of current user
    public static User getCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = null;
        if (currentUser != null) {
            user = new User();
            String email = currentUser.getProviderData().get(0).getEmail();
            user.setDisplayName(currentUser.getDisplayName());
            user.setEmail(email);
            user.setPhoneNumber(currentUser.getPhoneNumber());
            user.setPhotoURL(currentUser.getPhotoUrl().toString());
            user.setProviderId(currentUser.getProviders().get(0));
            user.setUid(currentUser.getUid());
        }
        return user;
    }

    // set image from user information(auth firebase)
    public static void setAvatar(Context context, String photoURL, String providerId, ImageView imageView) {
        // get max size image
        if (providerId.equals(GOOGLE)) photoURL = photoURL.replace("/s96-c/", "/s300-c/");
        else if (providerId.equals(FACEBOOK)) photoURL = photoURL + "?type=large";
        Picasso.with(context).load(photoURL)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.no_image_available)
                .resize(192, 192).into(imageView);
    }

    // set image from url
    public static void setImageFromURL(Context context, String photoURL, ImageView imageView) {
        Picasso.with(context).load(photoURL)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.no_image_available)
                .into(imageView);
    }

    public static void setImageRoundCorner(Context context, String photoURL, ImageView imageView) {
        Picasso.with(context).load(photoURL)
                .priority(Picasso.Priority.HIGH)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.no_image_available)
                .transform(new RoundedCornersTransformation(25, 0))
                .into(imageView);
    }

    // check uri is image file
    public static boolean isImageFile(Uri path, Context context) {
        String mimeType = null;
        if (path.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(path);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(path
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType != null && mimeType.startsWith("image");
    }

    // get File name from Uri file path
    public static String getFileName(Uri filePath, Activity activity) {
        String fileName = null;
        if (filePath.toString().startsWith("content://")) {
            try (Cursor cursor = activity.getContentResolver().query(filePath, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        } else if (filePath.toString().startsWith("file://")) {
            File myFile = new File(filePath.toString());
            fileName = myFile.getName();
        }
        return fileName;
    }

    // reduce quality of image
    public static byte[] reduceSizeImage(Uri filePath, Activity activity) {
        Bitmap bmp = null;
        byte[] data = new byte[0];
        try {
            bmp = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // get size of file
    public static long getSizeFile(Uri filePath, Activity activity) {
        long size = 0;
        if (filePath.toString().startsWith("content://")) {
            try (Cursor cursor = activity.getContentResolver().query(filePath, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    size = cursor.getLong(sizeIndex);
                }
            }
        } else if (filePath.toString().startsWith("file://")) {
            File myFile = new File(filePath.toString());
            size = myFile.length();
        }
        return size;
    }

    // encode QR code from content
    public static String encodeQRCode(String content) {
        return BuildConfig.QRSecretKey.concat(content);
    }

    // Check if the QR code is in my system?
    public static Boolean isCorrectQRCode(String code) {
        return code.contains(BuildConfig.QRSecretKey);
    }

    public static String decodeQRCode(String code) {
        return code.replace(BuildConfig.QRSecretKey, "");
    }

    public static void setColorToolAndStatusBar(Toolbar toolbar, Window window, String mainColor) {
        toolbar.setBackgroundColor(AutoColor.COLOR(mainColor).Main());
        window.setStatusBarColor(AutoColor.COLOR(mainColor).Dark());
    }

    // make a toast message
    public static void makeToastMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
