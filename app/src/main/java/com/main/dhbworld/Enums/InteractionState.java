package com.main.dhbworld.Enums;
// Zustände von dem Drücker, Kaffemaschine und Kantine

import android.content.Context;
import android.content.res.Resources;

import com.main.dhbworld.R;

public enum InteractionState {
    NORMAL(0, R.string.is_working, R.color.grey_dark_but_a_bit_lighter),
    DEFECT(1, R.string.defective, R.color.grey_defect),
    CLEANING(2, R.string.getting_cleaned, R.color.blue_cleaning),

    QUEUE_LONG(6, R.string.long_queue, R.color.red_dark),
    QUEUE_MIDDLE(5, R.string.medium_queue, R.color.orange_queue),
    QUEUE_SHORT(4, R.string.short_queue, R.color.green_queue),
    QUEUE_ABCENT(3, R.string.no_queue, R.color.grey_dark_but_a_bit_lighter);




    private int id;
    private int color;
    private int text;

    InteractionState(int id, int text, int color){
        this.id = id;
        this.color= color;
        this.text=text;
    }

    public int getColor() {
        return color;
    }

    public String getText(Context context) {
        return context.getResources().getString(text);
    }

    public int getId() {
        return id;
    }

    public static InteractionState parseId(int id) {
        switch (id) {
            case 0:
                return NORMAL;
            case 1:
                return DEFECT;
            case 2:
                return CLEANING;
            case 3:
                return QUEUE_ABCENT;
            case 4:
                return QUEUE_SHORT;
            case 5:
                return QUEUE_MIDDLE;
            case 6:
                return QUEUE_LONG;
            default:
                return null;
        }

    }
}
