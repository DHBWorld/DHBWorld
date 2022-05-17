package com.main.dhbworld;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.main.dhbworld.KVV.DataLoaderListener;
import com.main.dhbworld.KVV.Departure;
import com.main.dhbworld.KVV.Disruption;
import com.main.dhbworld.KVV.KVVDataLoader;
import com.main.dhbworld.KVV.KVVListAdapter;
import com.main.dhbworld.Navigation.NavigationUtilities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class KVVActivity extends AppCompatActivity implements DataLoaderListener {

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

    MaterialCardView tramDisruptionCardView;
    TextView tramDisruptionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvvactivity);

        localDateTime = LocalDateTime.now();

        NavigationUtilities.setUpNavigation(this, R.id.tram_departure);

        initViews();
        setupAdapter();

        KVVDataLoader dataLoader = new KVVDataLoader(this);
        dataLoader.setDataLoaderListener(this);
        dataLoader.loadData(localDateTime);

        setupTextViews();
        setupClickListeners(dataLoader);
    }

    private void setupTextViews() {
        textViewDepartureTimeSelect.setText(getResources().getString(
                R.string.time,
                localDateTime.getHour(),
                localDateTime.getMinute()
        ));

        textViewDepartureDateSelect.setText(
                localDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        );
    }

    private void setupClickListeners(KVVDataLoader dataLoader) {
        tramDepartureTimeSelectCardView.setOnClickListener(view -> timeSelectClick());

        tramDepartureDateSelectCardView.setOnClickListener(view -> dateSelectClick());

        refreshButton.setOnClickListener(view -> refresh(dataLoader));
    }

    private void refresh(KVVDataLoader dataLoader) {
        int oldSize = departures.size();
        departures.clear();
        kvvListAdapter.notifyItemRangeRemoved(0, oldSize);
        dataLoader.loadData(localDateTime);

        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void dateSelectClick() {
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
    }

    private void timeSelectClick() {
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
    }

    private void setupAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        kvvListAdapter = new KVVListAdapter(this, departures);
        recyclerView.setAdapter(kvvListAdapter);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.tram_recyclerview);
        progressIndicator = findViewById(R.id.tram_process_indicator);
        textViewDepartureTimeSelect = findViewById(R.id.tram_departure_time_select_textview);
        textViewDepartureDateSelect = findViewById(R.id.tram_departure_date_select_textview);
        tramDepartureTimeSelectCardView = findViewById(R.id.tram_departure_time_select);
        tramDepartureDateSelectCardView = findViewById(R.id.tram_departure_date_select);
        refreshButton = findViewById(R.id.tram_refresh);

        tramDisruptionCardView = findViewById(R.id.tram_disruption_title_view);
        tramDisruptionTitle = findViewById(R.id.tram_disruption_title_text);
    }

    @Override
    public void onDataLoaded(ArrayList<Departure> returnedDepartures, Disruption disruption) {
        departures.clear();
        departures.addAll(returnedDepartures);
        kvvListAdapter.notifyItemRangeInserted(0, departures.size());
        progressIndicator.setVisibility(View.GONE);
        if (departures.size() == 0) {
            Snackbar.make(KVVActivity.this.findViewById(android.R.id.content),
                    R.string.error_getting_kvv_data,
                    BaseTransientBottomBar.LENGTH_LONG)
                    .show();
        }

        if (disruption != null) {
            tramDisruptionTitle.setText(disruption.getSummary());
            tramDisruptionCardView.setVisibility(View.VISIBLE);
            tramDisruptionCardView.setOnClickListener(view -> {
                String builder = "<h1>" +
                        disruption.getSummary() +
                        "</h1><h3>" +
                        disruption.getLines() +
                        "</h3>" +
                        disruption.getDetails();
                new MaterialAlertDialogBuilder(KVVActivity.this)
                        .setMessage(Html.fromHtml(builder, Html.FROM_HTML_MODE_LEGACY))
                        .setPositiveButton(R.string.close, null)
                        .show();
            });
        } else {
            tramDisruptionCardView.setVisibility(View.GONE);
        }
    }
}