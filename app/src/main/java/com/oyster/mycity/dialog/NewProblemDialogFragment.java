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

import com.oyster.mycity.R;

import java.net.URI;

/**
 * Created by dima on 25.07.14.
 */
public class NewProblemDialogFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private static final String TAG = "NewProblemDialogFragment";
    EditText titleEditText;
    EditText descriptionEditText;
    ImageView thumbnailImageView;
    NewProblemCallback mCallbacks;
    Bitmap mThumbnail = null;
    URI mFullSizePhoto;

    public void setCallbacks(NewProblemCallback callbacks) {
        mCallbacks = callbacks;
    }

    public void setFullSizePhoto(URI mFullSizePhoto) {
        this.mFullSizePhoto = mFullSizePhoto;
    }

    public void setThumbnail(Bitmap thumbnail) {
        Log.d(TAG, "setThumbnail : " + this);
        mThumbnail = thumbnail;
        if (thumbnailImageView != null)
            thumbnailImageView.setImageBitmap(thumbnail);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_problem, null);
        titleEditText = (EditText) view.findViewById(R.id.title_edittext);
        descriptionEditText = (EditText) view.findViewById(R.id.description_edittext);
        thumbnailImageView = (ImageView) view.findViewById(R.id.thumbnail);
        if (mThumbnail != null)
            thumbnailImageView.setImageBitmap(mThumbnail);

        thumbnailImageView.setOnClickListener(new View.OnClickListener() {
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
                                // do something...
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
