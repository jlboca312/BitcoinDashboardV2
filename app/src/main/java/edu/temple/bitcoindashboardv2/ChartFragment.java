package edu.temple.bitcoindashboardv2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class ChartFragment extends Fragment {

    //GLOBAL VARIABLES
    ImageView chart;
    Button loadButton;
    Spinner spinner;
    String[] timePeriods;
    String url;
    String timePeriodStr;
    int isTwoPanes;
    View v;
    Boolean loadButtonClicked = false;

    public ChartFragment(){
        //required empty public constructor
    }

    @Override public void onAttach(Activity c){
        super.onAttach(c);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //determine if two panes, if returned 1 then PORTRAIT, if 2, then LANDSCAPE
        isTwoPanes = getResources().getConfiguration().orientation;

        if(isTwoPanes == 1) {
            v = inflater.inflate(R.layout.chart_frag, container, false);
        }else{
            v = inflater.inflate(R.layout.chart_frag_landscape, container, false);
        }
        //connect views to variables
        chart = (ImageView) v.findViewById(R.id.chartView);
        loadButton = (Button) v.findViewById(R.id.loadButton);
        spinner = (Spinner) v.findViewById(R.id.spinner);

        //load the time period string array from resources
        timePeriods = getResources().getStringArray(R.array.timePeriods);

        //default load the 1d chart
        Picasso.with(getActivity()).load("https://chart.yahoo.com/z?s=BTCUSD=X&t=1d").into(chart);

        //create an array adapter for the time periods array
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, timePeriods);

        //set the adapter
        spinner.setAdapter(adapter);

        //deal with the clicking of a spinner item
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //store what the spinner selected into a string so you can concatenate it to the url upon load button press
                timePeriodStr = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //NOTHING?
            }
        });

        //deal with clicking the load button
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //use picasso to deal with http request to get chart image
                //concatentate the time period to the url
                Picasso.with(getActivity()).load("https://chart.yahoo.com/z?s=BTCUSD=X&t=" + timePeriodStr).into(chart);

                //set flag to indicate button was clicked
                loadButtonClicked = true;
            }
        });

        //if they clicked the button perform the update (put a timer around pressing the button)
        if(loadButtonClicked) {
            Timer timerObj = new Timer();
            TimerTask timerTaskObj = new TimerTask() {
                public void run() {
                    loadButton.performClick();
                }
            };
            timerObj.schedule(timerTaskObj, 0, 15000);
        }


        return v;
    }

}
