package com.oyster.mycity.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oyster.mycity.Problem;
import com.oyster.mycity.ProblemFactory;
import com.oyster.mycity.R;
import com.oyster.mycity.activity.MainActivity;
import com.oyster.mycity.activity.ProblemDetailsActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 25.07.14.
 */
public class ProblemsMapFragment extends MapFragment{
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
        CameraUpdate center = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(46.459972, 30.7117875),11,0,0));
        mapView.moveCamera(center);

        mapView.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });


        mapView.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                ((MainActivity)getActivity()).addProblem(latLng);
            }
        });
        mapView.setInfoWindowAdapter(new MyInfoWindowAdapter());


        mapView.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (int i = 0; i < mProblems.size(); i++) {
                    if (mProblems.get(i).getLocation().equals(marker.getPosition())) {
                        Intent intent = new Intent(getActivity(), ProblemDetailsActivity.class);
                        intent.putExtra("problem_number", i);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });

        queryProblems();
    }

    private void queryProblems() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Problem");
        query.include("user");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                mProblems.clear();
                for (ParseObject o : parseObjects)
                    mProblems.add(Problem.fromParseObject(o));
                ProblemFactory.setProblems(mProblems);
                drawPoints();
            }
        });
    }

    private void drawPoints() {
        for (Problem p : mProblems) {
            MarkerOptions marker = new MarkerOptions();
            marker.title(p.getTitle());
            marker.position(p.getLocation());
            int resourceId = -1;
            switch (p.getType()){
                case BENCH:
                    resourceId = R.drawable.bench;
                    break;
                case LIGHT:
                    resourceId = R.drawable.streetlight;
                    break;
                case TRASH_BIN:
                    resourceId = R.drawable.trashcan;
                    break;
                case REPAIR:
                    resourceId = R.drawable.repair;
                    break;
            }

            if (resourceId != -1)
                marker.icon(BitmapDescriptorFactory.fromResource(resourceId));
            mapView.addMarker(marker);

        }
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            Problem problem = null;
            for (Problem p : mProblems) {
                if (p.getLocation().equals(marker.getPosition())) {
                    problem = p;
                    break;
                }
            }

            if (problem == null)
                return null;

            LayoutInflater inflater = getActivity().getLayoutInflater();

            View view = inflater.inflate(R.layout.infowindow,null);
            ((ImageView)view.findViewById(R.id.problem_image)).setImageBitmap(problem.getImage());
            ((TextView)view.findViewById(R.id.problem_title)).setText(problem.getTitle());
             TextView ratingTextView = (TextView) view.findViewById(R.id.problem_rating);

            int rating = problem.getRating();
            String ratingString = String.valueOf(rating);
            if (rating > 0) {
                ratingTextView.setTextColor(getResources().getColor(R.color.green));
                ratingTextView.setText("+" + ratingString);
            } else if (rating < 0) {
                ratingTextView.setTextColor(Color.RED);
                ratingTextView.setText("-" + ratingString);
            }
            else
                ratingTextView.setText(R.string.no_rating);

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

}
