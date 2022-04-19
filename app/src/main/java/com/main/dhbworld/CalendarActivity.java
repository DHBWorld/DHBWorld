package com.main.dhbworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.main.dhbworld.Navigation.NavigationUtilities;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;

import java.time.ZoneId;
import java.util.Calendar;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.time.LocalDate;
import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.parser.DataImporter;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;


public class CalendarActivity extends AppCompatActivity{

    WeekView cal;
    Map<String, Color> colorMap = new HashMap<>();
    Random rnd = new Random();
    Calendar date = Calendar.getInstance();

    List<Instant> loadedDateList = new ArrayList<java.time.Instant>();

    Map<LocalDate, ArrayList<Appointment>> data = null;

    List<String> whiteList = new ArrayList<>();
    List<Calendar> startDateList = new ArrayList<>();
    List<Calendar> endDateList = new ArrayList<>();
    List<String> personList = new ArrayList<>();
    List<String> resourceList = new ArrayList<>();
    List<String> titleList = new ArrayList<>();
    List<String> classList = new ArrayList<>();
    List<String> infoList = new ArrayList<>();

    ArrayList<Events> events = new ArrayList<>();


    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);

        NavigationUtilities.setUpNavigation(this, R.id.Calendar);


        cal = findViewById(R.id.weekView);

        cal.setNumberOfVisibleDays(5);

        cal.setShowFirstDayOfWeekFirst(true);

        cal.setDateFormatter(calendar -> {
            SimpleDateFormat date = new SimpleDateFormat("E dd.MM", Locale.getDefault());
            return date.format(calendar.getTime());
        });
        cal.setTimeFormatter(time -> time + " Uhr");


        ExecutorService executor = Executors.newFixedThreadPool(10);

       executor.submit(runnableTask);

        final AtomicReference<Float>[] x1 = new AtomicReference[]{new AtomicReference<>((float) 0)};
        final float[] x2 = {0};

        // immer nur auf montag scrollen, damit wochentage richtig angezeigt werden.
        date.set(Calendar.DAY_OF_WEEK,
                date.getActualMinimum(Calendar.DAY_OF_WEEK) + 1);
        cal.scrollToDateTime(date);
        cal.setOnTouchListener((v, event) -> {

            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1[0].set(event.getX());
                    break;
                case MotionEvent.ACTION_UP:
                    x2[0] = event.getX();
                    System.out.println(x1[0] + "     " + x2[0]);
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
        LocalDate calInLocalDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        loadedDateList.add(date.toInstant());

        Calendar dateCopy = (Calendar) date.clone();
        dateCopy.add(Calendar.WEEK_OF_YEAR,1);
        LocalDate nextWeek = LocalDateTime.ofInstant(dateCopy.toInstant(), ZoneId.systemDefault()).toLocalDate();

            try {
                data = DataImporter.ImportWeekRange(calInLocalDate,nextWeek, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                assert data != null;
                System.out.println(data);
                saveValues(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };


    public void saveValues(Map<LocalDate, ArrayList<Appointment>> data) throws Exception{
        Adapter adapter = new Adapter();
        cal.setAdapter(adapter);

        LocalDate asLocalDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        ArrayList<Appointment> currentData = data.get(asLocalDate);
        clearLists();

        for (int i = 0; i <= currentData.size() -1;i++){
            if(!titleList.contains(currentData.get(i).getTitle())){
                whiteList.add(currentData.get(i).getTitle());
            }
            startDateList.add(localDateTimeToDate(currentData.get(i).getStartDate()));
            endDateList.add(localDateTimeToDate(currentData.get(i).getEndDate()));
            personList.add(currentData.get(i).getPersons());
            resourceList.add(currentData.get(i).getResources());
            titleList.add(currentData.get(i).getTitle());
            classList.add(currentData.get(i).getClass().toString());
            infoList.add(currentData.get(i).getInfo());
        }
        fillCalendar(adapter);
    }

    public static Calendar localDateTimeToDate(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue()-1, localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        return calendar;
    }

    public void fillCalendar(Adapter adapter){
        for(int i = 0; i< titleList.size() -1; i++){
            final int baseColor = Color.WHITE;
            final int baseRed = Color.red(baseColor);
            final int baseGreen = Color.green(baseColor);
            final int baseBlue = Color.blue(baseColor);

            final int red = (baseRed + rnd.nextInt(256)) / 2;
            final int green = (baseGreen + rnd.nextInt(256)) / 2;
            final int blue = (baseBlue + rnd.nextInt(256)) / 2;

            int rndColor = Color.rgb(red, green, blue);

            WeekViewEntity.Style style = new WeekViewEntity.Style.Builder().setBackgroundColor(rndColor).build();

            if(whiteList.contains(titleList.get(i))){
                Events event = new Events(i, titleList.get(i), startDateList.get(i),endDateList.get(i), personList.get(i) + ", " + classList.get(i), style);
               if (!events.contains(event)) {
                   events.add(event);
                   System.out.println(event.title + event.startTime);
               }
           }
        }
        adapter.submitList(events);
    }

    public void clearLists(){
        startDateList.clear();
        endDateList.clear();
        personList.clear();
        resourceList.clear();
        titleList.clear();
        classList.clear();
        infoList.clear();
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
            Toast.makeText(getApplicationContext(), "Titel: " + data.title + "\nBeschreibung: " + data.description, Toast.LENGTH_LONG).show();
            super.onEventClick(data);
        }

    }



}