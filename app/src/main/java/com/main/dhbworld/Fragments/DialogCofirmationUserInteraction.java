package com.main.dhbworld.Fragments;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.R;


public class DialogCofirmationUserInteraction extends MaterialAlertDialogBuilder {

    private String nameOfSelectedState;
    private InteractionState selectedState;


    public DialogCofirmationUserInteraction(@NonNull Context context, int message, int buttonText) {
        super(context);
        this.setTitle(R.string.are_you_sure);
        this.setMessage(message);


        this.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //TODO can be removed
        this.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast= Toast.makeText(context, R.string.thank_you, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

            }
        });


    }

    public DialogCofirmationUserInteraction(@NonNull Context context, InteractionState[] states, int buttonText) {
        super(context);
        this.setTitle(R.string.are_you_sure);

        nameOfSelectedState = states[0].getText();

        selectedState = states[0];

        String[] stateNames = new String[states.length];
        for (int i=0; i<states.length; i++) {
            stateNames[i] = states[i].getText();
        }

        this.setSingleChoiceItems(
                stateNames,
                0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        nameOfSelectedState=states[item].getText();
                        selectedState = states[item];

                    }
                }
        );

        this.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //TODO can be removed
        this.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast= Toast.makeText(context, R.string.thank_you, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

            }
        });


    }

    public String getNameOfSelectedState() {
        return nameOfSelectedState;
    }

    public InteractionState getSelectedState() {
        return selectedState;
    }
}
