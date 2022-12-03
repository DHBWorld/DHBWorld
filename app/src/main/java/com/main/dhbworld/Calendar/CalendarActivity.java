package com.main.dhbworld.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.dhbworld.Navigation.NavigationUtilities;
import com.main.dhbworld.Organizer.OrganizerActivity;
import com.main.dhbworld.R;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.parser.DataImporter;

public class CalendarActivity extends AppCompatActivity{
    WeekView cal;
    Calendar date = Calendar.getInstance();
    List<Instant> loadedDateList = new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;
    AlertDialog alertDialog;
    static ArrayList<Events> events = new ArrayList<>();
    static ArrayList<String> blackList = new ArrayList<>();
    String url;
    boolean stillLoading = false;
    boolean showSat = false;
    LinearProgressIndicator progressBar;
    FirebaseFirestore firestore;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check url here so the schedule layout isn't displayed yet.
        checkURL();
        setContentView(R.layout.schedule_layout);
        progressBar = findViewById(R.id.calProgressBar);
        NavigationUtilities.setUpNavigation(this, R.id.Calendar);
        firstSetup();
        firestore= FirebaseFirestore.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_top_app_bar, menu);
        return true;
    }

    public void firstSetup(){
        setCalSettings();
        blackList = getBlackList("blackList");
        ExecutorService executor = Executors.newCachedThreadPool();
        // immer nur auf montag scrollen, damit wochentage richtig angezeigt werden.
        setDates();
        //reset Variables of EventCreator class. Relevant after applying filters.
        EventCreator.instantiateVariables();
        executor.submit(importWeek);
        setOnTouchListener(cal, executor);
    }

    public void setCalSettings(){
        cal = findViewById(R.id.weekView);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showSat = defaultSharedPreferences.getBoolean("calendarShowSat", false);

        if (showSat) {
            //Show Mon-Sat
            cal.setNumberOfVisibleDays(6);
        } else {
            //Only show Mon-Fri
            cal.setNumberOfVisibleDays(5);
        }

        cal.setShowFirstDayOfWeekFirst(true);
        //Scrolling only with onTouchListener allowed!
        cal.setHorizontalScrollingEnabled(false);
        cal.setNowLineStrokeWidth(6);
        //Prevent Calendar from scrolling to the bottom on Activity start
        cal.setHourHeight(7);
        //configure now Line
        cal.setShowNowLineDot(true);
        //cal.setNowLineColor(Color.parseColor("#343491"));
        //cal.setPastBackgroundColor(Color.LTGRAY);
        cal.setDateFormatter(calendar -> {
            SimpleDateFormat date = new SimpleDateFormat( "E dd.MM", Locale.getDefault());
            return date.format(calendar.getTime());
        });
        cal.setTimeFormatter(time -> time + " Uhr");
    }

    public String checkURL(){
       url = getURL();
        if(url == null){
            createUrlDialog();
        }
        url = getURL();
        return url;
    }

    public String getURL(){
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(CalendarActivity.this);
        String url = preferences.getString("CurrentURL",null);
        return  url;
    }

    //set dates to always display a monday. If it's a weekend, go to next monday.
    public void setDates(){
        date.set(Calendar.DAY_OF_WEEK,
                date.getActualMinimum(Calendar.DAY_OF_WEEK) + 1);

        Calendar tempCal = Calendar.getInstance();
        if(tempCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && !showSat){
            tempCal.add(Calendar.DAY_OF_WEEK,2);
            date = tempCal;
        }
        else if(tempCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            tempCal.add(Calendar.DAY_OF_WEEK,1);
            date = tempCal;
        }
        cal.scrollToDate(date);
    }

    public static String[] arrayConvertor(List<String> titleList){
        return titleList.toArray(new String[0]);
    }

    public static ArrayList<String> getBlackList() {
        return blackList;
    }



    //Set and apply new blackList with selected elements. Then restart Activity to display only whitelisted events.
    public void positiveButtonAction(){
        blackList = removeDuplicates(blackList);
        EventCreator.clearEvents();
        EventCreator.setBlackList(blackList);
        EventCreator.applyBlackList();
        loadedDateList.clear();
        saveBlackList(blackList,"blackList");
        restart(CalendarActivity.this);
    }

    //Create AlertDialog when Activity is first started. Prompt user to enter URL for calendar (can be changed in settings).
    public void createUrlDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CalendarActivity.this);

        builder.setView(R.layout.urlalertdialog);
        builder.setTitle("Please enter your Rapla-URL");
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(CalendarActivity.this);
        builder.setCancelable(false);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog tempView = (AlertDialog) dialog;
                        EditText urlEditText = tempView.findViewById(R.id.urlEditText);
                        EditText courseEditText = tempView.findViewById(R.id.urlCourseName);
                        EditText courseDirEditText = tempView.findViewById(R.id.urlCourseDirector);
                        String courseDirector = courseDirEditText.getText().toString().toLowerCase().replace(" ", "");
                        String courseName = courseEditText.getText().toString().toUpperCase().replace(" ","");
                        String urlString = urlEditText.getText().toString();
                        Map<String, Object> courseInFirestore = new HashMap<>();

                        if(!courseName.isEmpty() && !urlString.isEmpty()){
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("CurrentURL", urlString);
                            editor.apply();
                            courseInFirestore.put("URL", urlString);
                        }
                        else if(urlString.isEmpty() && !courseName.isEmpty() && !courseDirector.isEmpty()){
                            urlString = ("https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=" + courseDirector + "&file=" + courseName);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("CurrentURL", urlString);
                            editor.apply();
                            courseInFirestore.put("CourseDirector", courseDirector.toUpperCase().charAt(0)+courseDirector.substring(1).toLowerCase());
                            courseInFirestore.put("URL", urlString);
                        }
                        else if(!urlString.isEmpty()) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("CurrentURL", urlString);
                            editor.apply();
                            String partURLfile=urlString.substring(urlString.indexOf("file=")+5);
                            if (partURLfile.contains("&")){
                                courseName=partURLfile.substring(0, partURLfile.indexOf("&"));
                            }else {
                                courseName=partURLfile;
                            }
                            courseInFirestore.put("URL", urlString);
                        }
                        try {
                            URL urlCheck = new URL(urlString);
                            HttpsURLConnection connection = (HttpsURLConnection) urlCheck.openConnection();
                            if (connection.getResponseCode() == 200) {
                                firestore.collection("Courses").document(courseName.toLowerCase()).set(courseInFirestore, SetOptions.merge());
                            }
                        } catch (IOException | IllegalArgumentException ignored) {}
                    }
                }).start();
                restart(CalendarActivity.this);
            }

        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNeutralButton("No idea",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CalendarActivity.this, OrganizerActivity.class);
                CalendarActivity.this.finish();
                startActivity(intent);
            }
        });

        builder.create();
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public ArrayList<String> removeDuplicates(ArrayList<String> list){
        return (ArrayList<String>) list.stream().distinct().collect(Collectors.toList());
    }

    public static void restart(Activity activity) {
        activity.recreate();
    }

    public void saveBlackList(ArrayList<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CalendarActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        System.out.println(json);
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
            return new ArrayList<>();
        }
        else{
            return blackList;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setOnTouchListener(WeekView cal, ExecutorService executor){
        //calculate the delta between initial touch and release of finger. If over 100 pixels, switch pages.
        final AtomicReference<Float>[] x1 = new AtomicReference[]{new AtomicReference<>((float) 0)};
        final float[] x2 = {0};

        cal.setOnTouchListener((v, event) -> {
            // TODO Replace with native Android OnTouchListener
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1[0].set(event.getX());
                    break;
                case MotionEvent.ACTION_UP:
                    x2[0] = event.getX();
                    float deltaX = x2[0] - x1[0].get();
                    if (deltaX < -100 && stillLoading) {
                        date.add(Calendar.WEEK_OF_YEAR,1);
                        cal.scrollToDate(date);
                        if(!loadedDateList.contains(date.toInstant())) {
                            executor.submit(importWeek);
                        }
                        return true;
                    }else if(deltaX > 100 && stillLoading){
                        date.add(Calendar.WEEK_OF_YEAR,-1);
                        cal.scrollToDate(date);
                        if(!loadedDateList.contains(date.toInstant())) {
                            executor.submit(importWeek);
                        }
                        return true;
                    }
                    break;
            }
            return false;
        });
    }

    Runnable importWeek = () -> {
        stillLoading = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
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
                stillLoading = true;
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public static void setEvents(ArrayList<Events> events) {
        CalendarActivity.events = events;
    }

    public void fillAdapter(Adapter adapter){
        events = EventCreator.getEvents();
        adapter.submitList(events);
    }

    public void showBottomSheet(Events event){
        bottomSheetDialog = new BottomSheetDialog(this);
        LocalTime startTimeAsLocalDate = LocalDateTime.ofInstant(event.startTime.toInstant(), ZoneId.systemDefault()).toLocalTime();
        LocalTime endTimeAsLocalDate = LocalDateTime.ofInstant(event.endTime.toInstant(), ZoneId.systemDefault()).toLocalTime();

        String timeString = String.format("%s - %s",startTimeAsLocalDate.toString(),endTimeAsLocalDate.toString());
        bottomSheetDialog.setContentView(R.layout.calendaritembottonsheet);
        bottomSheetDialog.show();

        TextView titleView = bottomSheetDialog.findViewById(R.id.calendarTitleText);
        Objects.requireNonNull(titleView).setText(event.title);
            //titleView.setTextColor(event.style.hashCode());
        TextView timeView = bottomSheetDialog.findViewById(R.id.calendarTimeText);
            Objects.requireNonNull(timeView).setText(timeString);
        TextView descriptionView = bottomSheetDialog.findViewById(R.id.calendarDescriptionText);
        Objects.requireNonNull(descriptionView).setText(event.description);

        bottomSheetDialog.show();
    }

    public void openFilterClick(@NonNull MenuItem item) throws NullPointerException{
        final String[] listItems = arrayConvertor(Objects.requireNonNull(EventCreator.uniqueTitles()));
        final boolean[] checkedItems = new boolean[listItems.length];
        for(int i = 0; i < listItems.length; i++){
            checkedItems[i] = !blackList.contains(listItems[i]);
        }
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CalendarActivity.this);
        builder.setTitle("Filter your classes");
        builder.setMultiChoiceItems(listItems, checkedItems, (dialog, which, isChecked)
                -> checkedItems[which] = isChecked);
        // alert dialog shouldn't be cancellable
        builder.setCancelable(false);
        builder.setPositiveButton("Done", (dialog, which) -> {
            for (int i = 0; i < checkedItems.length; i++) {
                if(!checkedItems[i]){ blackList.add(listItems[i]); }
                else blackList.remove(listItems[i]);
            }
            positiveButtonAction();
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            //just close and do nothing, will not save the changed state.
        });
        builder.create();
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void jumpToToday (@NonNull MenuItem item) throws NullPointerException{
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.DAY_OF_WEEK,
                currentDate.getActualMinimum(Calendar.DAY_OF_WEEK) + 1);

        Calendar tempCal = Calendar.getInstance();
        if(tempCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && !showSat){
            tempCal.add(Calendar.DAY_OF_WEEK,2);
            currentDate = tempCal;
        }
        else if(tempCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            tempCal.add(Calendar.DAY_OF_WEEK,1);
            currentDate = tempCal;
        }
        date = currentDate;
        cal.scrollToDate(currentDate);
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