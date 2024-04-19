package com.example.help;// TeacherSignupActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherSignupActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference teacherReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_signup);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        teacherReference = database.getReference("teachers");

        EditText teacherNameEditText = findViewById(R.id.name);
        EditText teacherPhoneEditText = findViewById(R.id.phone);
        EditText teacherEmailEditText = findViewById(R.id.email);
        EditText teacherPasswordEditText = findViewById(R.id.password);
        TextView tv2 = findViewById(R.id.redirect);


        Button teacherSignupButton = findViewById(R.id.signup);

        teacherSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered values
                String teacherName = teacherNameEditText.getText().toString().trim();
                String teacherPhone = teacherPhoneEditText.getText().toString().trim();
                String teacherEmail = teacherEmailEditText.getText().toString().trim();
                String teacherPassword = teacherPasswordEditText.getText().toString();

                // Validate input data
                if (TextUtils.isEmpty(teacherName) || TextUtils.isEmpty(teacherPhone) ||
                        TextUtils.isEmpty(teacherEmail) || TextUtils.isEmpty(teacherPassword)) {
                    Toast.makeText(TeacherSignupActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(teacherEmail).matches()) {
                    Toast.makeText(TeacherSignupActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (teacherPassword.length() < 8) {
                    Toast.makeText(TeacherSignupActivity.this, "Password should be at least 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (teacherPhone.length() != 10 || !TextUtils.isDigitsOnly(teacherPhone)) {
                    Toast.makeText(TeacherSignupActivity.this, "Phone number should be 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the phone number is already used by another teacher
                teacherReference.orderByChild("Phone").equalTo(teacherPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Phone number is already taken
                            Toast.makeText(TeacherSignupActivity.this, "Phone number is already taken", Toast.LENGTH_SHORT).show();
                        } else {
                            // Create a unique key for the teacher using their name
                            String teacherKey = teacherName.replace(" ", "_");

                            // Create a reference for the specific teacher
                            DatabaseReference currentTeacherReference = teacherReference.child(teacherKey);

                            // Store teacher data in the database
                            currentTeacherReference.child("Name").setValue(teacherName);
                            currentTeacherReference.child("Phone").setValue(teacherPhone);
                            currentTeacherReference.child("Email").setValue(teacherEmail);
                            currentTeacherReference.child("Password").setValue(teacherPassword);

                            Toast.makeText(TeacherSignupActivity.this, "Teacher SignUp Successful", Toast.LENGTH_LONG).show();

                            // You can add more fields or modify the database structure as needed
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Toast.makeText(TeacherSignupActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherSignupActivity.this, TeacherLoginActivity.class));
                finish();
            }
        });
    }
}
