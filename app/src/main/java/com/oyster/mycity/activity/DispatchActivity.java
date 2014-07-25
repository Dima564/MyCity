package com.oyster.mycity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

/**
 * Created by dima on 18.07.14.
 */
public class DispatchActivity extends Activity {


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activity;
        if (ParseUser.getCurrentUser() == null)
            activity = new Intent(this,LoginActivity.class);
        else
            activity = new Intent(this,MainActivity.class);

        startActivity(activity);
        finish();
    }
}
