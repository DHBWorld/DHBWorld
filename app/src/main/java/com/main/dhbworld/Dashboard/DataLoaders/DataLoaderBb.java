package com.main.dhbworld.Dashboard.DataLoaders;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.main.dhbworld.Blackboard.BlackboardCard;
import com.main.dhbworld.R;
import com.main.dhbworld.Utilities.ProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataLoaderBb {
    private final Context context;
    private final LinearLayout layoutAdverTags;
    private final LinearLayout layoutAdvertisement;
    private final LinearLayout layoutCardBb;
    private final TextView textViewAdvertTitle;


    public DataLoaderBb(Context context, LinearLayout layoutAdvertisement, TextView textViewAdvertTitle, LinearLayout layoutAdverTags, LinearLayout layoutCardBb) {
        this.context = context;

        this.layoutAdvertisement = layoutAdvertisement;
        this.textViewAdvertTitle = textViewAdvertTitle;
        this.layoutAdverTags = layoutAdverTags;
        this.layoutCardBb = layoutCardBb;

    }

    public void load(FirebaseFirestore firestore) {
        ProgressIndicator indicator = new ProgressIndicator(context, layoutCardBb);
        indicator.show();
        new Thread(() -> layoutCardBb.post(() -> {
            firestore.collection("Blackboard").orderBy("addedOn", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {

                List<DocumentSnapshot> docs = task.getResult().getDocuments();

                for (int i = 0; i < docs.size(); i++) {
                    DocumentSnapshot doc = docs.get(i);
                    if (Boolean.TRUE.equals(doc.getBoolean("approved"))) {
                        indicator.hide();
                        showLastAdvert(doc);
                        return;
                    }
                }
                showEmptyBoard(indicator);

            }).addOnFailureListener(e -> showEmptyBoard(indicator));
        })).start();
    }

    private void showLastAdvert(DocumentSnapshot doc) {
        layoutAdvertisement.setVisibility(View.VISIBLE);
        layoutAdverTags.removeAllViews();
        textViewAdvertTitle.setText(doc.getString("title"));

        ArrayList<String> tags = new ArrayList<>();
        tags.add(doc.getString("tagOne"));
        tags.add(doc.getString("tagTwo"));
        tags.add(doc.getString("tagThree"));

        for (String tag : tags) {
            if ((Objects.nonNull(tag)) && (!tag.equals(""))) {
                BlackboardCard.buildTag(tag, layoutAdverTags, context);
            }
        }
    }

    private void showEmptyBoard(ProgressIndicator indicator){
        indicator.hide();
        layoutAdvertisement.setVisibility(View.GONE);
        TextView message= new TextView(context);
        message.setText(R.string.empty_blackboard);
        message.setPadding(0,0,0,20);
        layoutAdverTags.addView(message);
    }
}
