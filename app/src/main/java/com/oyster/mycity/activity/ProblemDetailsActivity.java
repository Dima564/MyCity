package com.oyster.mycity.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.oyster.mycity.Problem;
import com.oyster.mycity.ProblemFactory;
import com.oyster.mycity.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by dima on 25.07.14.
 */
public class ProblemDetailsActivity extends Activity {

    Problem mProblem;

    private ImageView imageView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView authorTextView;
    private TextView ratingTextView;
    private EditText commentEditText;
    private Button postCommentButton;

    private ImageButton likeButton;
    private ImageButton dislikeButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_details);
        int position = getIntent().getIntExtra("problem_number",-1);
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
        }
        else
            ratingTextView.setText(R.string.no_rating);

    }

    private void incrementRating(final int num) {
        mProblem.setRating(mProblem.getRating() + num);
        setRating(ratingTextView,mProblem.getRating());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Problem")
            .whereEqualTo("title",mProblem.getTitle())
                .whereEqualTo("description",mProblem.getDescription())
                .setLimit(1);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ParseObject problem = parseObjects.get(0);
                int rating = problem.getInt("rating");
                rating += num;
                problem.put("rating",rating);
                problem.saveInBackground();
                setRating(ratingTextView,rating);
                mProblem.setRating(rating);

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
        }
        else
            editText.setText(R.string.no_rating);
    }

}
