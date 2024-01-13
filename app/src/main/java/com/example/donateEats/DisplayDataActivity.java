package com.example.donateEats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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
                                String type = document.getString("user type");
                                String description = document.getString("description");
                                String userid = document.getString("userid");

                                // Create a TextView for each entry
                                TextView entryTextView = new TextView(DisplayDataActivity.this);
                                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                textParams.setMargins(0, 0, 0, 20);
                                entryTextView.setLayoutParams(textParams);
                                entryTextView.setText("Name: " + name + "\nUser Type: " + type + "\nDescription: " + description);
                                entryTextView.setTextSize(18);
                                showDataLayout.addView(entryTextView);
                            }
                        } else {
                            Log.d(TAG, "Error fetching data: ", task.getException());
                        }
                    }
                });
    }
}
