package com.oyster.mycity;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.net.URI;

/**
 * Created by dima on 25.07.14.
 */
public class Problem {
    private String title;
    private String description;
    private LatLng location;
    private Bitmap thumbnail;
    private URI imageURI;
    private int rating;

    public static Problem fromParseObject(ParseObject object) {
        // TODO complete it
        return null;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LatLng getLocation() {
        return location;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public URI getImageURI() {
        return imageURI;
    }

    public int getRating() {
        return rating;
    }

    public void save() {

    }


    private static class SaveObject extends AsyncTask<Problem,Void,Void> {

        @Override
        protected Void doInBackground(Problem... params) {
            Problem problem = params[0];
            ParseObject object = new ParseObject("Problem");
            object.put("user", ParseUser.getCurrentUser());
            object.put("title",problem.getTitle());
            object.put("description",problem.getDescription());
//            object.put("");
            return null;
        }
    }
}
