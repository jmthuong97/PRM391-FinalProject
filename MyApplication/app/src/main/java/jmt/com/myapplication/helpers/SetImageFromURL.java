package jmt.com.myapplication.helpers;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class SetImageFromURL {
    private String photoURL;
    private String providerId;
    private static final String GOOGLE = "google.com";

    public SetImageFromURL(String photoURL, String providerId) {
        this.photoURL = photoURL;
        this.providerId = providerId;
    }

    public void setImage(ImageView imageView) {
        // get max size image
        if (providerId.equals(GOOGLE)) photoURL.replace("/s96-c/", "/s300-c/");
        else photoURL = photoURL + "?type=large";

        Picasso.get().load(photoURL).into(imageView);
    }
}
