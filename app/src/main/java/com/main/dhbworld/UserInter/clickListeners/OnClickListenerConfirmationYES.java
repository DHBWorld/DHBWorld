package com.main.dhbworld.UserInter.clickListeners;

import android.view.View;

import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.Fragments.DialogCofirmationUserInteraction;

public class OnClickListenerConfirmationYES extends OnClickListenerConfirmation{
    public OnClickListenerConfirmationYES(Utilities firebaseUtilities, View view, int buttonIndex, InteractionState[][] states, DialogCofirmationUserInteraction confirmation) {
        super(firebaseUtilities, view, buttonIndex, states, confirmation);
    }

    @Override
    public void updateDatabase(){
        switch (buttonIndex) {
            case 0:
                firebaseUtilities.addToDatabase(Utilities.CATEGORY_CAFETERIA, confirmation.getSelectedState().getId());
                break;
            case 1:
                firebaseUtilities.addToDatabase(Utilities.CATEGORY_COFFEE, confirmation.getSelectedState().getId());
                break;
            case 2:
                firebaseUtilities.addToDatabase(Utilities.CATEGORY_PRINTER, InteractionState.DEFECT.getId());
                break;
        }
    }
}
