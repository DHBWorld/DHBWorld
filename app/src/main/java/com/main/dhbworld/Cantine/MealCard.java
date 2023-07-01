package com.main.dhbworld.Cantine;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.main.dhbworld.R;
import java.text.NumberFormat;
import java.util.Currency;

public class MealCard extends MaterialCardView {
    private final Context context;
    private final Meal meal;
    private LinearLayout chipLayout;
    private LinearLayout mealLayout;
    private  LinearLayout cardLayout;
    private  LinearLayout preisLayout;

    public MealCard(Context context, Meal meal) {
        super(context);
        this.context=context;
        this.meal=meal;
        buildCard();
    }

    private void buildCard(){
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.setStrokeColor(getResources().getColor(R.color.grey_dark));
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.dashboardCardBackground, value, true);
        this.setBackgroundColor(value.data);

        buildCardLayout();

    }

    private void buildMealLayout() {
        mealLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mealLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        mealLayoutParams.weight = 1f;
        mealLayout.setLayoutParams(mealLayoutParams);
        mealLayout.setOrientation(LinearLayout.VERTICAL);
        mealLayout.setPadding(0,0,10,0);
        cardLayout.addView(mealLayout);

        addMealName();
        addNotes();
        addPreis();
    }

    private void buildPreisLayout() {
        preisLayout = new LinearLayout(context);
        preisLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        preisLayout.setPadding(10, 0, 10, 0);
        preisLayout.setOrientation(LinearLayout.VERTICAL);

        TypedValue valuePreisBg = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.dashboardCardBackgroundElevated, valuePreisBg, true);
        preisLayout.setBackgroundColor(valuePreisBg.data);
        preisLayout.setGravity(Gravity.CENTER);
        cardLayout.addView(preisLayout);
    }

    private void addPreis(){
        buildPreisLayout();
        TextView preisView = new TextView(context);
        preisView.setTextSize(18);
        //preisView.setTextColor(getResources().getColor(R.color.grey_dark));
        preisView.setText(formatMealPrice(meal));
        preisView.setGravity(Gravity.CENTER);
        preisView.setMaxLines(1);
        preisView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        preisLayout.addView(preisView);
    }

    private void addMealName() {
        TextView mealView = new TextView(context);
        mealView.setTextSize(15);
        mealView.setText(meal.getName());
        mealView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mealLayout.addView(mealView);
    }

    private void buildCardLayout(){
        cardLayout = new LinearLayout(context);
        cardLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cardLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardLayout.setPadding(0,20,0,20);
        this.addView(cardLayout);
        buildMealLayout();
    }
    private void buildNoteLayout(){
        HorizontalScrollView chipScroll = new HorizontalScrollView(context);
        chipScroll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chipScroll.setHorizontalScrollBarEnabled(false);
        chipScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mealLayout.addView(chipScroll);

        chipLayout = new LinearLayout(context);
        chipLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        chipLayout.setOrientation(LinearLayout.HORIZONTAL);
        chipScroll.addView(chipLayout);

    }

    private void addNotes() {
        buildNoteLayout();
        if (meal.getNotes().size() == 0) {
            addInvisibleNote();
        }
        for (String note : meal.getNotes()) {
            addVisibleNote(note);

        }
    }

    private void addInvisibleNote(){
        Chip chip = new Chip(context);
        chip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        chip.setTextSize(12);
        chip.setText(" ");
        chip.setVisibility(View.INVISIBLE);
        chipLayout.addView(chip);
    }

    private void addVisibleNote(String note){
        Chip chip = new Chip(context);
        chip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        chip.setText(note);
        chip.setTextSize(12);
        chip.setGravity(Gravity.CENTER);
        chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        chip.setCheckable(false);
        chip.setClickable(false);
        chipLayout.addView(chip);
    }

    private String formatMealPrice(Meal meal) {
        try {
            double price = Double.parseDouble(meal.getPrice());
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setCurrency(Currency.getInstance("EUR"));
            return format.format(price);
        } catch (NumberFormatException e) {
            return "-";
        }
    }
}
