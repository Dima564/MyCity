package com.oyster.mycity.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.oyster.mycity.Problem;
import com.oyster.mycity.ProblemFactory;
import com.oyster.mycity.R;

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
}
