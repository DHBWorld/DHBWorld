package com.main.dhbworld.Dashboard.DataLoaders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.main.dhbworld.CantineActivity;
import com.main.dhbworld.Utilities.ProgressIndicator;
import com.main.dhbworld.R;

import java.util.List;

public class DataLoaderMealPlan {

    Context context;
    LinearLayout layoutCardMealPlan;
    LinearLayout[] layoutMeal;
    TextView[] textViewMeal;
    ImageView imageViewMeal;

    public DataLoaderMealPlan(Context context, LinearLayout layoutCardMealPlan, LinearLayout[] layoutMeal, TextView[] textViewMeal, ImageView imageViewMeal) {
        this.context = context;
        this.layoutCardMealPlan = layoutCardMealPlan;
        this.layoutMeal = layoutMeal;
        this.textViewMeal = textViewMeal;
        this.imageViewMeal = imageViewMeal;

    }

    public void load() {
        ProgressIndicator indicator = new ProgressIndicator(context, layoutCardMealPlan, layoutMeal);
        indicator.show();

        new Thread(() -> {
            List<String> meals = CantineActivity.loadDataForDashboard();
            layoutCardMealPlan.post(() -> {

                indicator.hide();
                if ((meals == null) || (meals.size() == 0)) {
                    imageViewMeal.setImageResource(R.drawable.ic_no_meals);
                    textViewMeal[0].setText(R.string.thereIsntAnyInfo);
                    layoutMeal[1].setVisibility(View.GONE);
                    layoutMeal[2].setVisibility(View.GONE);
                } else {
                    imageViewMeal.setImageResource(R.drawable.ic_restaurant);
                    for (int i = 0; i < 3; i++) {
                        textViewMeal[i].setVisibility(View.VISIBLE);
                        if (meals.size() > i) {
                            textViewMeal[i].setText(meals.get(i));
                        } else {
                            layoutMeal[i].setVisibility(View.GONE);
                        }
                    }
                }

            });
        }).start();
    }
}
