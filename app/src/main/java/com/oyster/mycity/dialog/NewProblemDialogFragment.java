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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.oyster.mycity.Problem;
import com.oyster.mycity.ProblemType;
import com.oyster.mycity.R;
import com.parse.ParseUser;

/**
 * Created by dima on 25.07.14.
 */
public class NewProblemDialogFragment extends DialogFragment {

    public static final int MIN_TITLE_LENGTH = 5;

    private static final String TAG = "NewProblemDialogFragment";

    EditText titleEditText;
    EditText descriptionEditText;
    ImageView imageView;
    Spinner typeSpinner;

    NewProblemCallback mCallbacks;

    private Problem mProblem = new Problem();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    public void setCallbacks(NewProblemCallback callbacks) {
        mCallbacks = callbacks;
    }

    public void setLocation(LatLng location) {
        mProblem.setLocation(location);
    }

    public void setThumbnail(Bitmap image) {
        Log.d(TAG, "setThumbnail : " + this);
        mProblem.setImage(image);
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
        typeSpinner = (Spinner) view.findViewById(R.id.type_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.type_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        typeSpinner.setAdapter(adapter);
        typeSpinner.setPrompt(getResources().getString(R.string.choose_solution));

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),
                        (ProblemType.values()[position]).toString(),Toast.LENGTH_SHORT).show();
                mProblem.setType(ProblemType.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mProblem.setType(ProblemType.OTHER);
            }
        });

        Bitmap bitmap = mProblem.getImage();
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
                                    mProblem.setTitle(getTitle());
                                    mProblem.setDescription(getDescription());
                                    mProblem.setAuthor(ParseUser.getCurrentUser().getString("user"));
                                    mProblem.save();
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
