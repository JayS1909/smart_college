package com.example.help;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    Button b1;
    EditText e1, e2;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        e1 = findViewById(R.id.ZPRN);
        e2 = findViewById(R.id.password);
        b1 = findViewById(R.id.button);
        t = findViewById(R.id.tv);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateZPRN() || !validatePassword()) {
                    // Validation failed
                } else {
                    checkLog();
                }
            }
        });

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, MainActivity.class));
            }
        });
    }

    public Boolean validateZPRN() {
        String val = e1.getText().toString();
        if (val.isEmpty()) {
            e1.setError("Please Enter ZPRN");
            return false;
        } else {
            e1.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = e2.getText().toString();
        if (val.isEmpty()) {
            e2.setError("Please enter password");
            return false;
        } else {
            e2.setError(null);
            return true;
        }
    }

    public void checkLog() {
        String zprn = e1.getText().toString().trim();
        String pass = e2.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("Zprn").equalTo(zprn);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    e1.setError(null);
                    String passwordFromDB = snapshot.child(zprn).child("Password").getValue(String.class);
                    if (passwordFromDB != null && passwordFromDB.equals(pass)) {
                        e2.setError(null);

                        String zprnFromDB = snapshot.child(zprn).child("Zprn").getValue(String.class);
                        Intent intent = new Intent(login.this,Home.class);
                        intent.putExtra("Zprn", zprnFromDB);
                        intent.putExtra("Password", passwordFromDB);
                        startActivity(intent);
                    } else {
                        showError("Invalid Credentials");
                    }
                } else {
                    showError("User does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showError("Database Error");
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(login.this, message, Toast.LENGTH_SHORT).show();
    }
}
