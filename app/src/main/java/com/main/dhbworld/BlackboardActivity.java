package com.main.dhbworld;

import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.dhbworld.Blackboard.BlackboardCard;
import com.main.dhbworld.Blackboard.NewAdvertisementActivity;
import com.main.dhbworld.Navigation.NavigationUtilities;

import java.util.ArrayList;

public class BlackboardActivity extends AppCompatActivity {
    private LinearLayout board;
    private FloatingActionButton addAdvertisementButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackboard);
        NavigationUtilities.setUpNavigation(this, R.id.blackboard);

        board = findViewById(R.id.card_dash_mealPlan_layout);
        loadAdvertisements();
        configurateClickers();





    }
    private void configurateClickers(){
        addAdvertisementButton = findViewById(R.id.add_advertisement);
        addAdvertisementButton.setOnClickListener(v -> {
            Intent intent = new Intent(BlackboardActivity.this, NewAdvertisementActivity.class);
            startActivity(intent);
        });
    }

    private void loadAdvertisements() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Blackboard").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int nextCardColor = R.color.grey_light;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (Boolean.TRUE.equals(document.getBoolean("approved"))) {
                        BlackboardCard card = new BlackboardCard(BlackboardActivity.this, board, nextCardColor);

                        card.setTitle(document.getString("title"));
                        ArrayList<String> tags = new ArrayList<>();
                        tags.add(document.getString("tagOne"));
                        tags.add(document.getString("tagTwo"));
                        card.setTags(tags);
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