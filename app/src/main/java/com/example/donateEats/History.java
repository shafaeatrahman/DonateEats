package com.example.donateEats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class History extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookref = db.collection("user data");
    public static final String TAG = "TAG";
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        fAuth = FirebaseAuth.getInstance();

        loadNotes();
    }

    public void loadNotes() {
        notebookref.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinearLayout showDataLayout = findViewById(R.id.showdata);
                            showDataLayout.removeAllViews(); // Clear existing views

                            String userID = fAuth.getCurrentUser().getUid(); // Get current user's ID

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Retrieve data from Firestore
                                String name = document.getString("name");
                                String type = document.getString("user type");
                                String description = document.getString("description");
                                String userid = document.getString("userid");
                                Timestamp ts = document.getTimestamp("timestamp");
                                String dateandtime = ts.toDate().toString();

                                if (userid.equals(userID)) {
                                    // Create a new CardView for each entry
                                    androidx.cardview.widget.CardView entryCardView = new androidx.cardview.widget.CardView(History.this);
                                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    cardParams.setMargins(10, 10, 10, 0);
                                    entryCardView.setLayoutParams(cardParams);
                                    entryCardView.setCardBackgroundColor(getResources().getColor(R.color.gray));
                                    entryCardView.setRadius(12);
                                    entryCardView.setCardElevation(5);

                                    // Create a TextView to display the data
                                    TextView entryTextView = new TextView(History.this);
                                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    textParams.setMargins(10, 10, 10, 10);
                                    entryTextView.setLayoutParams(textParams);
                                    entryTextView.setText("Name: " + name + "\nUser Type: " + type + "\nDescription: " + description + "\nDate & Time: " + dateandtime);
                                    entryTextView.setTextColor(getResources().getColor(R.color.white));
                                    entryTextView.setTextSize(15);
                                    entryCardView.addView(entryTextView);

                                    // Create a Delete Button for each entry
                                    Button deleteButton = new Button(History.this);
                                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    buttonParams.setMargins(10, 2, 10, 20);
                                    deleteButton.setLayoutParams(buttonParams);
                                    deleteButton.setText("Delete");
                                    deleteButton.setBackgroundColor(getResources().getColor(R.color.red));
                                    deleteButton.setTextColor(getResources().getColor(R.color.white));
                                    deleteButton.setTextSize(18);

                                    final String documentId = document.getId(); // Get the document ID for deletion

                                    deleteButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // Delete the document from Firestore
                                            notebookref.document(documentId)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Document successfully deleted
                                                            // Refresh the UI after deletion
                                                            loadNotes();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Handle errors
                                                        }
                                                    });
                                        }
                                    });

                                    entryCardView.addView(deleteButton);
                                    showDataLayout.addView(entryCardView);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error fetching data: ", task.getException());
                        }
                    }
                });
    }
}
