package jmt.com.myapplication.helpers;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import jmt.com.myapplication.interfaces.IAccessTokenCallback;
import jmt.com.myapplication.models.User;

public class Helper {
    static void GetAccessToken(final IAccessTokenCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful())
                                callback.onSuccessGetAccessToken(task.getResult().getToken());
                        }
                    });
        }
    }

    public static User GetCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = new User();
        user.setDisplayName(currentUser.getDisplayName());
        user.setEmail(currentUser.getEmail());
        user.setPhoneNumber(currentUser.getPhoneNumber());
        user.setPhotoURL(currentUser.getPhotoUrl().toString());
        user.setProviderId(currentUser.getProviders().get(0));
        user.setUid(currentUser.getUid());
        return user;
    }
}
