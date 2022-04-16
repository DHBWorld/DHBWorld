package com.main.dhbworld;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.main.dhbworld.KVV.DataLoaderListener;
import com.main.dhbworld.KVV.Departure;
import com.main.dhbworld.KVV.KVVDataLoader;
import com.main.dhbworld.KVV.KVVListAdapter;
import com.main.dhbworld.Navigation.NavigationUtilities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class KVVActivity extends AppCompatActivity {

    KVVListAdapter kvvListAdapter;
    ArrayList<Departure> departures = new ArrayList<>();
    LocalDateTime localDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvvactivity);

        localDateTime = LocalDateTime.now();

        NavigationUtilities.setUpNavigation(this, R.id.tram_departure);

        RecyclerView recyclerView = findViewById(R.id.tram_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        kvvListAdapter = new KVVListAdapter(departures);

        recyclerView.setAdapter(kvvListAdapter);

        CircularProgressIndicator progressIndicator = findViewById(R.id.tram_process_indicator);

        KVVDataLoader dataLoader = new KVVDataLoader(this);
        dataLoader.setDataLoaderListener(new DataLoaderListener() {
            @Override
            public void onDataLoaded(ArrayList<Departure> returnedDepartures) {
                departures.clear();
                departures.addAll(returnedDepartures);
                kvvListAdapter.notifyDataSetChanged();
                progressIndicator.setVisibility(View.GONE);
                System.out.println(departures);
            }
        });

        dataLoader.loadData(localDateTime);

        TextView textViewDepartureTimeSelect = findViewById(R.id.tram_departure_time_select_textview);
        textViewDepartureTimeSelect.setText(getResources().getString(
                R.string.time,
                localDateTime.getHour(),
                localDateTime.getMinute()
        ));

        TextView textViewDepartureDateSelect = findViewById(R.id.tram_departure_date_select_textview);
        textViewDepartureDateSelect.setText(localDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));

        MaterialCardView tramDepartureTimeSelectCardView = findViewById(R.id.tram_departure_time_select);
        tramDepartureTimeSelectCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(localDateTime.getHour())
                        .setMinute(localDateTime.getMinute())
                        .setTitleText("Abfahrtszeit")
                        .build();

                materialTimePicker.show(getSupportFragmentManager(), "Abfahrtszeit");
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewDepartureTimeSelect
                                .setText(getResources().getString(
                                        R.string.time,
                                        materialTimePicker.getHour(),
                                        materialTimePicker.getMinute()
                                ));

                        localDateTime = localDateTime.withHour(materialTimePicker.getHour()).withMinute(materialTimePicker.getMinute());
                    }
                });
            }
        });


        MaterialCardView tramDepartureDateSelectCardView = findViewById(R.id.tram_departure_date_select);
        tramDepartureDateSelectCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Abfahrtsdatum")
                        .setSelection(localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli())
                        .build();

                materialDatePicker.show(getSupportFragmentManager(), "Abfahrtsdatum");
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Instant instant = Instant.ofEpochMilli((long) selection);
                        LocalDateTime selectedDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

                        textViewDepartureDateSelect.setText(selectedDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));

                        localDateTime = localDateTime.withYear(selectedDateTime.getYear())
                                .withDayOfYear(selectedDateTime.getDayOfYear());
                    }
                });
            }
        });

        MaterialButton refreshButton = findViewById(R.id.tram_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departures.clear();
                kvvListAdapter.notifyDataSetChanged();
                dataLoader.loadData(localDateTime);

                progressIndicator.setVisibility(View.VISIBLE);
            }
        });
    }
}