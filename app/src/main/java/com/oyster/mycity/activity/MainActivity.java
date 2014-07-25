package com.oyster.mycity.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.oyster.mycity.Facebook;
import com.oyster.mycity.LeftMenu;
import com.oyster.mycity.R;
import com.oyster.mycity.dialog.NewProblemDialogFragment;
import com.oyster.mycity.fragment.ProblemsListFragment;
import com.oyster.mycity.fragment.ProblemsMapFragment;

import static com.oyster.mycity.LeftMenu.ITEM_BOOKMARKS;
import static com.oyster.mycity.LeftMenu.ITEM_EXIT;
import static com.oyster.mycity.LeftMenu.ITEM_MAP;
import static com.oyster.mycity.LeftMenu.ITEM_MESSAGES;
import static com.oyster.mycity.LeftMenu.ITEM_PROBLEMS;


public class MainActivity extends Activity implements LeftMenu.MenuCallbacks, NewProblemDialogFragment.NewProblemCallback {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "MainActivity";
    private static final String DIALOG_TAG = "dialog";
    String mCurrentPhotoPath;
    NewProblemDialogFragment mNewProblemDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
        getFragmentManager().beginTransaction().add(R.id.content_frame, new ProblemsMapFragment())
                .commit();

        new LeftMenu(this, this).build();

    }


    public void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (mNewProblemDialog != null)
                ((NewProblemDialogFragment)getFragmentManager().findFragmentByTag(DIALOG_TAG))
                        .setThumbnail(imageBitmap);
        }
    }

    @Override
    public void itemSelected(int item) {

        Fragment fragment = null;
        switch (item) {
            case ITEM_MAP:
                fragment = new ProblemsMapFragment();
                break;
            case ITEM_PROBLEMS:
                fragment = new ProblemsListFragment();
                break;
            case ITEM_BOOKMARKS:
            case ITEM_MESSAGES:
            case ITEM_EXIT:
            default:
                Facebook.logout();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
        }

        if (fragment != null)
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }



    public void addProblem(LatLng location) {
        mNewProblemDialog = new NewProblemDialogFragment();
        mNewProblemDialog.setCallbacks(this);
        new NewProblemDialogFragment().show(getFragmentManager(), "dialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void addProblem(String title, String description, Bitmap thumbnail) {
        // TODO deal with it
    }
}
