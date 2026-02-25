package com.daniel.greencampus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Account extends AppCompatActivity {

    private TextView textEmail, textSessionCount;
    private Button btnBack, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        textEmail = findViewById(R.id.textEmail);
        textSessionCount = findViewById(R.id.textSessionCount);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDeleteAccount);

        btnBack.setOnClickListener(v -> finish());

        loadAccountInfo();

        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadAccountInfo() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        textEmail.setText(email == null ? "Guest" : email);

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("sessions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        textSessionCount.setText(String.valueOf(snap.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("This will permanently delete your account and all study data.")
                .setPositiveButton("Delete", (d, w) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .removeValue()
                .addOnCompleteListener(task -> FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .delete()
                        .addOnCompleteListener(t -> {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(this, LogIn.class));
                            finishAffinity();
                        }));
    }
}
