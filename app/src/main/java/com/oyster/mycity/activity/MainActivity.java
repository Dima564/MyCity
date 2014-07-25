package com.oyster.mycity.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.oyster.mycity.LeftMenu;
import com.oyster.mycity.R;
import com.oyster.mycity.fragment.ProblemsMapFragment;


public class MainActivity extends Activity implements LeftMenu.MenuCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().add(R.id.content_frame, new ProblemsMapFragment())
                .commit();

        new LeftMenu(this, this).build();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void itemSelected(int item) {

    }
}
