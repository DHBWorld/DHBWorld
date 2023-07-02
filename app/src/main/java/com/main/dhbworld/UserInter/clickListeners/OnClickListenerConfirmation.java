package com.main.dhbworld.UserInter.clickListeners;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.Fragments.DialogCofirmationUserInteraction;
import com.main.dhbworld.UserInter.DataSendListenerUserInt;

import java.util.List;

public class OnClickListenerConfirmation implements DialogInterface.OnClickListener {

      View view;
      Utilities firebaseUtilities;
     int buttonIndex;
      InteractionState[][] states;
      DialogCofirmationUserInteraction confirmation;

    public  OnClickListenerConfirmation(Utilities firebaseUtilities, View view,int buttonIndex , InteractionState[][] states, DialogCofirmationUserInteraction confirmation){
        this.firebaseUtilities=firebaseUtilities;
        this.view=view;
        this.buttonIndex=buttonIndex;
        this.states=states;
        this.confirmation=confirmation;

    }



    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        firebaseUtilities.setDataSendListener(new DataSendListenerUserInt(firebaseUtilities, view.findViewById(android.R.id.content),states));
        updateDatabase();

    }

    public void updateDatabase(){


    }

}


