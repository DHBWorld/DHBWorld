package com.main.dhbworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.main.dhbworld.Navigation.NavigationUtilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;


public class CalendarActivity extends AppCompatActivity {

    WeekView cal;


    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);

        NavigationUtilities.setUpNavigation(this, R.id.Calendar);

        cal = findViewById(R.id.weekView);

        cal.setNumberOfVisibleDays(5);
        cal.setShowFirstDayOfWeekFirst(false);

        cal.setDateFormatter(calendar -> {
            SimpleDateFormat date = new SimpleDateFormat("E dd.MM", Locale.getDefault());
            return date.format(calendar.getTime());
        });

        cal.setTimeFormatter(time -> time + " Uhr");


        final AtomicReference<Float>[] x1 = new AtomicReference[]{new AtomicReference<>((float) 0)};
        final float[] x2 = {0};




        //set onClickListener for Events

        Adapter adapter = new Adapter();
        cal.setAdapter(adapter);

        Calendar start1 = Calendar.getInstance();
        start1.set(Calendar.DAY_OF_MONTH, 13);
        start1.set(Calendar.HOUR_OF_DAY, 10);
        start1.set(Calendar.MINUTE, 0);

        Calendar end1 = Calendar.getInstance();
        end1.set(Calendar.DAY_OF_MONTH, 13);
        end1.set(Calendar.HOUR_OF_DAY, 12);
        end1.set(Calendar.MINUTE, 0);
        
        Calendar start2 = Calendar.getInstance();
        start2.set(Calendar.DAY_OF_MONTH, 14);
        start2.set(Calendar.HOUR_OF_DAY, 13);
        start2.set(Calendar.MINUTE, 0);

        Calendar end2 = Calendar.getInstance();
        end2.set(Calendar.DAY_OF_MONTH, 14);
        end2.set(Calendar.HOUR_OF_DAY, 15);
        end2.set(Calendar.MINUTE, 30);


        Calendar start3 = Calendar.getInstance();
        start3.set(Calendar.DAY_OF_MONTH, 15);
        start3.set(Calendar.HOUR_OF_DAY, 14);
        start3.set(Calendar.MINUTE, 0);

        Calendar end3 = Calendar.getInstance();
        end3.set(Calendar.DAY_OF_MONTH, 15);
        end3.set(Calendar.HOUR_OF_DAY, 16);
        end3.set(Calendar.MINUTE, 0);
        
        Calendar start4 = Calendar.getInstance();
        start4.set(Calendar.DAY_OF_MONTH, 16);
        start4.set(Calendar.HOUR_OF_DAY, 8);
        start4.set(Calendar.MINUTE, 30);

        Calendar end4 = Calendar.getInstance();
        end4.set(Calendar.DAY_OF_MONTH, 16);
        end4.set(Calendar.HOUR_OF_DAY, 12);
        end4.set(Calendar.MINUTE, 30);

        
        Calendar start5 = Calendar.getInstance();
        start5.set(Calendar.DAY_OF_MONTH, 17);
        start5.set(Calendar.HOUR_OF_DAY, 10);
        start5.set(Calendar.MINUTE, 0);

        Calendar end5 = Calendar.getInstance();
        end5.set(Calendar.DAY_OF_MONTH, 17);
        end5.set(Calendar.HOUR_OF_DAY, 12);
        end5.set(Calendar.MINUTE, 0);
        
        ArrayList<Events> events = new ArrayList<>();
       
        
        events.add(new Events(1, "Klausur Betriebssysteme", start1, end1, "Faber, Uwe \n Hörsaal A172 \n TINF20B4"));
        events.add(new Events(2, "Rechnerarchtiektur", start2, end2, "Roethig, Juergen"));
        events.add(new Events(3, "Klausur Numerik", start3, end3, "Lausen, Ralph \n Hörsaal A172 \n TINF20B4"));
        events.add(new Events(4, "Grundlagen des Software Engineering", start4, end4, "Berkling, Kay Margerethe"));
        events.add(new Events(5, "Klausur Webengineering II", start5, end5, "Pfeil, Daniel \n Hörsaal A172 \n TINF20B4 & TINF20B5"));
        
        adapter.submitList(events);


        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, 13);
        date.set(Calendar.HOUR_OF_DAY, 8);
        date.set(Calendar.MINUTE, 30);
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
                        return true;
                    }else if(deltaX > 100){
                        date.add(Calendar.WEEK_OF_YEAR,-1);
                        cal.scrollToDate(date);
                        return true;
                    }
                    break;
            }

            return false;
        });


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
                    .build();
        }

        @Override
        public void onEventClick(Events data) {
            Toast.makeText(getApplicationContext(), "Titel: " + data.title + "\nBeschreibung: " + data.description, Toast.LENGTH_LONG).show();
            super.onEventClick(data);
        }

    }



}