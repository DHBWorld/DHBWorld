package com.main.dhbworld.UserInter.clickListeners;

import android.view.View;

import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.Fragments.DialogCofirmationUserInteraction;

public class OnClickListenerConfirmationNO extends OnClickListenerConfirmation{
    public OnClickListenerConfirmationNO(Utilities firebaseUtilities, View view, int buttonIndex, InteractionState[][] states, DialogCofirmationUserInteraction confirmation) {
        super(firebaseUtilities, view, buttonIndex, states, confirmation);
    }
    @Override
    public void updateDatabase(){

        switch (this.buttonIndex) {
            case 0:
                firebaseUtilities.addToDatabase(Utilities.CATEGORY_CAFETERIA, 3);
                break;
            case 1:
                firebaseUtilities.addToDatabase(Utilities.CATEGORY_COFFEE, 0);
                break;
            case 2:
                firebaseUtilities.addToDatabase(Utilities.CATEGORY_PRINTER, 0);
                break;
        }


    }
}
