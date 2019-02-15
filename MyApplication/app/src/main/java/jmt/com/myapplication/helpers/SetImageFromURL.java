package jmt.com.myapplication.helpers;

import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class SetImageFromURL {
    private String photoURL;
    private String providerId;
    private static final String GOOGLE = "google.com";
    private static final String FACEBOOK = "facebook.com";

    public SetImageFromURL(String photoURL, String providerId) {
        this.photoURL = photoURL;
        this.providerId = providerId;
    }

    public void setImage(ImageView imageView) {
        // get max size image
        if (providerId.equals(GOOGLE)) photoURL = photoURL.replace("/s96-c/", "/s300-c/");
        else if (providerId.equals(FACEBOOK)) photoURL = photoURL + "?type=large";

        Picasso.get().load(photoURL).resize(192, 192).into(imageView);
    }
}
