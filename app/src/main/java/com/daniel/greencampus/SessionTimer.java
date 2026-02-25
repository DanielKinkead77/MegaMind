package com.daniel.greencampus;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.*;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class SessionTimer extends AppCompatActivity {

    private TextView textSubject, textGoal, textTimer;
    private Button btnPauseResume, btnFinish;

    private boolean paused = false;
    private long seconds = 0;
    private Handler handler = new Handler();

    private String subject, goal;
    private int mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_timer);

        textSubject = findViewById(R.id.textSubject);
        textGoal = findViewById(R.id.textGoal);
        textTimer = findViewById(R.id.textTimer);
        btnPauseResume = findViewById(R.id.btnPauseResume);
        btnFinish = findViewById(R.id.btnFinish);

        subject = getIntent().getStringExtra("subject");
        goal = getIntent().getStringExtra("goal");
        mood = getIntent().getIntExtra("mood", 3);

        textSubject.setText(subject);
        textGoal.setText(goal);

        handler.post(timerRunnable);

        btnPauseResume.setOnClickListener(v -> {
            paused = !paused;
            btnPauseResume.setText(paused ? "Resume" : "Pause");
        });

        btnFinish.setOnClickListener(v -> saveSession());
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!paused) {
                seconds++;
                long h = seconds / 3600;
                long m = (seconds % 3600) / 60;
                long s = seconds % 60;
                textTimer.setText(String.format("%02d:%02d:%02d", h, m, s));
            }
            handler.postDelayed(this, 1000);
        }
    };

    private void saveSession() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("subject", subject);
        data.put("goal", goal);
        data.put("mood", mood);
        data.put("duration", seconds);
        data.put("timestamp", System.currentTimeMillis());

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("sessions")
                .push()
                .setValue(data)
                .addOnSuccessListener(v -> {
                    startActivity(new Intent(this, HomePage.class));
                    finish();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
    }
}
