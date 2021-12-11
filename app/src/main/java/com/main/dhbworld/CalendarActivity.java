package com.main.dhbworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;


public class CalendarActivity extends AppCompatActivity {

    WeekView cal;


    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);

        cal = findViewById(R.id.weekView);

        cal.setNumberOfVisibleDays(5);
        cal.setShowFirstDayOfWeekFirst(false);

        cal.setDateFormatter(calendar -> {
            SimpleDateFormat date = new SimpleDateFormat("E dd.MM", Locale.getDefault());
            return date.format(calendar.getTime());
        });

        cal.setTimeFormatter(time -> time + " Uhr");

        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.scrollToDate(date);

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


        //set onClickListener for Events

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
}