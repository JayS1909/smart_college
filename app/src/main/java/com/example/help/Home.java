package com.example.help;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class Home extends AppCompatActivity {
    TextView welcomeMessageTextView, userInfoTextView;
    FirebaseDatabase database;
    DatabaseReference reference, myRef;
    FirebaseAuth mAuth;

    ImageButton IB1, IB2, scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("scanned_data");

        welcomeMessageTextView = findViewById(R.id.welmsg);
        userInfoTextView = findViewById(R.id.userInfo);

        IB1 = findViewById(R.id.i1);
        IB2 = findViewById(R.id.i3);
        scan = findViewById(R.id.i2);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");
        myRef = database.getReference("scanned_data");

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("Zprn")) {
            String zprn = extras.getString("Zprn");
            loadUserInfo(zprn);
        }

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator i1 = new IntentIntegrator(Home.this);
                i1.setPrompt("Scan a QR Code");
                i1.setOrientationLocked(false);
                i1.initiateScan();
            }
        });

        // Add a click listener to IB1 to start UploadResultActivity
        IB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, UploadResultActivity.class));
            }
        });
    }

    private void loadUserInfo(String zprn) {
        reference.child(zprn).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String email = dataSnapshot.child("Email").getValue(String.class);

                    String welcomeMessage = "Welcome, " + name + "!";
                    welcomeMessageTextView.setText(welcomeMessage);

                    String userInfo = "Name: " + name + "\nZPRN: " + zprn + "\nEmail: " + email;
                    userInfoTextView.setText(userInfo);
                } else {
                    String notfound = "user not found " + "!";
                    welcomeMessageTextView.setText(notfound);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult ir1 = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (ir1.getContents() != null) {
            // Retrieve user's ZPRN from Intent
            String zprn = getIntent().getStringExtra("Zprn");

            // Store scanned data in the user's Attendance database under the specific date and scanned content key
            DatabaseReference userAttendanceRef = database.getReference("users").child(zprn).child("Attendance");

            // Get current date
            String currentDate = getCurrentDate();

            // Create a new key with the scanned content using push
            DatabaseReference newAttendanceRef = userAttendanceRef.child(currentDate).child(ir1.getContents());

            // Get current time
            String currentTime = getCurrentTime();

            // Store time inside the key
            newAttendanceRef.setValue(currentTime);

            Toast.makeText(this, "Attendance has been marked successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Scan again", Toast.LENGTH_LONG).show();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Helper method to get current time
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
