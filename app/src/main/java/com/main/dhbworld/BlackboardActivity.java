package com.main.dhbworld;

import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.dhbworld.Blackboard.BlackboardCard;
import com.main.dhbworld.Blackboard.NewAdvertisementActivity;
import com.main.dhbworld.Navigation.NavigationUtilities;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;

public class BlackboardActivity extends AppCompatActivity {
    private LinearLayout board;
    private FloatingActionButton addAdvertisementButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackboard);
        NavigationUtilities.setUpNavigation(this, R.id.blackboard);

        setupToolbar();

        board = findViewById(R.id.card_dash_mealPlan_layout);
        loadAdvertisements();
        configurateClickers();





    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.blackboard_information) {
                showInformation();
            }
            return false;
        });
    }

    private void showInformation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.disclaimer)
                .setMessage(R.string.blackboard_disclaimer)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void configurateClickers(){
        addAdvertisementButton = findViewById(R.id.add_advertisement);
        addAdvertisementButton.setOnClickListener(v -> {
            if (isPostingAllowed()) {
                Intent intent = new Intent(BlackboardActivity.this, NewAdvertisementActivity.class);
                startActivity(intent);
            } else {
                Snackbar.make(this.findViewById(android.R.id.content), getResources().getString(R.string.maximum_posts_blackboard), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isPostingAllowed() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        long lastPostDate = sharedPrefs.getLong("blackboard_date_last_post", 0);
        int postCount = sharedPrefs.getInt("blackboard_total_posted", 0);

        if (lastPostDate == 0 || Instant.ofEpochMilli(lastPostDate).atZone(ZoneId.systemDefault()).getDayOfYear() != Instant.now().atZone(ZoneId.systemDefault()).getDayOfYear()) {
            return true;
        } else {
            return postCount < 3;
        }
    }

    private void loadAdvertisements() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Blackboard").orderBy("addedOn").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int nextCardColor = R.color.grey_light;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (Boolean.TRUE.equals(document.getBoolean("approved"))) {
                        BlackboardCard card = new BlackboardCard(BlackboardActivity.this, board, nextCardColor);

                        card.setTitle(document.getString("title"));
                        ArrayList<String> tags = new ArrayList<>();
                        tags.add(document.getString("tagOne"));
                        tags.add(document.getString("tagTwo"));
                        tags.add(document.getString("tagThree"));
                        card.addTags(tags);
                        card.setText(document.getString("text"));
                        nextCardColor = changeColor(nextCardColor);
                    }
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private int changeColor(int color) {
        if (color == R.color.red) {
            return R.color.grey_light;
        } else {
            return R.color.red;
        }
    }
}