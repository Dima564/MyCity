package com.oyster.mycity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dima on 18.07.14.
 */
public class LoginActivity extends FragmentActivity {
    public static final String TAG = "LoginActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton;
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginFacebookButtonClicked();
            }
        });
    }

    private void onLoginFacebookButtonClicked() {

        final AlertDialog dialog =
                new AlertDialog.Builder(this).setTitle("Logging in...").create();
        dialog.show();


        Log.d(TAG, "FacebookLoginButton Clicked");
        List<String> permissions = Arrays.asList("public_profile", "user_friends");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                Log.d(TAG, "Facebook login callback");

                if (user == null) {
                    Log.d(TAG,
                            "Uh oh. The user cancelled the Facebook login.");
                } else {
                    Log.d(TAG, "Managed to get here");
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    dialog.dismiss();
                    LoginActivity.this.finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

}
