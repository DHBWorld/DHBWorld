package com.main.dhbworld.Dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.graphics.ColorUtils;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.R;

public class DashCard extends ViewGroup {



    public Boolean cardKvv_isVisible = true;
    public Boolean cardInfo_isVisible = true;
    public  Boolean cardWeather_isVisible = true;

    public MaterialCardView card_dash_calendar;
    public MaterialCardView card_dash_pi;
    public MaterialCardView card_dash_kvv;

    public  MaterialCardView card_dash_info;
    public  MaterialCardView card_dash_user_interaction;
    public MaterialCardView card_dash_weather;

    public LinearLayout forecastLayout;

    public LinearLayout boxCardCalendar;
    public LinearLayout boxCardPI;

    public LinearLayout boxCardKvv;
    public LinearLayout boxCardInfo;

    public LinearLayout card_dash_calendar_layout;
    public LinearLayout card_dash_pi_layout;
    public LinearLayout card_dash_kvv_layout;

    public LinearLayout card_dash_user_interaction_layout;
    public LinearLayout card_dash_info_layout;







    public Boolean cardMealPlan_isVisible = true;


    public MaterialCardView card_dash_mealPlan;



    public  LinearLayout boxCardMealPlan;



    public  LinearLayout card_dash_mealPlan_layout;


    private LinearLayout layoutCardMealPlan;












    public DashCard mealPlan;
    Button btns[];


    public DashCard(Context context) {
        super(context);
        configureCard(context);
        userConfigurationOfDashboard();

    }

    public DashCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        configureCard(context);
        userConfigurationOfDashboard();



    }

    public DashCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configureCard(context);
        userConfigurationOfDashboard();

    }

    public DashCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        configureCard(context);
        userConfigurationOfDashboard();


    }
    private void configureCard(Context context){
        a=0;
        layoutCardMealPlan= findViewById(R.id.layoutCardMealPlan);

        card_dash_mealPlan = findViewById(R.id.card_dash_mealPlan);

        boxCardMealPlan= findViewById(R.id.buttonCardMealPlan);
        card_dash_mealPlan_layout = findViewById(R.id.card_dash_mealPlan_layout);




       /* btns = new Button[10];
        Random rnd = new Random(System.currentTimeMillis());
        for (int i=0; i<btns.length; i++) {
            btns[i] = new Button(context);
            btns[i].setText(rnd.nextBoolean() ? "LongText" : "TXT");
            btns[i].setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {

                    btns[3].setVisibility(INVISIBLE);
                }
            });
            this.addView(btns[i]);

        }*/
    }
    private int a;
    private void userConfigurationOfDashboard(){




        boxCardMealPlan.setBackgroundColor((ColorUtils.setAlphaComponent(getResources().getColor(R.color.black),0)));





        if (a==0){
            card_dash_mealPlan.setVisibility(View.GONE);
        }else
        {
            a=1;
            card_dash_mealPlan.setVisibility(View.VISIBLE);
        }


        card_dash_mealPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (cardMealPlan_isVisible) {
                        cardMealPlan_isVisible = false; //True = Card is visible
                        card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(), 50));
                        card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(), 50));
                    } else {
                        cardMealPlan_isVisible = true;//True = Card is visible
                        card_dash_mealPlan.setStrokeColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(), 255));
                        card_dash_mealPlan_layout.setBackgroundColor(ColorUtils.setAlphaComponent(card_dash_mealPlan.getStrokeColor(), 255));
                    }
            }
        });

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        //get the available size of child view
        int childLeft = this.getPaddingLeft();
        int childTop = this.getPaddingTop();
        int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        int childWidth = childRight - childLeft;
        int childHeight = childBottom - childTop;

        maxHeight = 0;
        curLeft = childLeft;
        curTop = childTop;
        //walk through each child, and arrange it from left to right
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
                //Get the maximum size of the child
                child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
                curWidth = child.getMeasuredWidth();
                curHeight = child.getMeasuredHeight();

                //do the layout
               child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
                //store the max height
                if (maxHeight < curHeight)
                    maxHeight = curHeight;
                curLeft += curWidth;

        }



    }
}