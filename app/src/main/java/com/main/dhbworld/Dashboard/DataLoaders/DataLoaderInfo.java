package com.main.dhbworld.Dashboard.DataLoaders;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.dhbworld.Utilities.ProgressIndicator;

public class DataLoaderInfo {
    private final Context context;
    private final TextView title;
    private final TextView[] message;
    private final LinearLayout[] layoutInfoFull;
    private final LinearLayout[] layoutInfo;
    private final LinearLayout layoutCardInfo;


    public DataLoaderInfo(Context context, TextView title, TextView[] message, LinearLayout[] layoutInfoFull, LinearLayout[] layoutInfo, LinearLayout layoutCardInfo) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.layoutInfoFull = layoutInfoFull;
        this.layoutInfo = layoutInfo;
        this.layoutCardInfo = layoutCardInfo;
    }

    public void load(FirebaseFirestore firestore) {

        ProgressIndicator indicator = new ProgressIndicator(context, layoutCardInfo, layoutInfoFull);
        indicator.show();
        new Thread(() -> layoutCardInfo.post(() -> {
            DocumentReference contact = firestore.collection("General").document("InfoCard");
            contact.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    indicator.hide();
                    title.setText(doc.getString("Title"));
                    String m = doc.getString("Message");
                    assert m != null;
                    String[] parts = m.split("#");
                    for (int i = 0; i < 3; i++) {
                        if (parts.length > i) {
                            message[i].setText(parts[i]);
                        } else {
                            layoutInfo[i].setVisibility(View.GONE);
                        }
                    }
                    if (m.length() == 0) {
                        layoutInfo[0].setVisibility(View.GONE);
                    }

                }
            });
        })).start();

    }
}
