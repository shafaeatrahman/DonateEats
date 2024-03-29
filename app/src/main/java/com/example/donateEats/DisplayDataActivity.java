package com.example.donateEats;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DisplayDataActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("user data");
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        loadData();

        // Set up the refresh button click listener
        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });
    }

    private void loadData() {
        notebookRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinearLayout showDataLayout = findViewById(R.id.showDataLayout);
                            showDataLayout.removeAllViews(); // Clear existing views

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Retrieve data from Firestore
                                String name = document.getString("name");
                                String type = document.getString("phone");
                                String description = document.getString("description");
                                Long interestedCount = document.getLong("interestedCount");

                                // Create a CardView for each entry
                                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                cardParams.setMargins(0, 0, 0, 16);

                                CardView entryCardView = new CardView(DisplayDataActivity.this);
                                entryCardView.setLayoutParams(cardParams);
                                entryCardView.setCardBackgroundColor(getResources().getColor(R.color.gray));
                                entryCardView.setRadius(12);
                                entryCardView.setCardElevation(5);

                                // Create a RelativeLayout to hold the TextView and Interested Button
                                RelativeLayout cardContainer = new RelativeLayout(DisplayDataActivity.this);
                                entryCardView.addView(cardContainer);

                                // Create a TextView to display the data
                                TextView entryTextView = new TextView(DisplayDataActivity.this);
                                RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                );
                                textParams.setMargins(16, 16, 16, 16);
                                entryTextView.setLayoutParams(textParams);
                                entryTextView.setText("Name: " + name + "\nPhone: " + type + "\nDescription: " + description + "\nInterested Count: " + interestedCount);
                                entryTextView.setTextSize(18);
                                cardContainer.addView(entryTextView);

                                // Create an Interested Button for each entry
                                Button interestedButton = new Button(DisplayDataActivity.this);
                                RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                );
                                buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); // Align at the bottom
                                buttonParams.addRule(RelativeLayout.ALIGN_PARENT_END); // Align at the end (right)
                                buttonParams.setMargins(0, 0, 16, 16); // Adjust margins as needed
                                interestedButton.setLayoutParams(buttonParams);

                                // Set text and click listener for Interested Button
                                interestedButton.setText("Interested");
                                interestedButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Implement your logic when the Interested Button is clicked
                                        Log.d(TAG, "Interested button clicked for " + name);
                                        // Update interestedCount in Firestore
                                        updateInterestedCount(document.getId());
                                    }
                                });

                                cardContainer.addView(interestedButton);
                                showDataLayout.addView(entryCardView);
                            }
                        } else {
                            Log.d(TAG, "Error fetching data: ", task.getException());
                        }
                    }
                });
    }

    private void updateInterestedCount(String documentId) {
        notebookRef.document(documentId)
                .update("interestedCount", FieldValue.increment(1))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Interested count updated successfully!");
                            // Refresh the UI or perform any other necessary actions
                        } else {
                            Log.e(TAG, "Error updating interested count", task.getException());
                        }
                    }
                });
    }
}
