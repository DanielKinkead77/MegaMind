package com.daniel.greencampus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SessionHistory extends AppCompatActivity {

    private RecyclerView sessionRecyclerView;
    private SessionHistoryAdapter adapter;
    private List<StudySession> sessions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_history);

        sessionRecyclerView = findViewById(R.id.sessionRecyclerView);
        sessionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SessionHistoryAdapter(sessions);
        sessionRecyclerView.setAdapter(adapter);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());

        loadSessions();
    }

    private void loadSessions() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance()
                .getReference("users").child(uid).child("sessions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        sessions.clear();
                        for (DataSnapshot s : snapshot.getChildren()) {
                            StudySession ss = s.getValue(StudySession.class);
                            if (ss != null) sessions.add(ss);
                        }
                        Log.d("SESSION_HISTORY", "Sessions loaded: " + sessions.size());

                        Collections.sort(sessions, Comparator.comparingLong(StudySession::getTimestamp).reversed());

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}