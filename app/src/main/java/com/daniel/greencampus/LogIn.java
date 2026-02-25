package com.daniel.greencampus;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Random;

import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView txtGuest, txtCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LogIn.this, HomePage.class));
            finish();
            return;
        }

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtGuest = findViewById(R.id.txtGuest);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);

        btnLogin.setOnClickListener(v -> loginUser());
        txtCreateAccount.setOnClickListener(v ->
                startActivity(new Intent(LogIn.this, RegisterAccount.class))
        );
        txtGuest.setOnClickListener(view -> showGuestAlert());
    }

    private void loginUser() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LogIn.this, HomePage.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnLogin.setEnabled(true);
                });
    }

    private void loginAsGuest() {
        auth.signInAnonymously()
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Continuing as Guest", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LogIn.this, HomePage.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void showGuestAlert() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Guest Mode")
                .setMessage("Your data will not be saved")
                .setPositiveButton("Continue", (dialog, which) -> loginAsGuest())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
