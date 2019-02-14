package jmt.com.myapplication.helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import jmt.com.myapplication.BuildConfig;
import jmt.com.myapplication.interfaces.IAccessTokenCallback;
import jmt.com.myapplication.models.User;

public class Helper {
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

    public static GoogleSignInClient getGoogleSignInClient(Activity activity) {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.RequestIdToken)
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(activity, gso);
    }

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
}
