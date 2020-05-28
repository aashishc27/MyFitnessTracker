package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfitnesstracker.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import helper.CommonConstants;
import helper.Util;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SampleSignIn";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    ActionBar actionBar;
    SignInButton signInButton;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

        signInButton = findViewById(R.id.sign_in_button);
        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText("Login via Google");
        actionBar = new ActionBar(this);

        actionBar = findViewById(R.id.action_bar);

        actionBar.setLeftTitle("Login");

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.loginButton);

        sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();



        findViewById(R.id.sign_in_button).setOnClickListener(this);
        //findViewById(R.id.sign_out).setOnClickListener(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                openCollectData();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancel Login", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void openCollectData(){
        Util.showToast("Login Success",LoginActivity.this);
        editor.putBoolean(CommonConstants.IS_LOGIN,true);
        editor.apply();

        Intent intent = new Intent(LoginActivity.this,DataCollectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void onResume() {
        super.onResume();
        silentSignIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN: {
                GoogleSignIn.getSignedInAccountFromIntent(data)
                        .addOnSuccessListener(this, new OnSuccessListener<GoogleSignInAccount>() {
                            @Override
                            public void onSuccess(GoogleSignInAccount account) {
                                Log.d(TAG, "Sign in success");
                                onSignIn(account);
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Sign in failed", e);
                            }
                        });
                break;
            }
           // callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button: {
                signIn();
                break;
            }
//            case R.id.sign_out: {
//                signOut();
//                break;
//            }
        }
    }

    private void silentSignIn() {
        mGoogleSignInClient.silentSignIn()
                .addOnSuccessListener(this, new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount account) {
                        Log.d(TAG, "Silent sign in success");
                        onSignIn(account);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Silent sign in failed", e);
                    }
                });
    }

    private void signIn() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Sign out success");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Sign out failed", e);
                    }
                });
    }

    private void onSignIn(GoogleSignInAccount account) {
        openCollectData();
        account.getDisplayName();
        account.getEmail();

//        final GamesClient gamesClient = Games.getGamesClient(this, account);
//        gamesClient.setViewForPopups(findViewById(android.R.id.content));
//        gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
    }
}