package com.oyster.mycity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

/**
 * Created by dima on 25.07.14.
 */
public class Problem {
    private static final String TAG = "Problem";
    private String title;
    private String description;
    private LatLng location;
    private Bitmap image;
    private int rating;
    private ProblemType type;
    private String author;

    public Problem(String title, String description, LatLng location, Bitmap image, ProblemType type) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.image = image;
        this.type = type;
    }


    public Problem() {}

    public static Problem fromParseObject(ParseObject object) {
        Problem problem = new Problem();
        problem.title = object.getString("title");
        problem.description = object.getString("description");
        problem.rating = object.getInt("rating");
        problem.type = ProblemType.valueOf(object.getString("type"));
        ParseGeoPoint location = object.getParseGeoPoint("location");
        problem.location = new LatLng(location.getLatitude(),location.getLongitude());
        problem.author = object.getParseObject("user").getString("name");


        try {
            ParseFile image = object.getParseFile("image");
            byte[] data = image.getData();
            problem.image = BitmapFactory.decodeByteArray(data,0,data.length);
        } catch (Exception e) {
            Log.e(TAG,"Unable to cast file to bitmap");
        }

        return problem;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void save() {
        new SaveObject().execute(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


    public void setType(ProblemType type) {
        Log.d(TAG,type.toString());
        this.type = type;
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

    public Bitmap getImage() {
        return image;
    }

    public int getRating() {
        return rating;
    }
    public ProblemType getType() {
        return type;
    }

    private static class SaveObject extends AsyncTask<Problem,Void,Void> {

        @Override
        protected Void doInBackground(Problem... params) {
            Problem problem = params[0];
            ParseObject object = new ParseObject("Problem");
            object.put("user", ParseUser.getCurrentUser());
            object.put("title",problem.title);
            object.put("description",problem.description);
            object.put("rating",problem.rating);
            object.put("type",problem.type.toString());
            ParseGeoPoint location = new ParseGeoPoint(problem.location.latitude,
                    problem.location.longitude);
            object.put("location",location);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (problem.image != null) {
                problem.image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] data = stream.toByteArray();
                ParseFile image = new ParseFile(data);

                object.put("image", image);
            }

            try {
                object.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
