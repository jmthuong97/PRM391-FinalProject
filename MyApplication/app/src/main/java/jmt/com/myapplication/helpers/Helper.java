package jmt.com.myapplication.helpers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Window;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import jmt.com.myapplication.BuildConfig;
import jmt.com.myapplication.activity.LoginActivity;
import jmt.com.myapplication.interfaces.IAccessTokenCallback;
import jmt.com.myapplication.models.User;

public class Helper {

    // get accessToken of current user login
    static void GetAccessToken(final IAccessTokenCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("ACCESS_TOKEN", task.getResult().getToken());
                                callback.onSuccessGetAccessToken(task.getResult().getToken());
                            }
                        }
                    });
        }
    }

    // get GoogleSignInClient for google login
    public static GoogleSignInClient getGoogleSignInClient(Activity activity) {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.RequestIdToken)
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(activity, gso);
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

    // get information of current user
    public static User GetCurrentUser() {
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

    //
    public static void setColorToolAndStatusBar(Toolbar toolbar, Window window, String mainColor) {
        toolbar.setBackgroundColor(AutoColor.COLOR(mainColor).Main());
        window.setStatusBarColor(AutoColor.COLOR(mainColor).Dark());
    }

    // make a toast message
    public static void makeToastMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
