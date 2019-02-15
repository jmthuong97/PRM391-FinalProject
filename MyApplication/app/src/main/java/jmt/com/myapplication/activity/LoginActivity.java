package jmt.com.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import jmt.com.myapplication.R;
import jmt.com.myapplication.helpers.Helper;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private static final String FACEBOOK = "FACEBOOK";
    private static final String GOOGLE = "GOOGLE";
    private static final int GOOGLE_SIGN_IN = 1;

    View progress_circular;
    SignInButton buttonGoogleLogin;
    LoginButton buttonFacebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progress_circular = findViewById(R.id.progress_circular);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        loginWithFacebook();
        loginWithGoogle();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (Helper.GetCurrentUser() != null) updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            try {
                // Google Sign In was successful, authenticate with Firebase
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleAccessToken(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(GOOGLE, "Google sign in failed", e);
                setStateLogin(false);
                Helper.makeToastMessage("Google sign in failed !", LoginActivity.this);
            }
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void loginWithGoogle() {
        // Initialize Google Login button
        buttonGoogleLogin = findViewById(R.id.btn_google);
        buttonGoogleLogin.setColorScheme(SignInButton.COLOR_DARK);
        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStateLogin(true);
                Intent signInIntent = Helper.getGoogleSignInClient(LoginActivity.this).getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
            }
        });
    }

    private void loginWithFacebook() {
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        buttonFacebookLogin = findViewById(R.id.btn_facebook);
        buttonFacebookLogin.setReadPermissions("email", "public_profile");
        buttonFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStateLogin(true);
            }
        });
        buttonFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(FACEBOOK, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(FACEBOOK, "facebook:onCancel");
                setStateLogin(false);
                Helper.makeToastMessage("Facebook sign in cancel !", LoginActivity.this);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(FACEBOOK, "facebook:onError", error);
                setStateLogin(false);
                Helper.makeToastMessage("Facebook sign in error !", LoginActivity.this);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) updateUI();
                        else {
                            setStateLogin(false);
                            Helper.makeToastMessage("Facebook sign in error !", LoginActivity.this);
                        }
                    }
                });
    }


    private void handleGoogleAccessToken(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) updateUI();
                        else {
                            setStateLogin(false);
                            Helper.makeToastMessage("Google sign in failed !", LoginActivity.this);
                        }
                    }
                });
    }

    private void updateUI() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void setStateLogin(boolean status) {
        if (status) {
            buttonFacebookLogin.setVisibility(View.GONE);
            buttonGoogleLogin.setVisibility(View.GONE);
            progress_circular.setVisibility(View.VISIBLE);
        } else {
            buttonFacebookLogin.setVisibility(View.VISIBLE);
            buttonGoogleLogin.setVisibility(View.VISIBLE);
            progress_circular.setVisibility(View.GONE);
        }
    }
}
