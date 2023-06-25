package com.main.dhbworld.UserInter;

import android.view.View;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Firebase.DataSendListener;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.R;


public class DataSendListenerUserInt implements DataSendListener {
    private final View view;
    private final Utilities firebaseUtilities;

    InteractionState[][] states;

    public DataSendListenerUserInt(Utilities firebaseUtilities, View view, InteractionState[][] states){
        this.firebaseUtilities=firebaseUtilities;
        this.view=view;
        this.states=states;
    }
    @Override
    public void success() {
        Snackbar.make(view.findViewById(android.R.id.content),
                        R.string.thank_you,
                        BaseTransientBottomBar.LENGTH_LONG)
                .show();

        UserIntUtilities.updateInteractionState(firebaseUtilities);
    }

    @Override
    public void failed(Exception reason) {
        if (reason.getMessage() != null) {
            Snackbar.make(view.findViewById(android.R.id.content),
                            reason.getMessage(),
                            BaseTransientBottomBar.LENGTH_LONG)
                    .show();
        }
    }


}
