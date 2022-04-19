package com.main.dhbworld;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
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

    RecyclerView recyclerView;
    CircularProgressIndicator progressIndicator;
    TextView textViewDepartureTimeSelect;
    TextView textViewDepartureDateSelect;
    MaterialCardView tramDepartureTimeSelectCardView;
    MaterialCardView tramDepartureDateSelectCardView;
    MaterialButton refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvvactivity);

        localDateTime = LocalDateTime.now();

        NavigationUtilities.setUpNavigation(this, R.id.tram_departure);

        recyclerView = findViewById(R.id.tram_recyclerview);
        progressIndicator = findViewById(R.id.tram_process_indicator);
        textViewDepartureTimeSelect = findViewById(R.id.tram_departure_time_select_textview);
        textViewDepartureDateSelect = findViewById(R.id.tram_departure_date_select_textview);
        tramDepartureTimeSelectCardView = findViewById(R.id.tram_departure_time_select);
        tramDepartureDateSelectCardView = findViewById(R.id.tram_departure_date_select);
        refreshButton = findViewById(R.id.tram_refresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        kvvListAdapter = new KVVListAdapter(departures);
        recyclerView.setAdapter(kvvListAdapter);

        KVVDataLoader dataLoader = new KVVDataLoader(this);
        dataLoader.setDataLoaderListener(returnedDepartures -> {
            departures.clear();
            departures.addAll(returnedDepartures);
            kvvListAdapter.notifyItemRangeInserted(0, departures.size());
            progressIndicator.setVisibility(View.GONE);
            if (departures.size() == 0) {
                Toast.makeText(KVVActivity.this,
                        R.string.error_getting_kvv_data,
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        dataLoader.loadData(localDateTime);

        textViewDepartureTimeSelect.setText(getResources().getString(
                R.string.time,
                localDateTime.getHour(),
                localDateTime.getMinute()
        ));

        textViewDepartureDateSelect.setText(
                localDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        );

        tramDepartureTimeSelectCardView.setOnClickListener(view -> {
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(localDateTime.getHour())
                    .setMinute(localDateTime.getMinute())
                    .setTitleText(R.string.departure_time)
                    .build();

            materialTimePicker.show(getSupportFragmentManager(), "DEPARTURE_TIME");
            materialTimePicker.addOnPositiveButtonClickListener(view1 -> {
                textViewDepartureTimeSelect
                        .setText(getResources().getString(
                                R.string.time,
                                materialTimePicker.getHour(),
                                materialTimePicker.getMinute()
                        ));

                localDateTime = localDateTime.withHour(
                        materialTimePicker.getHour()).withMinute(materialTimePicker.getMinute()
                );
            });
        });



        tramDepartureDateSelectCardView.setOnClickListener(view -> {
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.departure_date)
                    .setSelection(localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli())
                    .build();

            materialDatePicker.show(getSupportFragmentManager(), "DEPARTURE_DATE");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                Instant instant = Instant.ofEpochMilli(selection);
                LocalDateTime selectedDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

                textViewDepartureDateSelect.setText(
                        selectedDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                );

                localDateTime = localDateTime.withYear(selectedDateTime.getYear())
                        .withDayOfYear(selectedDateTime.getDayOfYear());
            });
        });

        refreshButton.setOnClickListener(view -> {
            int oldSize = departures.size();
            departures.clear();
            kvvListAdapter.notifyItemRangeRemoved(0, oldSize);
            dataLoader.loadData(localDateTime);

            progressIndicator.setVisibility(View.VISIBLE);
        });
    }
}