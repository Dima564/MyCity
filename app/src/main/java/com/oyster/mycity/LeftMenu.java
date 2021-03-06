package com.oyster.mycity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
* Created by dima on 29.06.14.
*/
public class LeftMenu {
    public static final int ITEM_MAP = 0;
    public static final int ITEM_PROBLEMS = 1;
    public static final int ITEM_BOOKMARKS = 2;
    public static final int ITEM_MESSAGES = 3;
    public static final int ITEM_MY_PLACES = 4;
    public static final int ITEM_EXIT = 5;
    private static final String TAG = "LeftMenu";

    SlidingMenu menu;
    private MenuCallbacks mCallbacks;
    private Activity mActivity;

    public LeftMenu(Activity activity, MenuCallbacks callbacks) {
        mCallbacks = callbacks;
        mActivity = activity;
    }

    public void build() {
        View view = mActivity.getLayoutInflater().inflate(R.layout.menu_left,null);
        ListView list = (ListView) view.findViewById(R.id.list);
        list.setAdapter(new MenuListAdapter());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.itemSelected(position);
            }
        });

        ImageView imageView = (ImageView) view.findViewById(R.id.user_icon);
        TextView userFirstName = (TextView) view.findViewById(R.id.user_first_name);
        TextView userLastName = (TextView) view.findViewById(R.id.user_last_name);

        Facebook.setProfilePicture(mActivity,imageView,userFirstName,userLastName);

        menu = new SlidingMenu(mActivity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindWidthRes(R.dimen.menu_width);
        menu.setFadeDegree(0f);
        menu.setMenu(view);
        menu.attachToActivity(mActivity, SlidingMenu.SLIDING_CONTENT);
    }

    public void setActive(boolean isActive) {
        if (isActive)
            menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        else
            menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }


    public interface MenuCallbacks {
        public void itemSelected(int item);
    }


    class MenuListAdapter extends ArrayAdapter {

        public MenuListAdapter() {
            super(mActivity,R.layout.menu_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater layoutInflater;
                layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(R.layout.menu_item, null);
            }

            ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);
            TextView title = (TextView) convertView.findViewById(R.id.item_title);

            switch (position) {
                case ITEM_MAP:
                    title.setText(R.string.map);
                    icon.setImageResource(android.R.drawable.ic_dialog_map);
                    break;
                case ITEM_PROBLEMS:
                    title.setText(R.string.problems);
                    icon.setImageResource(android.R.drawable.ic_lock_idle_low_battery);
                    break;
                case ITEM_BOOKMARKS:
                    title.setText(R.string.bookmarks);
                    icon.setImageResource(android.R.drawable.star_big_off);
                    break;
                case ITEM_MESSAGES:
                    title.setText(R.string.government_messages);
                    icon.setImageResource(android.R.drawable.ic_dialog_map);
                    break;
                case ITEM_MY_PLACES:
                    title.setText(R.string.my_places);
                    icon.setImageResource(android.R.drawable.ic_menu_myplaces);
                    break;
                case ITEM_EXIT:
                    title.setText(R.string.exit);
                    icon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                    break;
                default:
                    title.setText("Default menu item");
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return 6;
        }
    }


}
