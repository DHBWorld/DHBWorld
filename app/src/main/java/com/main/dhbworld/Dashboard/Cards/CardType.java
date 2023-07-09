package com.main.dhbworld.Dashboard.Cards;

import android.app.Activity;

import com.main.dhbworld.BlackboardActivity;
import com.main.dhbworld.Calendar.CalendarActivity;
import com.main.dhbworld.CantineActivity;
import com.main.dhbworld.KVVActivity;
import com.main.dhbworld.MainActivity;
import com.main.dhbworld.UserInteractionActivity;

public enum CardType {

    CARD_KVV(KVVActivity.class, "cardKvv"),
    CARD_PI(MainActivity.class, "cardPI"),
    CARD_CALENDAR(CalendarActivity.class, "cardCalendar"),
    CARD_MEAL_PLAN(CantineActivity.class, "cardMealPlan"),
    CARD_INFO(CantineActivity.class, "cardInfo"),
    CARD_WEATHER(null, "cardWeather"),
    CARD_USER_INTERACTION(UserInteractionActivity.class, "cardUserInt"),
    CARD_BB(BlackboardActivity.class, "cardBb");

    private final Class <Activity> link;
    private final String savedIn;

     CardType(Class link, String savedIn) {
        this.link = link;
        this.savedIn = savedIn;
    }

    public Class <Activity> getLink() {
        return link;
    }

    public  String getSavedIn(){
        return savedIn;
    }


}
