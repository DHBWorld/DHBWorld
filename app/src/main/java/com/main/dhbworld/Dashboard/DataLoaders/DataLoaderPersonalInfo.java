package com.main.dhbworld.Dashboard.DataLoaders;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.R;

public class DataLoaderPersonalInfo {
    LinearLayout[] layoutInfo;
    ImageButton[] imageButtonCopy;
    TextView[] infoView;
    Context context;
    View snackbarView;

    public DataLoaderPersonalInfo(Context context, LinearLayout[] layoutInfo, ImageButton[] imageButtonCopy, TextView[] infoView, View snackbarView) {
        this.imageButtonCopy=imageButtonCopy;
        this.infoView=infoView;
        this.layoutInfo=layoutInfo;
        this.context=context;
        this.snackbarView=snackbarView;
    }


    public void load(){
        SharedPreferences sp = context.getSharedPreferences(DashboardActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        String[] markerTitle = new String[3];
        markerTitle[0]=context.getString(R.string.matriculationNumber)+"\n";
        markerTitle[1]=context.getString(R.string.libraryNumber)+"\n" ;
        markerTitle[2]=context.getString(R.string.emailStudent)+"\n";
        boolean emptyCard=true;
        String[] info = new String[3];
        info[0]=sp.getString("matriculationNumberKey", "");
        info[1]=sp.getString("libraryNumberKey", "");
        info[2]=sp.getString("studentMailKey", "");

        for (int i=0;i<3;i++){
            layoutInfo[i].setVisibility(View.VISIBLE);
            imageButtonCopy[i].setVisibility(View.VISIBLE);
            if ((!info[i].equals("")) && (!info[i].equals(" "))){
                emptyCard=false;
                infoView[i].setText( markerTitle[i]+info[i]);
                copyClick(imageButtonCopy[i], info[i]);
            }else{
                layoutInfo[i].setVisibility(View.GONE);
            }
        }
        if (emptyCard){
            layoutInfo[0].setVisibility(View.VISIBLE);
            imageButtonCopy[0].setVisibility(View.GONE);
            infoView[0].setText(context.getResources().getString(R.string.thereIsntAnyPersonalData));
        }
    }

    private void copyClick(ImageButton button, String copyText){
        button.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", copyText);
            clipboard.setPrimaryClip(clip);
            Snackbar.make(snackbarView, context.getResources().getString(R.string.copied), BaseTransientBottomBar.LENGTH_SHORT).show();
        });

    }
}
