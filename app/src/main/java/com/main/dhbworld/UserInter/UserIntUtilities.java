package com.main.dhbworld.UserInter;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.R;
import com.main.dhbworld.UserInteraction;


public class UserIntUtilities {


    public static void changeStatus(InteractionState state, TextView stateTV, LinearLayout imageBox, int status, Context context){
        state = InteractionState.parseId(status);
        stateTV.setText(context.getResources().getString(R.string.state, state.getText(context)));
        imageBox.setBackgroundColor(context.getResources().getColor(state.getColor()));
    }
}
