package com.main.dhbworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.main.dhbworld.Navigation.NavigationUtilities;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.time.LocalDate;
import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.parser.DataImporter;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;


public class CalendarActivity extends AppCompatActivity{
    WeekView cal;
    Calendar date = Calendar.getInstance();
    boolean useTempCal;
    List<Instant> loadedDateList = new ArrayList<>();
    ArrayList<Events> events = new ArrayList<>();
    ArrayList<String> blackList = new ArrayList<>();

    boolean firstClick = false;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);
        NavigationUtilities.setUpNavigation(this, R.id.Calendar);

        setCalSettings();
        ExecutorService executor = Executors.newCachedThreadPool();
        // immer nur auf montag scrollen, damit wochentage richtig angezeigt werden.
        setDates();

        executor.submit(runnableTask);
        setOnTouchListener(cal, executor);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_top_app_bar, menu);
        return true;
    }

    public void setCalSettings(){
        cal = findViewById(R.id.weekView);
        cal.setNumberOfVisibleDays(5);
        cal.setShowFirstDayOfWeekFirst(true);
        cal.setNowLineStrokeWidth(6);
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


    public void openFilterClick(@NonNull MenuItem item){
        final String[] listItems = arrayConvertor(EventCreator.getAllTitleList());
        final boolean[] checkedItems = new boolean[listItems.length];
        final List<String> selectedItems = Arrays.asList(listItems);

        for(int i = 0; i < listItems.length; i++){
            if(blackList.contains(listItems[i])){
                checkedItems[i] = false;
            }
            else{
                checkedItems[i] = true;
            }
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
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            if(!checkedItems[i]){
                                blackList.add(listItems[i]);
                            }
                            EventCreator.setBlackList(blackList);
                            EventCreator.applyBlackList();
                        }
                    }
                });

                // handle the negative button of the alert dialog
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

               //TODO When neutral button is pressed, the Dialog should still stay!
                builder.setNeutralButton("RESET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Arrays.fill(checkedItems, true);
                    }
                });
                builder.create();

                AlertDialog dialog = builder.create();
                dialog.show();

    }

    public void setDates(){
        date.set(Calendar.DAY_OF_WEEK,
                date.getActualMinimum(Calendar.DAY_OF_WEEK) + 1);

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
            cal.scrollToDate(date);
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
        Map<LocalDate, ArrayList<Appointment>> data = null;
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

    public void fillAdapter(Adapter adapter){
        events = Events.getEvents();
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