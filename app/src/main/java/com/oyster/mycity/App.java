package com.oyster.mycity;

import android.app.Application;

import com.oyster.mycity.activity.MainActivity;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.PushService;

/**
 * Created by dima on 18.07.14.
 */
public class App extends Application {
    public void onCreate() {
        Parse.initialize(this, "8icCprd0wWojSbkBXNexmODm9McLa7HcyPh5J0bF", "VC2Eq5AsXBu2ygy6RpNc68giVzRo8NMn3MKxDGEN");
        ParseFacebookUtils.initialize(getString(R.string.app_id));
        PushService.setDefaultPushCallback(this, MainActivity.class);

    }
}
