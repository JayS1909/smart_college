package com.example.help;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class TeacherLoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        EditText teacherEmailEditText = findViewById(R.id.teacher_login_email);
        EditText teacherPasswordEditText = findViewById(R.id.teacher_login_password);
        Button teacherLoginButton = findViewById(R.id.teacher_login_button);

        teacherLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered values
                String teacherEmail = teacherEmailEditText.getText().toString().trim();
                String teacherPassword = teacherPasswordEditText.getText().toString();

                // Validate input data
                if (TextUtils.isEmpty(teacherEmail) || TextUtils.isEmpty(teacherPassword)) {
                    Toast.makeText(TeacherLoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Authenticate teacher using Firebase Authentication
                mAuth.signInWithEmailAndPassword(teacherEmail, teacherPassword)
                        .addOnCompleteListener(TeacherLoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // If login is successful, redirect to TeacherHomeActivity or another desired activity
                                Intent intent = new Intent(TeacherLoginActivity.this, Home.class);
                                startActivity(intent);
                                finish();
                            } else {

                                // If login fails, display a message to the user.
                                Toast.makeText(TeacherLoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
