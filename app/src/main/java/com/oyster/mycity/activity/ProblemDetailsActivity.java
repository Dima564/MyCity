package com.oyster.mycity.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.oyster.mycity.Problem;
import com.oyster.mycity.ProblemFactory;
import com.oyster.mycity.R;

import org.w3c.dom.Text;

/**
 * Created by dima on 25.07.14.
 */
public class ProblemDetailsActivity extends Activity {
    Problem mProblem;

    private ImageView imageView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView authorTextView;
    private Text


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_details);
        int position = getIntent().getIntExtra("problem_number",-1);
        if (position == -1)
            finish();
        else
            mProblem = ProblemFactory.getProblem(position);



    }
}
