package com.main.dhbworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.dhbworld.Navigation.NavigationUtilities;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.time.LocalDate;
import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.parser.DataImporter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CalendarActivity extends AppCompatActivity{
    WeekView cal;
    Calendar date = Calendar.getInstance();
    boolean useTempCal;
    List<Instant> loadedDateList = new ArrayList<>();
    static ArrayList<Events> events = new ArrayList<>();
    static ArrayList<String> blackList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);
        NavigationUtilities.setUpNavigation(this, R.id.Calendar);
        firstSetup();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_top_app_bar, menu);
        return true;
    }

//    public String getDashboardInfo(){
//
//    }


    public void firstSetup(){
        setCalSettings();
        blackList = getBlackList("blackList");
        ExecutorService executor = Executors.newCachedThreadPool();
        // immer nur auf montag scrollen, damit wochentage richtig angezeigt werden.
        setDates();
        EventCreator.instantiateVariables();
        executor.submit(runnableTask);
        setOnTouchListener(cal, executor);
    }


    public void setCalSettings(){
        cal = findViewById(R.id.weekView);
        cal.setNumberOfVisibleDays(5);
        cal.setShowFirstDayOfWeekFirst(true);
        cal.setNowLineStrokeWidth(6);
        cal.setHourHeight(9);
        cal.setShowNowLineDot(true);
        cal.setNowLineColor(Color.parseColor("#343491"));
        cal.setPastBackgroundColor(Color.LTGRAY);
        cal.setDateFormatter(calendar -> {
            SimpleDateFormat date = new SimpleDateFormat( "E dd.MM", Locale.getDefault());
            return date.format(calendar.getTime());
        });
        cal.setTimeFormatter(time -> time + " Uhr");
    }

    public static String[] arrayConvertor(List<String> titleList){
        return titleList.toArray(new String[0]);
    }


    public static ArrayList<String> getBlackList() {
        return blackList;
    }

    public void openFilterClick(@NonNull MenuItem item) throws NullPointerException{
        final String[] listItems = arrayConvertor(Objects.requireNonNull(EventCreator.uniqueTitles()));
        final boolean[] checkedItems = new boolean[listItems.length];
        final List<String> selectedItems = Arrays.asList(listItems);
        for(int i = 0; i < listItems.length; i++){
            if(blackList.contains(listItems[i])){ checkedItems[i] = false; }
            else{ checkedItems[i] = true; }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
                builder.setTitle("Filter your classes");
                builder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                        String currentItem = selectedItems.get(which);
                    }
                });
                // alert dialog shouldn't be cancellable
                builder.setCancelable(false);
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            if(!checkedItems[i]){
                                blackList.add(listItems[i]);
                            }
                            else if(blackList.contains(listItems[i])){
                                blackList.remove(listItems[i]);
                            }
                        }
                        blackList = removeDuplicates(blackList);
                        EventCreator.clearEvents();
                        EventCreator.setBlackList(blackList);
                        EventCreator.applyBlackList();
                        loadedDateList.clear();
                        saveBlackList(blackList,"blackList");
                        restart(CalendarActivity.this, false);
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //just close and do nothing, will not save the changed state.
                    }
                });
                builder.create();
                AlertDialog dialog = builder.create();
                dialog.show();
    }

    public ArrayList<String> removeDuplicates(ArrayList<String> list){
        return (ArrayList<String>) list.stream().distinct().collect(Collectors.toList());
    }

    public static void restart(Activity activity, boolean transition) {
        activity.recreate();
    }

    public void saveBlackList(ArrayList<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CalendarActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getBlackList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CalendarActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> blackList = gson.fromJson(json, type);
        if (blackList == null){
            ArrayList<String> emptyList = new ArrayList<>();
            return emptyList;
        }
        else{
            return blackList;
        }
    }

    //TODO Sundays do NOT work!!!!
    public void setDates(){
        date.set(Calendar.DAY_OF_WEEK,
                date.getActualMinimum(Calendar.DAY_OF_WEEK) + 1);
        date.set(Calendar.HOUR_OF_DAY,20);

        Calendar tempCal = Calendar.getInstance();
        if(tempCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            tempCal.add(Calendar.DAY_OF_WEEK,2);
            cal.scrollToDate(tempCal);
            useTempCal = true;
        }
        else if(tempCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            tempCal.add(Calendar.DAY_OF_WEEK,1);
            cal.scrollToDate(tempCal);
            useTempCal = true;
        }
        else{
            cal.scrollToDateTime(date);
            useTempCal = false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setOnTouchListener(WeekView cal, ExecutorService executor){
        final AtomicReference<Float>[] x1 = new AtomicReference[]{new AtomicReference<>((float) 0)};
        final float[] x2 = {0};

        cal.setOnTouchListener((v, event) -> {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1[0].set(event.getX());
                    break;
                case MotionEvent.ACTION_UP:
                    x2[0] = event.getX();
                    float deltaX = x2[0] - x1[0].get();
                    if (deltaX < -100) {
                        date.add(Calendar.WEEK_OF_YEAR,1);
                        cal.scrollToDate(date);
                        if(!loadedDateList.contains(date.toInstant())) {
                            executor.submit(runnableTask);
                        }
                        return true;
                    }else if(deltaX > 100){
                        date.add(Calendar.WEEK_OF_YEAR,-1);
                        cal.scrollToDate(date);
                        if(!loadedDateList.contains(date.toInstant())) {
                            executor.submit(runnableTask);
                        }
                        return true;
                    }
                    break;
            }
            return false;
        });
    }

    Runnable runnableTask = () -> {
        String url = "https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=eisenbiegler&file=TINF20B4" ; // ist von nutzer einzugeben (oder Liste von Unis auswählen).
        Map<LocalDate, ArrayList<Appointment>> data = new HashMap<>();
        LocalDate thisWeek = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        loadedDateList.add(date.toInstant());

        Calendar dateCopy = (Calendar) date.clone();
        dateCopy.add(Calendar.WEEK_OF_YEAR,1);
        LocalDate nextWeek = LocalDateTime.ofInstant(dateCopy.toInstant(), ZoneId.systemDefault()).toLocalDate();

            try {
                data = DataImporter.ImportWeekRange(thisWeek,nextWeek, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                assert data != null;
                saveValues(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

    public void saveValues(Map<LocalDate, ArrayList<Appointment>> data){
        Adapter adapter = new Adapter();
        cal.setAdapter(adapter);
        EventCreator.fillData(data, date);
        fillAdapter(adapter);
    }

    public static void setEvents(ArrayList<Events> events) {
        CalendarActivity.events = events;
    }

    public void fillAdapter(Adapter adapter){
        events = EventCreator.getEvents();
        adapter.submitList(events);
    }

    public void showBottomSheet(Events event){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        LocalTime startTimeAsLocalDate = LocalDateTime.ofInstant(event.startTime.toInstant(), ZoneId.systemDefault()).toLocalTime();
        LocalTime endTimeAsLocalDate = LocalDateTime.ofInstant(event.endTime.toInstant(), ZoneId.systemDefault()).toLocalTime();

        String timeString = String.format("%s - %s",startTimeAsLocalDate.toString(),endTimeAsLocalDate.toString());
        bottomSheetDialog.setContentView(R.layout.calendaritembottonsheet);
        bottomSheetDialog.show();

        TextView titleView = bottomSheetDialog.findViewById(R.id.calendarTitleText);
            titleView.setText(event.title);
            //titleView.setTextColor(event.style.hashCode());
        TextView timeView = bottomSheetDialog.findViewById(R.id.calendarTimeText);
            timeView.setText(timeString);
        TextView descriptionView = bottomSheetDialog.findViewById(R.id.calendarDescriptionText);
        descriptionView.setText(event.description);

        bottomSheetDialog.show();
    }


    class Adapter extends WeekView.SimpleAdapter<Events> implements com.main.dhbworld.Adapter {
        @NonNull
        @Override
        public WeekViewEntity onCreateEntity(Events item) {
            return new WeekViewEntity.Event.Builder<>(item)
                    .setId(item.id)
                    .setTitle(item.title)
                    .setStartTime(item.startTime)
                    .setEndTime(item.endTime)
                    .setStyle(item.style)
                    .build();
        }

        @Override
        public void onEventClick(Events data) {
            CalendarActivity.this.showBottomSheet(data);
            super.onEventClick(data);
        }
    }
}