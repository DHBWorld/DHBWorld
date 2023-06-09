package com.main.dhbworld.Blackboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.BlackboardActivity;
import com.main.dhbworld.R;


public class BlackboardCard extends MaterialCardView{
    TextView text;
    TextView title;
    LinearLayout board;
    Context context;

    public BlackboardCard(Context context, LinearLayout board){
        super(context);

        this.context=context;
        this.board=board;



        MaterialCardView advertCard= new MaterialCardView(context);
        advertCard.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        advertCard.setStrokeColor(context.getResources().getColor(R.color.red));
        advertCard.setCardElevation(0);
        advertCard.setStrokeWidth(10);
        board.addView(advertCard);

        LinearLayout paddingRed= new LinearLayout(context);
        paddingRed.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        paddingRed.setPadding(0,70,0,0);
        paddingRed.setBackgroundColor(context.getResources().getColor(R.color.red));
        advertCard.addView(paddingRed);


        LinearLayout cardLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mealLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        mealLayoutParams.weight = 1f;
        cardLayout.setLayoutParams(mealLayoutParams);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(50,20,50,20);
        paddingRed.addView(cardLayout);
        title = new TextView(context);
        title.setTextSize(18);
        title.setTypeface(Typeface.DEFAULT_BOLD);

        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cardLayout.addView(title);

        text = new TextView(context);
        text.setTextSize(15);


        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cardLayout.addView(text);

        addPadding();

    }

    private void addPadding(){
        LinearLayout padding= new LinearLayout(context);
        padding.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        padding.setPadding(0,60,0,0);
        board.addView(padding);
    }

    public void setText(String text){
        this.text.setText(text);

    }
    public void setTitle(String title){
        this.title.setText(title);
    }
}
