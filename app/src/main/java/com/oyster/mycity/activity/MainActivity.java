package com.oyster.mycity.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import com.google.android.gms.maps.model.LatLng;
import com.oyster.mycity.Facebook;
import com.oyster.mycity.LeftMenu;
import com.oyster.mycity.R;
import com.oyster.mycity.dialog.NewProblemDialogFragment;
import com.oyster.mycity.fragment.BookmarksListFragment;
import com.oyster.mycity.fragment.MyPlacesFragment;
import com.oyster.mycity.fragment.ProblemsListFragment;
import com.oyster.mycity.fragment.ProblemsMapFragment;
import com.parse.ParseUser;
import com.parse.PushService;

import static com.oyster.mycity.LeftMenu.ITEM_BOOKMARKS;
import static com.oyster.mycity.LeftMenu.ITEM_EXIT;
import static com.oyster.mycity.LeftMenu.ITEM_MAP;
import static com.oyster.mycity.LeftMenu.ITEM_MESSAGES;
import static com.oyster.mycity.LeftMenu.ITEM_MY_PLACES;
import static com.oyster.mycity.LeftMenu.ITEM_PROBLEMS;


public class MainActivity extends Activity implements LeftMenu.MenuCallbacks, NewProblemDialogFragment.NewProblemCallback {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "MainActivity";
    private static final String DIALOG_TAG = "dialog";
    String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userName = ParseUser.getCurrentUser().getString("name");
        String channel = userName.replace(" ","_");
        PushService.subscribe(this, channel, MainActivity.class);

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
                fragment = new BookmarksListFragment();
                break;
            case ITEM_MY_PLACES:
                fragment = new MyPlacesFragment();
                break;
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
        NewProblemDialogFragment newProblemDialogFragment = new NewProblemDialogFragment();
        newProblemDialogFragment.setCallbacks(this);
        newProblemDialogFragment.setLocation(location);
        newProblemDialogFragment.show(getFragmentManager(), DIALOG_TAG);
    }


    @Override
    public void addProblem(String title, String description, Bitmap thumbnail) {
        // TODO deal with it
    }
}
