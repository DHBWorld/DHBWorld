package com.main.dhbworld.Blackboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.Constraints;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.main.dhbworld.R;
import java.util.ArrayList;
import java.util.Objects;


public class BlackboardCard extends MaterialCardView {
    private TextView text;
    private TextView title;
    private final LinearLayout board;
    private final Context context;
    private final int cardsBorderColor;
    private MaterialCardView card;
    private LinearLayout cardLayout;
    private LinearLayout tagLayout;
    private LinearLayout descriptionLayout;
    private ImageButton arrow;
    private LinearLayout extrasLayout;
    private int cardsBackgroungColor;


    public BlackboardCard(Context context, LinearLayout board, int cardsBorderColor) {
        super(context);

        this.context = context;
        this.board = board;
        this.cardsBorderColor = cardsBorderColor;
        this.cardsBackgroungColor=choseCardsColor();


        createBox();
        createColorfulBoxInside();
        addFields();
        addDescriptionLayout();
        addOutsidePadding();
        configurateClickers();

    }

    private void createBox() {
        card = new MaterialCardView(context);
        card.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        card.setStrokeColor(context.getResources().getColor(cardsBorderColor));
        card.setCardElevation(0);
        card.setStrokeWidth(10);
        board.addView(card);
    }

    private void createColorfulBoxInside() {
        LinearLayout paddingRed = new LinearLayout(context);
        paddingRed.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        paddingRed.setPadding(0, 70, 0, 0);
        paddingRed.setBackgroundColor(context.getResources().getColor(cardsBorderColor));
        card.addView(paddingRed);

        cardLayout = new LinearLayout(context);
        cardLayout.setBackgroundColor(context.getResources().getColor(cardsBackgroungColor));
        cardLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(30, 20, 30, 20);
        paddingRed.addView(cardLayout);
    }

    private void addDescriptionLayout() {
        descriptionLayout = new LinearLayout(context);
        descriptionLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        descriptionLayout.setOrientation(LinearLayout.HORIZONTAL);
        descriptionLayout.setVisibility(GONE);
        descriptionLayout.setPadding(10, 0, 10, 0);
        cardLayout.addView(descriptionLayout);


        text = new TextView(context);
        text.setTextSize(15);
        text.setPadding(0, 10, 0, 10);
        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        text.setClickable(true);
        text.setLinksClickable(true);
        text.setAutoLinkMask(Linkify.ALL);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        descriptionLayout.addView(text);
    }
    public void setLink(){
       // descriptionLayout.addView();
    }

    private void addFields() {
        title = new TextView(context);
        title.setTextSize(17);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        title.setPadding(10, 0, 10, 0);
        cardLayout.addView(title);

        extrasLayout = new LinearLayout(context);
        extrasLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        extrasLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardLayout.addView(extrasLayout);

        tagLayout = new LinearLayout(context);
        tagLayout.setLayoutParams(new ViewGroup.LayoutParams(800, ViewGroup.LayoutParams.WRAP_CONTENT));
        tagLayout.setOrientation(LinearLayout.HORIZONTAL);
        extrasLayout.addView(tagLayout);


    }

    private void addOutsidePadding() {
        LinearLayout padding = new LinearLayout(context);
        padding.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        padding.setPadding(0, 60, 0, 0);
        board.addView(padding);
    }

    public void addTags(ArrayList<String> tags) {

        for (String tag : tags) {
            if ((Objects.nonNull(tag)) && (!tag.equals(""))) {
                buildTag(tag, tagLayout, context);
            }
        }
        addArrow();
    }

    private void addArrow() {
        LinearLayout arrowLayoutPadding = new LinearLayout(context);
        arrowLayoutPadding.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        arrowLayoutPadding.setOrientation(LinearLayout.HORIZONTAL);
        extrasLayout.addView(arrowLayoutPadding);

        arrow = new ImageButton(context);
        arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_down));
        arrow.setBackgroundColor(context.getColor(cardsBackgroungColor));
        arrow.setColorFilter(context.getColor(R.color.grey_dark));
        arrowLayoutPadding.addView(arrow);
        arrow.setOnClickListener(new CardExpandClicker());
    }
    private int choseCardsColor(){
        int[] attrs = {R.attr.dashboardCardBackground};
        @SuppressLint("ResourceType") TypedArray ta = context.obtainStyledAttributes(attrs);
        int color = ta.getResourceId(0, android.R.color.black);
        ta.recycle();
        return color;
    }

    public static void buildTag(String tagText, LinearLayout layout, Context context) {
        Chip chip = new Chip(context);
        chip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        chip.setText(tagText);
        for (CardTags cardTag : CardTags.values()) {
            if (cardTag.compare(tagText)) {
                chip.setChipBackgroundColorResource((cardTag.getColor()));
            }
        }
        chip.setTextSize(12);
        chip.setGravity(Gravity.CENTER);
        chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        chip.setRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));
        chip.setClickable(false);
        chip.setFocusable(false);
        layout.addView(chip);

    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void configurateClickers() {
        cardLayout.setOnClickListener(new CardExpandClicker());
        extrasLayout.setOnClickListener(new CardExpandClicker());
    }

    class CardExpandClicker implements View.OnClickListener {
        @Override
        public void onClick(View view) {


            if (descriptionLayout.getVisibility() == View.GONE) {
                expand(descriptionLayout);
            } else {
                collapse(descriptionLayout);
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
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            ? Constraints.LayoutParams.WRAP_CONTENT
                            : (int) (targetHeight * interpolatedTime);
                    v.requestLayout();
                }
            };

            // Expansion speed of 1dp/ms
            a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);
            arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_up));

        }

        public void collapse(final View v) {
            final int initialHeight = v.getMeasuredHeight();

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        v.setVisibility(View.GONE);
                    } else {
                        v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }
            };

            // Collapse speed of 1dp/ms
            a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);
            arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_down));


        }
    }
}


