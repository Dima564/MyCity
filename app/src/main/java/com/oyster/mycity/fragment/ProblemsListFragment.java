package com.oyster.mycity.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.oyster.mycity.R;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by dima on 25.07.14.
 */
public class ProblemsListFragment extends ListFragment {
    List<ParseObject> mProblems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void queryProblems() {

    }




    private class CardAdapter extends ArrayAdapter<ParseObject> {
        private Context mContext;

        public CardAdapter() {
            super(getActivity(), R.layout.list_item_problem, mProblems);
            mContext = getActivity();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater)
                        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item_problem, null);
            }
            ParseObject card = mProblems.get(position);


            if (card == null) return v;
//            TextView mTitleTextView = (TextView) v.findViewById(R.id.);

//            mTitleTextView.setText(card.get("title").toString());

            return v;
        }

    }

}
