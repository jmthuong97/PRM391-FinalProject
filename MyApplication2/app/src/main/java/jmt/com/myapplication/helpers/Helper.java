package jmt.com.myapplication.helpers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import jmt.com.myapplication.BuildConfig;
import jmt.com.myapplication.R;
import jmt.com.myapplication.models.User;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class Helper {
    private static final String GOOGLE = "google.com";
    private static final String FACEBOOK = "facebook.com";

    // get information of current user
    public static User getCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = null;
        if (currentUser != null) {
            currentUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("ACCESS_TOKEN", task.getResult().getToken());
                            }
                        }
                    });

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
