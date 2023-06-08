package com.main.dhbworld.Dashboard;

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
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.ProgressIndicator;
import com.main.dhbworld.R;

import java.time.Duration;
import java.time.LocalDateTime;

import dhbw.timetable.rapla.data.event.Appointment;

public class DataLoaderCalendar {

    ImageView uniImage ;
    TextView nextClassView ;
    LinearLayout layoutTime ;
    LinearLayout layoutCardCalendarInformation ;
    LinearLayout layoutTimeDigit ;
    TextView timeView ;
    TextView timeViewMin ;
    TextView letterTimeView ;
    Context context;
    LinearLayout layoutCardCalendar;

    public DataLoaderCalendar(Context context, LinearLayout layoutCardCalendar,  ImageView uniImage, TextView nextClassView, LinearLayout layoutTime, LinearLayout layoutCardCalendarInformation, LinearLayout layoutTimeDigit, TextView timeView, TextView timeViewMin, TextView letterTimeView) {
    this.uniImage=uniImage;
    this.nextClassView=nextClassView;
    this.layoutTime=layoutTime;
    this.layoutCardCalendarInformation=layoutCardCalendarInformation;
    this.layoutTimeDigit=layoutTimeDigit;
    this.timeView=timeView;
    this.timeViewMin=timeViewMin;
    this.letterTimeView=letterTimeView;
    this.context=context;
   this.layoutCardCalendar=layoutCardCalendar;

    }

    public void load(){
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = preferences.getString("CurrentURL",null);

        if (!(url ==null) && (!url.equals(""))) {
            layoutCardCalendarInformation.setVisibility(View.GONE);
            ProgressIndicator indicator= new ProgressIndicator(context, layoutCardCalendar);
            indicator.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nextEventsProvider nextEventsProvider = new nextEventsProvider(context);
                        Appointment nextClass = nextEventsProvider.getNextEvent();

                        layoutCardCalendar.post(new Runnable() {
                            @Override
                            public void run() {
                                if(nextClass == null || nextClass.getStartDate() == null){
                                    indicator.hide();
                                    layoutCardCalendarInformation.setVisibility(View.VISIBLE);
                                    nextClassView.setText(context.getString(R.string.no_classes));
                                    layoutTimeDigit.setVisibility(View.GONE);
                                    layoutTime.setVisibility(View.GONE);
                                    timeView.setVisibility(View.GONE);
                                    timeViewMin.setVisibility(View.GONE);
                                    letterTimeView.setVisibility(View.GONE);
                                    uniImage.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_pause));
                                }
                                else {
                                    LocalDateTime now = LocalDateTime.now();
                                    LocalDateTime startClass = nextClass.getStartDate();
                                    LocalDateTime endClass = nextClass.getEndDate();
                                    Duration durationUntilStartOfClass = Duration.between(now, startClass);
                                    Duration durationUntilEndOfClass = Duration.between(now, endClass);
                                    indicator.hide();
                                    layoutCardCalendarInformation.setVisibility(View.VISIBLE);
                                    if ((durationUntilStartOfClass.toHours() <= 8) && (durationUntilEndOfClass.toMinutes() >= 0)) {
                                        uniImage.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_uni));
                                        nextClassView.setText(nextClass.getTitle());
                                        timeView.setText(nextClass.getStartTime());
                                        timeViewMin.setVisibility(View.VISIBLE);
                                        timeViewMin.setText(context.getResources().getString(R.string.min));
                                        if (durationUntilStartOfClass.toMinutes() >= 0) {
                                            new CountDownTimer(durationUntilStartOfClass.toMinutes() * 60000, 60000) {
                                                public void onTick(long millisUtilFinished) {
                                                    timeView.setText(Long.toString(millisUtilFinished / 60000 + 1));
                                                    letterTimeView.setText(context.getResources().getString(R.string.startsIn));
                                                    timeViewMin.setText(context.getResources().getString(R.string.min));
                                                }

                                                @Override
                                                public void onFinish() {
                                                    timeView.setText(context.getResources().getString(R.string.now));
                                                    letterTimeView.setText("");
                                                    timeViewMin.setText("");
                                                }
                                            }.start();
                                        } else {
                                            new CountDownTimer(durationUntilEndOfClass.toMinutes() * 60000, 60000) {
                                                public void onTick(long millisUtilFinished) {
                                                    timeView.setText(Long.toString(millisUtilFinished / 60000 + 1));
                                                    letterTimeView.setText(context.getResources().getString(R.string.endsIn));
                                                    timeViewMin.setText(context.getResources().getString(R.string.min));
                                                }

                                                @Override
                                                public void onFinish() {
                                                    timeView.setText("");
                                                    nextClassView.setText(context.getResources().getString(R.string.pause));
                                                    letterTimeView.setText("");
                                                    timeViewMin.setText("");
                                                    uniImage.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_pause));
                                                }
                                            }.start();
                                        }
                                    } else {
                                        uniImage.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_uni));
                                        nextClassView.setText(nextClass.getTitle());
                                        timeView.setText(nextClass.getStartTime());
                                        timeViewMin.setVisibility(View.GONE);
                                        letterTimeView.setText(startClass.getDayOfWeek().toString());
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        layoutCardCalendar.post(new Runnable() {
                            @Override
                            public void run() {
                                indicator.hide();
                                layoutCardCalendarInformation.setVisibility(View.VISIBLE);
                                layoutTimeDigit.setVisibility(View.GONE);
                                layoutTime.setVisibility(View.GONE);
                                uniImage.setVisibility(View.GONE);
                                timeView.setVisibility(View.GONE);
                                timeViewMin.setVisibility(View.GONE);
                                letterTimeView.setVisibility(View.GONE);
                                nextClassView.setText(context.getResources().getString(R.string.problemsWithCalenderView));
                            }
                        });
                    }
                }
            }).start();
        }else{
            layoutTimeDigit.setVisibility(View.GONE);
            layoutTime.setVisibility(View.GONE);
            uniImage.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
            timeViewMin.setVisibility(View.GONE);
            letterTimeView.setVisibility(View.GONE);
            nextClassView.setText(context.getResources().getString(R.string.pasteLinkInCalender));

        }

    }
}
