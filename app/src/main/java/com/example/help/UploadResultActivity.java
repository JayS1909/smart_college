package com.example.help;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class UploadResultActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private Spinner spinnerYear, spinnerSemester;
    private RadioGroup radioGroupStudentType;
    private Button btnUploadResult, btnChooseFile;
    private EditText editTextMarks;

    // Declare pdfUri as a class-level variable
    private Uri pdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_result);

        // Check and request permissions at runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }

        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerSemester = findViewById(R.id.spinnerSemester);
        radioGroupStudentType = findViewById(R.id.radioGroupStudentType);
        btnUploadResult = findViewById(R.id.btnUploadResult);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        editTextMarks = findViewById(R.id.editTextMarks);

        // Set up spinners
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(
                this, R.array.year_array, android.R.layout.simple_spinner_item
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(
                this, R.array.semester_array, android.R.layout.simple_spinner_item
        );
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        // Set up click listener for the upload button
        btnUploadResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadResult();
            }
        });

        // Set up click listener for the "Choose PDF File" button
        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });
    }

    private void uploadResult() {
        // Get selected options
        String selectedYear = spinnerYear.getSelectedItem().toString();
        String selectedSemester = spinnerSemester.getSelectedItem().toString();
        String selectedStudentType = getSelectedStudentType();
        String marks = editTextMarks.getText().toString();

        // Validate and process the data as needed

        // Construct the database reference path
        String databasePath = "Result/" + selectedYear + "/" + selectedSemester + "/" + selectedStudentType;

        // Get a reference to the selected path
        DatabaseReference resultRef = FirebaseDatabase.getInstance().getReference(databasePath);

        // Generate a unique key for this result entry
        String resultKey = resultRef.push().getKey();

        // Upload PDF file to Firebase Storage
        if (pdfUri != null) {
            // Replace "your_document.pdf" with the actual file name
            String documentFileName = "your_document.pdf";
            uploadDocument(resultKey, documentFileName, pdfUri);
        }

        // Add data to the database
        resultRef.child(resultKey).child("Marks").setValue(marks);
        // Add other data as needed

        // Show a toast message indicating success
        Toast.makeText(this, "Result uploaded successfully!", Toast.LENGTH_SHORT).show();
    }

    private void uploadDocument(String resultKey, String documentFileName, Uri pdfUri) {
        // Get the Firebase Storage reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to the location where you want to store the PDF
        StorageReference pdfRef = storageRef.child("ResultDocuments/" + documentFileName);

        // Upload the PDF file to Firebase Storage
        pdfRef.putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully, get the download URL
                    pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Now, you can save the download URL to the Firebase Realtime Database
                        DatabaseReference resultRef = FirebaseDatabase.getInstance().getReference("Result");
                        resultRef.child(resultKey).child("DocumentURL").setValue(uri.toString());

                        // Show a toast message indicating success
                        Toast.makeText(UploadResultActivity.this, "PDF uploaded successfully!", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(UploadResultActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf"); // Set the MIME type to PDF files
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "Choose PDF File"), PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Handle the selected PDF file
            pdfUri = data.getData();
            // Add your logic to use the selected PDF file, such as storing its reference or uploading to Firebase
            Toast.makeText(this, "PDF File selected: " + Objects.requireNonNull(pdfUri).getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedStudentType() {
        int selectedRadioButtonId = radioGroupStudentType.getCheckedRadioButtonId();

        if (selectedRadioButtonId == R.id.radioButtonRegular) {
            return "Regular";
        } else if (selectedRadioButtonId == R.id.radioButtonDirectSecondYear) {
            return "Direct Second Year";
        } else {
            return ""; // Handle other cases as needed
        }
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now proceed with your actions
            } else {
                // Permission denied,
                // Permission denied, handle accordingly (e.g., show a message, disable functionality)
                Toast.makeText(this, "Permission denied. App cannot access external storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

