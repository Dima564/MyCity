package com.oyster.mycity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.oyster.mycity.Problem;
import com.oyster.mycity.ProblemType;
import com.oyster.mycity.R;

/**
 * Created by dima on 25.07.14.
 */
public class NewProblemDialogFragment extends DialogFragment {

    public static final int MIN_TITLE_LENGTH = 5;

    private static final String TAG = "NewProblemDialogFragment";

    EditText titleEditText;
    EditText descriptionEditText;
    ImageView imageView;

    NewProblemCallback mCallbacks;

    private Problem problem = new Problem();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    public void setCallbacks(NewProblemCallback callbacks) {
        mCallbacks = callbacks;
    }

    public void setLocation(LatLng location) {
        problem.setLocation(location);
    }

    public void setThumbnail(Bitmap image) {
        Log.d(TAG, "setThumbnail : " + this);
        problem.setImage(image);
        if (imageView != null)
            imageView.setImageBitmap(image);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_problem, null);
        titleEditText = (EditText) view.findViewById(R.id.title_edittext);
        descriptionEditText = (EditText) view.findViewById(R.id.description_edittext);
        imageView = (ImageView) view.findViewById(R.id.image);

        Bitmap bitmap = problem.getImage();
        if (bitmap == null)
            imageView.setImageBitmap(bitmap);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewProblemCallback) getActivity()).takePhoto();
            }
        });


        return new AlertDialog.Builder(getActivity())
                .setTitle("title")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (validateData()) {
                                    problem.setTitle(getTitle());
                                    problem.setDescription(getDescription());
                                    problem.setType(ProblemType.OTHER);
                                    problem.save();
                                }
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .setView(view)
                .create();
    }

    private String getTitle() {
        return titleEditText.getText().toString();
    }

    private String getDescription() {
        return descriptionEditText.getText().toString();
    }

    private boolean validateData() {
        if (titleEditText.getText().toString().length() >= MIN_TITLE_LENGTH)
            return true;
        Toast.makeText(getActivity(),R.string.short_title,Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onDestroyView()
    {
        Dialog dialog = getDialog();

        // Work around bug: http://code.google.com/p/android/issues/detail?id=17423
        if ((dialog != null) && getRetainInstance())
            dialog.setDismissMessage(null);

        super.onDestroyView();
    }


    public interface NewProblemCallback {
        public void addProblem(String title, String description, Bitmap thumbnail);
        public void takePhoto();
    }
}
