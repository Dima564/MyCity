package com.oyster.mycity;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

/**
 * Created by dima on 25.07.14.
 */
public class Facebook {

    public static void setProfilePicture(final Context context, final ImageView profilePicture,
                                         final TextView firstName, final TextView lastName) {
        Session session = Session.getActiveSession();
        Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                try {
                    firstName.setText(user.getFirstName());
                    lastName.setText(user.getLastName());
                    Picasso.with(context).load("https://graph.facebook.com/" + user.getId() + "/picture?type=large").into(profilePicture);
                } catch (Exception e) {
                }
            }
        }).executeAsync();

    }

    public static void logout(Context context) {
        clearData(context);
        ParseUser.logOut();
        Session session = Session.getActiveSession();
        session.closeAndClearTokenInformation();
    }

    private static void clearData(Context context) {

    }
}
