package com.main.dhbworld.Dashboard;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.Constraints;
import androidx.core.graphics.ColorUtils;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.Calendar.CalendarActivity;
import com.main.dhbworld.DashboardActivity;

public class DashboardCardWeather extends DashboardCard{
    LinearLayout forecastLayout;
    public DashboardCardWeather(CardType cardType, LinearLayout outerLayout, MaterialCardView materialCard, LinearLayout coloredButtonBox, LinearLayout innerlayout, LinearLayout forecastLayout) {
        super(cardType, outerLayout, materialCard, coloredButtonBox, innerlayout);
   this.forecastLayout=forecastLayout; }

    @Override
    public void coloreCard(int intensity){
        if (!card_isVisible){
            materialCard.setStrokeColor(ColorUtils.setAlphaComponent(materialCard.getStrokeColor(),intensity));
        }
    }
    @Override
    public void configurateClickers(DashboardActivity currentActivity, Class<Activity> nextActivitys) {
        materialCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (configurationModus) {
                    if (card_isVisible) {
                        card_isVisible = false; //True = Card is visible
                        materialCard.setStrokeColor(ColorUtils.setAlphaComponent(materialCard.getStrokeColor(), 50));
                    } else {
                        card_isVisible = true;//True = Card is visible
                        materialCard.setStrokeColor(ColorUtils.setAlphaComponent(materialCard.getStrokeColor(), 255));
                    }
                } else {
                    if (forecastLayout.getVisibility() == View.GONE) {
                        expand(forecastLayout);
                    } else {
                        collapse(forecastLayout);
                    }
                }
            }

            public void expand(final View v) {
                int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
                int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
                final int targetHeight = v.getMeasuredHeight();

                // Older versions of android (pre API 21) cancel animations for views with a height of 0.
                v.getLayoutParams().height = 1;
                v.setVisibility(View.VISIBLE);
                Animation a = new Animation()
                {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        v.getLayoutParams().height = interpolatedTime == 1
                                ? Constraints.LayoutParams.WRAP_CONTENT
                                : (int)(targetHeight * interpolatedTime);
                        v.requestLayout();
                    }
                };

                // Expansion speed of 1dp/ms
                a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
                v.startAnimation(a);
            }

            public void collapse(final View v) {
                final int initialHeight = v.getMeasuredHeight();

                Animation a = new Animation()
                {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        if(interpolatedTime == 1){
                            v.setVisibility(View.GONE);
                        }else{
                            v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                            v.requestLayout();
                        }
                    }
                };

                // Collapse speed of 1dp/ms
                a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
                v.startAnimation(a);
            }
        });

    }
}

