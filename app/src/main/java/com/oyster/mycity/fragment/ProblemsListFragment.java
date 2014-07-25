package com.oyster.mycity.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oyster.mycity.Problem;
import com.oyster.mycity.ProblemFactory;
import com.oyster.mycity.R;
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
public class ProblemsListFragment extends ListFragment {
    private static final String TAG = "ProblemsListFragment";
    List<Problem> mProblems = new ArrayList<>();
    ArrayAdapter<Problem> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState == null) {
            mAdapter = new ProblemAdapter(getActivity(),R.layout.list_item_problem,mProblems);
            setListAdapter(mAdapter);

            queryProblems();
        }


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), ProblemDetailsActivity.class);
                i.putExtra("problem_number",position);
                startActivity(i);
            }
        });
    }

    private void queryProblems() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Problem");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                mProblems.clear();
                for (ParseObject o : parseObjects)
                    mProblems.add(Problem.fromParseObject(o));
                mAdapter.notifyDataSetChanged();
                ProblemFactory.setProblems(mProblems);
            }
        });
    }




    private class ProblemAdapter extends ArrayAdapter<Problem> {
        private Context mContext;
        List<Problem> mProblems;

        public ProblemAdapter(Context context, int resource, List<Problem> objects) {
            super(context, resource, objects);
            mContext = context;
            mProblems = objects;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.d(TAG,"getView()");
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater)
                        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item_problem, null);
            }

            TextView titleTextView = (TextView) v.findViewById(R.id.problem_title);
            TextView descriptionTextView = (TextView) v.findViewById(R.id.problem_description);
            ImageView imageView = (ImageView) v.findViewById(R.id.problem_image);
            TextView ratingTextView = (TextView) v.findViewById(R.id.problem_rating);

            Problem problem = getItem(position);

            titleTextView.setText(problem.getTitle());

            imageView.setImageBitmap(problem.getImage());

            String description = problem.getDescription();
            if (description.length() > 140)
                description = description.substring(0,138) + "...";
            descriptionTextView.setText(description);


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
            return v;
        }

    }

}
