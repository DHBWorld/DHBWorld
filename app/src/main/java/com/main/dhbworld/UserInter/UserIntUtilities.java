package com.main.dhbworld.UserInter;

import android.content.Context;
import android.content.res.Resources;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.R;


public class UserIntUtilities {


    public static void changeStatus(InteractionState state, TextView stateTV, LinearLayout imageBox, int status, Context context){
        state = InteractionState.parseId(status);
        stateTV.setText(context.getResources().getString(R.string.state, state.getText(context)));
        imageBox.setBackgroundColor(context.getResources().getColor(state.getColor()));
    }

    public static  void  setPriviousNotofications(Long notification, TextView notificationTV, long reportCount, Context context){
        notification = reportCount;
        notificationTV.setText(context.getResources().getString(R.string.previous_notifications, String.valueOf(notification)));
    }

    public static void setupCards(TextView stateTV, InteractionState state, LinearLayout imageBox, Context context){
        Resources res = context.getResources();
        stateTV.setText(res.getString(R.string.state, state.getText(context)));
        imageBox.setBackgroundColor(res.getColor(state.getColor()));

    }

    public static int setupNotifications(TextView notificationTV,  Context context){
        notificationTV.setText(context.getResources().getString(R.string.previous_notifications, String.valueOf(0)));
        return 0;
    }
}
