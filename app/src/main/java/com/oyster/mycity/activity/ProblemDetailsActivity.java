package com.oyster.mycity.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oyster.mycity.Problem;
import com.oyster.mycity.ProblemFactory;
import com.oyster.mycity.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 25.07.14.
 */
public class ProblemDetailsActivity extends Activity {

    private static final String TAG = "ProblemDetailsActivity";
    private static final int MIN_COMMENT_LENGTH = 5;
    Problem mProblem;
    ParseObject mParseObjectProblem;
    List<ParseObject> mComments = new ArrayList<>();
    private boolean mIsFavorite = false;

    private ImageView imageView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView authorTextView;
    private TextView ratingTextView;
    private EditText commentEditText;
    private Button postCommentButton;


    private ImageButton likeButton;
    private ImageButton dislikeButton;
    private ImageButton favouriteButton;

    private ListView mCommentsListView;

    private CommentsAdapter mCommentsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_details);


        mCommentsListView = (ListView) findViewById(R.id.comments_container);

        if (savedInstanceState == null) {
            mCommentsAdapter = new CommentsAdapter(this,mComments);
            mCommentsListView.setAdapter(mCommentsAdapter);
        }


        int position = getIntent().getIntExtra("problem_number", -1);
        if (position == -1)
            finish();
        else
            mProblem = ProblemFactory.getProblem(position);

        imageView = (ImageView) findViewById(R.id.problem_image);
        titleTextView = (TextView) findViewById(R.id.problem_title);
        descriptionTextView = (TextView) findViewById(R.id.problem_description);
        authorTextView = (TextView) findViewById(R.id.problem_author);
        ratingTextView = (TextView) findViewById(R.id.problem_rating);
        commentEditText = (EditText) findViewById(R.id.comment_edittext);
        postCommentButton = (Button) findViewById(R.id.post_comment_button);
        likeButton = (ImageButton) findViewById(R.id.like_button);
        dislikeButton = (ImageButton) findViewById(R.id.dislike_button);
        favouriteButton = (ImageButton) findViewById(R.id.favorite);


        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = commentEditText.getText().toString();
                if (text.length() >= MIN_COMMENT_LENGTH)
                {
                    ParseObject comment = new ParseObject("Comment");
                    comment.put("user", ParseUser.getCurrentUser());
                    comment.put("text", text);
                    comment.put("problem", mParseObjectProblem);
                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null)
                                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG)
                                        .show();
                            queryComments();
                        }

                    });
                    mComments.add(comment);
                    mCommentsAdapter.notifyDataSetChanged();
                    commentEditText.setText("");

                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
                }
                else
                    Toast.makeText(ProblemDetailsActivity.this,
                            R.string.too_short,Toast.LENGTH_SHORT).show();

            }
        });

        setFavourite(favouriteButton);


        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementRating(1);
            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementRating(-1);
            }
        });


        imageView.setImageBitmap(mProblem.getImage());
        titleTextView.setText(mProblem.getTitle());
        descriptionTextView.setText(mProblem.getDescription());

        String authorText = getResources().getString(R.string.added_by) + " " + mProblem.getAuthor();
        authorTextView.setText(authorText);

        int rating = mProblem.getRating();
        String ratingString = String.valueOf(rating);
        if (rating > 0) {
            ratingTextView.setTextColor(getResources().getColor(R.color.green));
            ratingTextView.setText("+" + ratingString);
        } else if (rating < 0) {
            ratingTextView.setTextColor(Color.RED);
            ratingTextView.setText("-" + ratingString);
        } else
            ratingTextView.setText(R.string.no_rating);

    }

    private void incrementRating(final int num) {
        mProblem.setRating(mProblem.getRating() + num);
        setRating(ratingTextView, mProblem.getRating());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Problem")
                .whereEqualTo("title", mProblem.getTitle())
                .whereEqualTo("description", mProblem.getDescription())
                .setLimit(1);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ParseObject problem = parseObjects.get(0);
                int rating = problem.getInt("rating");
                rating += num;
                problem.put("rating", rating);
                problem.saveInBackground();
                setRating(ratingTextView, rating);
                mProblem.setRating(rating);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryComments();

    }

    private void setFavourite(final ImageButton favouriteButton) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Problem")
                .whereEqualTo("title", mProblem.getTitle())
                .whereEqualTo("description", mProblem.getDescription())
                .setLimit(1);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ParseObject problem = parseObjects.get(0);
                JSONArray array = problem.getJSONArray("favorite");
                if (array == null) {
                    favouriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                    return;
                }

                for (int i = 0; i < array.length(); i++) {
                    String userId = null;
                    try {
                        userId = array.getString(i);
                    } catch (JSONException e1) {
                        Log.e(TAG, "SOMEONE BROKE SOMETHING");
                    }

                    if (userId.equals(ParseUser.getCurrentUser().getObjectId())) {
                        favouriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                        mIsFavorite = true;
                        return;
                    }

                }
                favouriteButton.setImageResource(android.R.drawable.btn_star_big_off);

            }
        });
    }

    private void toggleFavorite() {
        mIsFavorite = !mIsFavorite;
        if (mIsFavorite)
            favouriteButton.setImageResource(android.R.drawable.btn_star_big_on);
        else
            favouriteButton.setImageResource(android.R.drawable.btn_star_big_off);



        ParseQuery<ParseObject> query = ParseQuery.getQuery("Problem")
                .whereEqualTo("title", mProblem.getTitle())
                .whereEqualTo("description", mProblem.getDescription())
                .setLimit(1);

        query.findInBackground(new FindCallback<ParseObject>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ParseObject problem = parseObjects.get(0);
                JSONArray array = problem.getJSONArray("favorite");
                if (mIsFavorite) {
                    if (array == null) {
                        array = new JSONArray();
                    }
                    array.put(ParseUser.getCurrentUser().getObjectId());
                }
                else {
                    for (int i = 0; i < array.length(); i++) {
                        String userId = null;
                        try {
                            userId = array.getString(i);
                        } catch (JSONException e1) {
                            Log.e(TAG, "SOMEONE BROKE SOMETHING");
                        }

                        if (userId.equals(ParseUser.getCurrentUser().getObjectId())) {
                            array.remove(i);
                            break;
                        }
                    }
                }
                problem.put("favorite",array);
                problem.saveInBackground();
            }
        });
    }

    private void setRating(TextView editText, int rating) {
        String ratingString = String.valueOf(rating);
        if (rating > 0) {
            editText.setTextColor(getResources().getColor(R.color.green));
            editText.setText("+" + ratingString);
        } else if (rating < 0) {
            editText.setTextColor(Color.RED);
            editText.setText("-" + ratingString);
        } else
            editText.setText(R.string.no_rating);
    }


    private void queryComments() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Problem")
                .whereEqualTo("title", mProblem.getTitle())
                .whereEqualTo("description", mProblem.getDescription())
                .setLimit(1);

        query.orderByDescending("createdAt");

        query.include("user");

        query.findInBackground(new FindCallback<ParseObject>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                mParseObjectProblem = parseObjects.get(0);

                ParseQuery<ParseObject> commentsQuery = ParseQuery.getQuery("Comment");
                commentsQuery.whereEqualTo("problem",mParseObjectProblem);
                commentsQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> comments, ParseException e) {
                        mComments.clear();
                        mComments.addAll(comments);
                        mCommentsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private class CommentsAdapter extends ArrayAdapter<ParseObject> {

        public CommentsAdapter(Context context, List<ParseObject> comments) {
            super(context, R.layout.comment,comments);
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.comment,null);
            }

            final ImageView userImageView = (ImageView) convertView.findViewById(R.id.user_picture);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            final TextView userNameTextView = (TextView) convertView.findViewById(R.id.user_name);



            mComments.get(position).getParseObject("user").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    userNameTextView.setText(parseObject.getString("name"));


                    try {
                        ParseFile image = parseObject.getParseFile("picture");
                        byte[] data = image.getData();
                        Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
                        userImageView.setImageBitmap(picture);

                    } catch (Exception ex) {
                        Log.e(TAG,"Unable to cast file to bitmap");
                    }


                }
            });
            textView.setText(mComments.get(position).getString("text"));

            return convertView;
        }
    }

}
