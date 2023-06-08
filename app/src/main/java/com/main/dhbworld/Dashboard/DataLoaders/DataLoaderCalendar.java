package com.main.dhbworld.Dashboard.DataLoaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.preference.PreferenceManager;

import com.main.dhbworld.Calendar.nextEventsProvider;
import com.main.dhbworld.R;
import com.main.dhbworld.Utilities.ProgressIndicator;

import java.time.Duration;
import java.time.LocalDateTime;

import dhbw.timetable.rapla.data.event.Appointment;

public class DataLoaderCalendar {

    private final ImageView uniImage;
    private final TextView nextClassView;
    private final LinearLayout layoutTime;
    private final LinearLayout layoutCardCalendarInformation;
    private final LinearLayout layoutTimeDigit;
    private final TextView timeView;
    private final TextView timeViewMin;
    private final TextView letterTimeView;
    private final Context context;
    private final LinearLayout layoutCardCalendar;

    public DataLoaderCalendar(Context context, LinearLayout layoutCardCalendar, ImageView uniImage, TextView nextClassView, LinearLayout layoutTime, LinearLayout layoutCardCalendarInformation, LinearLayout layoutTimeDigit, TextView timeView, TextView timeViewMin, TextView letterTimeView) {
        this.uniImage = uniImage;
        this.nextClassView = nextClassView;
        this.layoutTime = layoutTime;
        this.layoutCardCalendarInformation = layoutCardCalendarInformation;
        this.layoutTimeDigit = layoutTimeDigit;
        this.timeView = timeView;
        this.timeViewMin = timeViewMin;
        this.letterTimeView = letterTimeView;
        this.context = context;
        this.layoutCardCalendar = layoutCardCalendar;

    }

    public void load() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = preferences.getString("CurrentURL", null);

        if ((url == null) || (url.equals(""))) {
            showMessage(context.getResources().getString(R.string.pasteLinkInCalender));
            uniImage.setVisibility(View.GONE);
            return;
        }
        layoutCardCalendarInformation.setVisibility(View.GONE);
        ProgressIndicator indicator = new ProgressIndicator(context, layoutCardCalendar);
        indicator.show();
        new Thread(() -> {
            try {
                nextEventsProvider nextEventsProvider = new nextEventsProvider(context);
                Appointment nextClass = nextEventsProvider.getNextEvent();
                layoutCardCalendar.post(new DataLoader(nextClass, indicator));
            } catch (Exception e) {
                layoutCardCalendar.post(new ErrorLoader(indicator));
            }
        }).start();

    }

    private void nextLectureComesNotSoon(ProgressIndicator indicator) {
        indicator.hide();
        layoutCardCalendarInformation.setVisibility(View.VISIBLE);
        uniImage.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_pause));
        showMessage(context.getString(R.string.no_classes));
    }

    private void nextLectureComesSoon(Appointment nextClass, ProgressIndicator indicator) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startClass = nextClass.getStartDate();
        LocalDateTime endClass = nextClass.getEndDate();
        Duration durationUntilStartOfClass = Duration.between(now, startClass);
        Duration durationUntilEndOfClass = Duration.between(now, endClass);
        indicator.hide();
        layoutCardCalendarInformation.setVisibility(View.VISIBLE);
        if ((durationUntilStartOfClass.toHours() <= 8) && (durationUntilEndOfClass.toMinutes() >= 0)) {
            showNextLectureWithCounter(nextClass, durationUntilStartOfClass, durationUntilEndOfClass);
        } else {
            showNextLectureWithFixedTime(nextClass, startClass);

        }
    }

    private void showNextLectureWithCounter(Appointment nextClass, Duration durationUntilStartOfClass, Duration durationUntilEndOfClass) {
        uniImage.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_uni));
        nextClassView.setText(nextClass.getTitle());
        timeView.setText(nextClass.getStartTime());
        timeViewMin.setVisibility(View.VISIBLE);
        timeViewMin.setText(context.getResources().getString(R.string.min));
        if (durationUntilStartOfClass.toMinutes() >= 0) {
            new CountDownTimerOne(durationUntilStartOfClass.toMinutes() * 60000, 60000).start();
        } else {
            new CountDownTimerTwo(durationUntilEndOfClass.toMinutes() * 60000, 60000);
        }

    }

    private void showNextLectureWithFixedTime(Appointment nextClass, LocalDateTime startClass) {
        uniImage.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_uni));
        nextClassView.setText(nextClass.getTitle());
        changeViews(nextClass.getStartTime(), startClass.getDayOfWeek().toString(), "");
        timeViewMin.setVisibility(View.GONE);
    }

    private void changeViews(String timeView, String letterTimeView, String timeViewMin) {
        this.timeView.setText(timeView);
        this.letterTimeView.setText(letterTimeView);
        this.timeViewMin.setText(timeViewMin);

    }

    private void showMessage(String message) {
        layoutTimeDigit.setVisibility(View.GONE);
        layoutTime.setVisibility(View.GONE);
        timeView.setVisibility(View.GONE);
        timeViewMin.setVisibility(View.GONE);
        letterTimeView.setVisibility(View.GONE);
        nextClassView.setText(message);
    }

    private class CountDownTimerOne extends CountDownTimer {
        public CountDownTimerOne(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUtilFinished) {
            changeViews(Long.toString(millisUtilFinished / 60000 + 1), context.getResources().getString(R.string.startsIn), context.getResources().getString(R.string.min));

        }

        @Override
        public void onFinish() {
            changeViews(context.getResources().getString(R.string.now), "", "");

        }
    }

    private class CountDownTimerTwo extends CountDownTimer {
        public CountDownTimerTwo(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onTick(long millisUtilFinished) {
            changeViews(
                    Long.toString(millisUtilFinished / 60000 + 1),
                    context.getResources().getString(R.string.endsIn),
                    context.getResources().getString(R.string.min));
        }

        @Override
        public void onFinish() {
            changeViews("", "", "");
            nextClassView.setText(context.getResources().getString(R.string.pause));
            uniImage.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_pause));
        }
    }

    private class DataLoader implements Runnable {
        ProgressIndicator indicator;
        Appointment nextClass;

        public DataLoader(Appointment nextClass, ProgressIndicator indicator) {
            this.indicator = indicator;
            this.nextClass = nextClass;
        }

        @Override
        public void run() {
            if (nextClass == null || nextClass.getStartDate() == null) {
                nextLectureComesNotSoon(indicator);
            } else {
                nextLectureComesSoon(nextClass, indicator);
            }
        }
    }

    private class ErrorLoader extends DataLoader {
        public ErrorLoader(ProgressIndicator indicator) {
            super(null, indicator);
        }

        @Override
        public void run() {
            indicator.hide();
            layoutCardCalendarInformation.setVisibility(View.VISIBLE);
            DataLoaderCalendar.this.showMessage(context.getResources().getString(R.string.problemsWithCalenderView));
            uniImage.setVisibility(View.GONE);
        }
    }
}


