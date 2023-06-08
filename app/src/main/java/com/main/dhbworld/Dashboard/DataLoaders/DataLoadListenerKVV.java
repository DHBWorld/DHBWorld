package com.main.dhbworld.Dashboard.DataLoaders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.main.dhbworld.KVV.DataLoaderListener;
import com.main.dhbworld.KVV.Departure;
import com.main.dhbworld.KVV.Disruption;
import com.main.dhbworld.Utilities.ProgressIndicator;
import com.main.dhbworld.R;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class DataLoadListenerKVV implements DataLoaderListener {
    private final LinearLayout[] layoutTram ;
    private final ProgressIndicator indicator;
    private final TextView[] tramView ;
    private final TextView[] platformView ;
    private final TextView[] timeView ;
    private final ImageView tramImageOne;
    private final Context context;



    public DataLoadListenerKVV(LinearLayout[] layoutTram, ProgressIndicator indicator, TextView[] tramView, TextView[] platformView, TextView[] timeView, ImageView tramImageOne, Context context) {
        this.layoutTram = layoutTram;
       this. indicator = indicator;
        this.tramView =tramView;
        this.platformView = platformView;
        this.timeView = timeView;
        this.tramImageOne=tramImageOne;
        this.context=context;


    }


    @Override
    public void onDataLoaded(ArrayList<Departure> departures, Disruption disruption) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        indicator.hide();
        if ( (departures==null) || (departures.size()<1)){
            tramImageOne.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_pause));
            platformView[0].setVisibility(View.GONE);
            timeView[0].setVisibility(View.GONE);
            tramView[0].setText(context.getResources().getString(R.string.serverTrouble));
            layoutTram[2].setVisibility(View.GONE);
            layoutTram[1].setVisibility(View.GONE);
        }else{
            tramImageOne.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_tram));
            platformView[0].setVisibility(View.VISIBLE);
            timeView[0].setVisibility(View.VISIBLE);
            for (int i=0;i<3;i++){
                if (departures.size()>i){
                    layoutTram[i].setVisibility(View.VISIBLE);
                    tramView[i].setText(departures.get(i).getLine().substring(departures.get(i).getLine().length()-1)+" ("+departures.get(i).getDestination()+")");
                    if (departures.get(i).isNotServiced()) {
                        platformView[i].setText(R.string.canceled_in_parens);
                    } else {
                        platformView[i].setText(departures.get(i).getPlatform());
                    }
                    timeView[i].setText(departures.get(i).getDepartureTime().format(formatter));
                }else{
                    layoutTram[i].setVisibility(View.GONE);
                }
            }
        }
    }
}
