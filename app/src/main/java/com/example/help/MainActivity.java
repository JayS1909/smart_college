package com.example.help;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button b1;
    EditText et1, et2, et3, et4, et5, et6;
    TextView tv1, tv2,tv3;
    FirebaseDatabase database;
    DatabaseReference reference;
    Spinner divisionSpinner;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = findViewById(R.id.name);
        et2 = findViewById(R.id.ZPRN);
        et3 = findViewById(R.id.email);
        et4 = findViewById(R.id.number);
        et5 = findViewById(R.id.number2);
        et6 = findViewById(R.id.password);
        b1 = findViewById(R.id.button);
        tv1 = findViewById(R.id.heading);
        tv2 = findViewById(R.id.redirect);
        tv3 = findViewById(R.id.teacher);
        divisionSpinner = findViewById(R.id.divisionSpinner);

        ArrayAdapter<CharSequence> divisionAdapter = ArrayAdapter.createFromResource(this,
                R.array.class_options, android.R.layout.simple_spinner_item);
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(divisionAdapter);

        // Firebase initialization
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zprn = et2.getText().toString();
                String name = et1.getText().toString();
                String email = et3.getText().toString();
                String number = et4.getText().toString();
                String number2 = et5.getText().toString();
                String pass = et6.getText().toString();

                String division = divisionSpinner.getSelectedItem().toString();  // Get selected class

                if (!isValidDivision(division)) {
                    et2.setError("Invalid division");
                    return;
                }

                if (!isValidZprn(zprn)) {
                    et2.setError("ZPRN should be 9 characters");
                    return;
                }

                if (!isValidName(name)) {
                    et1.setError("Name should contain only alphabets");
                    return;
                }

                if (!isValidEmail(email)) {
                    et3.setError("Invalid email format");
                    return;
                }

                if (!isValidContactNumber(number)) {
                    et4.setError("Student contact number should be 10 digits only");
                    return;
                }

                if (!isValidContactNumber(number2)) {
                    et5.setError("Parent contact number should be 10 digits only");
                    return;
                }

                if (!isValidPassword(pass)) {
                    et6.setError("Password should be at least 8 characters");
                    return;
                }

                // Check if ZPRN already exists in any class
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(zprn)) {
                            et2.setError("This ZPRN is already registered for another class");
                        } else {
                            // Check if phone number is already taken
                            reference.orderByChild("Student no").equalTo(number).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        et4.setError("Student contact number is already taken");
                                    } else {
                                        // Check if email is already taken
                                        reference.orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    et3.setError("Email is already taken");
                                                } else {
                                                    // Check if student number is equal to parent number
                                                    if (number.equals(number2)) {
                                                        et5.setError("Student number and Parent number should be different");
                                                    } else {
                                                        // All checks passed, proceed with signup
                                                        reference.child(division).child(zprn).child("Name").setValue(name);
                                                        reference.child(division).child(zprn).child("Zprn").setValue(zprn);
                                                        reference.child(division).child(zprn).child("Email").setValue(email);
                                                        reference.child(division).child(zprn).child("Student no").setValue(number);
                                                        reference.child(division).child(zprn).child("Parent no").setValue(number2);
                                                        reference.child(division).child(zprn).child("Password").setValue(pass);
                                                        reference.child(division).child(zprn).child("Class").setValue(division);

                                                        Toast.makeText(MainActivity.this, "SignUp Successful", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e("FirebaseError", "Database operation cancelled: " + databaseError.getMessage());
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("FirebaseError", "Database operation cancelled: " + databaseError.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Database operation cancelled: " + error.getMessage());
                    }
                });
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, login.class));
                finish();
            }
        });

        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TeacherSignupActivity.class));
                finish();
            }
        });
    }


    private boolean isValidDivision(String division) {
        return !TextUtils.isEmpty(division);
    }

    private boolean isValidZprn(String zprn) {
        return zprn.length() == 9;
    }

    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z ]+");
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidContactNumber(String contactNumber) {
        return contactNumber.length() == 10 && contactNumber.matches("\\d+");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }
}
