package com.main.dhbworld.Blackboard;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.R;


public class BlackboardCard extends MaterialCardView {
   private TextView text;
    private TextView title;
    private LinearLayout board;
    private Context context;
    private int color;
    private MaterialCardView card;
    private LinearLayout cardLayout;

    public BlackboardCard(Context context, LinearLayout board, int color) {
        super(context);

        this.context = context;
        this.board = board;
        this.color = color;


        createBox();
        createColorfulBoxInside();
        addFields();
        addOutsidePadding();

    }

    private void createBox() {
        card = new MaterialCardView(context);
        card.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        card.setStrokeColor(context.getResources().getColor(color));
        card.setCardElevation(0);
        card.setStrokeWidth(10);
        board.addView(card);
    }

    private void createColorfulBoxInside() {
        LinearLayout paddingRed = new LinearLayout(context);
        paddingRed.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        paddingRed.setPadding(0, 70, 0, 0);
        paddingRed.setBackgroundColor(context.getResources().getColor(color));
        card.addView(paddingRed);

        cardLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mealLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        mealLayoutParams.weight = 1f;
        cardLayout.setLayoutParams(mealLayoutParams);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(50, 20, 50, 20);
        paddingRed.addView(cardLayout);
    }

    private void addFields() {
        title = new TextView(context);
        title.setTextSize(17);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cardLayout.addView(title);

        text = new TextView(context);
        text.setTextSize(15);
        text.setPadding(0, 10, 0, 10);
        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cardLayout.addView(text);
    }

    private void addOutsidePadding() {
        LinearLayout padding = new LinearLayout(context);
        padding.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        padding.setPadding(0, 60, 0, 0);
        board.addView(padding);
    }


    public void setText(String text) {
        this.text.setText(text);

    }

    public void setTitle(String title) {
        this.title.setText(title);
    }
}
