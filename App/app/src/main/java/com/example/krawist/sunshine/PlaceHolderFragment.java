package com.example.krawist.sunshine;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceHolderFragment extends Fragment {


    public PlaceHolderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_main,container,false);

        String[] days = {"Today-Sunny-88/63",
                "Tomorrow-Foggy-70/46",
                "Weds-Cloudy-72/63",
                "Thurs-Rainy-64/51",
                "Fri-Foggy-70/46",
                "Sat-Sunny-76/68",
                "Sun-Rainy-54/40"};

        List<String> listOfDays = new ArrayList<>(Arrays.asList(days));

        ArrayAdapter<String> mForecastadapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_textview,
                listOfDays);

        ListView listView = rootView.findViewById(R.id.fragement_main_listview);

        listView.setAdapter(mForecastadapter);

        return rootView;
    }

}
