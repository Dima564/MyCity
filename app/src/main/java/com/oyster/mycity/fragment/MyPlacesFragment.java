package com.oyster.mycity.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.oyster.mycity.Problem;
import com.oyster.mycity.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 26.07.14.
 */
public class MyPlacesFragment extends MapFragment {
    GoogleMap mapView;
    List<Problem> mProblems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView = getMap();
        CameraUpdate center = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(46.459972, 30.7117875), 11, 0, 0));
        mapView.moveCamera(center);

        mapView.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addPlace(latLng);
            }
        });

        mapView.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                sendNotification(latLng);
            }
        });

        queryPlaces();
    }

    private void sendNotification(final LatLng latLng) {
        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(parseUsers != null)
                for (ParseUser user : parseUsers) {
                    JSONArray array = user.getJSONArray("my_places");
                    if (array != null)
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject object = (JSONObject) array.get(i);
                                ParseGeoPoint point = new ParseGeoPoint(object.getDouble("latitude"),
                                        object.getDouble("longitude"));

                                ParseGeoPoint notificationLocation = new ParseGeoPoint(latLng.latitude,latLng.longitude);
                                if (notificationLocation.distanceInKilometersTo(point) < 0.4) {
                                    List<String> channels = new ArrayList<String>();
                                    String userName = user.getString("name");
                                    String channel = userName.replace(" ","_");
                                    channels.add(channel);
                                    ParsePush parsePush = new ParsePush();
                                    parsePush.setMessage("Something bad happened");
                                    parsePush.setChannels(channels);
                                    parsePush.sendInBackground(new SendCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Toast.makeText(getActivity(),
                                                    e == null ? "SUCCESS" : e.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                }

            }
        });
    }

    private void queryPlaces() {
        ParseUser user = ParseUser.getCurrentUser();
        JSONArray array = user.getJSONArray("my_places");
        if (array != null)
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                LatLng position = new LatLng(object.getDouble("latitude"),object.getDouble("longitude"));


                addCircleToMap(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPlace(LatLng latLng) {
//        MarkerOptions marker = new MarkerOptions();
//        marker.position(latLng);
//        mapView.addMarker(marker);

        addCircleToMap(latLng);


        ParseUser user = ParseUser.getCurrentUser();
        JSONArray array = user.getJSONArray("my_places");
        if (array == null) {
            array = new JSONArray();
        }
        array.put(new ParseGeoPoint(latLng.latitude,latLng.longitude));
        user.put("my_places", array);
        user.saveInBackground();

    }

    private void addCircleToMap(LatLng latLng) {

        // circle settings
        int radiusM = 400;

        // draw circle
        int d = 500; // diameter
        Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.green));
        c.drawCircle(d/2, d/2, d/2, p);

        // generate BitmapDescriptor from circle Bitmap
        BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);

// mapView is the GoogleMap
        mapView.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(latLng,radiusM*2,radiusM*2).
                transparency(0.4f));
    }
}
