package com.main.dhbworld.Fragments;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.main.dhbworld.R;


public class DialogCofirmationUserInteraction extends MaterialAlertDialogBuilder {


    public DialogCofirmationUserInteraction(@NonNull Context context, int message, int buttonText) {
        super(context);
        this.setTitle(R.string.sind_sie_sicher);
        this.setMessage(message);

        this.setNegativeButton(R.string.abbrechen, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        this.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast= Toast.makeText(context, R.string.danke_fuer_event, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

            }
        });
    }

  }
