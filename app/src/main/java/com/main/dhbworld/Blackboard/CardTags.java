package com.main.dhbworld.Blackboard;

import com.main.dhbworld.R;

public enum CardTags {
    EVENT("Event",  R.color.LightLilaSummer),
    FOUND("Gefunden", R.color.light_transparent),
    RENT("Mieten",  R.color.LightBlueSummer),
   SPORT("Sport",  R.color.teal_700),
    LOST("Verloren",  R.color.LightRedSummer),
    SEARCH_FOR("Gesucht", R.color.LightYellowSummer);

    private final String name;
    private final int color;

    CardTags(String name, int color) {
        this.color = color;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public boolean compare(String cardTagName){
        if (cardTagName.equals(name)) {
            return true;
        }
        return false;

    }
}
