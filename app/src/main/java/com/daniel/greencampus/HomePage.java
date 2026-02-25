package com.daniel.greencampus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class HomePage extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private AppCompatImageView menuIcon;

    private TextView todayMinutes, streakCount, averageScore, motivationQuote;
    private Button startSessionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuIcon = findViewById(R.id.menuIcon);

        todayMinutes = findViewById(R.id.todayMinutes);
        streakCount = findViewById(R.id.streakCount);
        averageScore = findViewById(R.id.averageScore);
        motivationQuote = findViewById(R.id.motivationQuote);
        startSessionButton = findViewById(R.id.startSessionButton);

        NavigationView navigationView = findViewById(R.id.navigationView);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                menuIcon.setRotation(slideOffset * 180);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                menuIcon.setImageResource(R.drawable.ic_close);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                menuIcon.setImageResource(R.drawable.ic_menu);
                menuIcon.setRotation(0);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navStudy) {
                startActivity(new Intent(HomePage.this, StartSession.class));

            } else if (id == R.id.navAnalytics) {
                startActivity(new Intent(HomePage.this, Analytics.class));

            } else if (id == R.id.navHistory) {
                startActivity(new Intent(HomePage.this, SessionHistory.class));

            } else if (id == R.id.navInsights) {
                startActivity(new Intent(HomePage.this, Insights.class));

            } else if (id == R.id.navAccount) {
                startActivity(new Intent(HomePage.this, Account.class));
            } else if (id == R.id.navLogout) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Log out?")
                        .setMessage("Are you sure you want to log out?")
                        .setCancelable(true)
                        .setPositiveButton("Log out", (dialog, which) -> {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(HomePage.this, LogIn.class));
                            finish();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        motivationQuote.setText(getRandomQuote());

        startSessionButton.setOnClickListener(v ->
                startActivity(new Intent(HomePage.this, StartSession.class))
        );

        loadDashboardData();
    }

    private void loadDashboardData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("sessions");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long todayMinutesTotal = 0;
                int sessionCount = 0;
                int moodSum = 0;

                Calendar today = Calendar.getInstance();
                int todayDay = today.get(Calendar.DAY_OF_YEAR);
                int todayYear = today.get(Calendar.YEAR);

                for (DataSnapshot s : snapshot.getChildren()) {
                    StudySession session = s.getValue(StudySession.class);
                    if (session == null) continue;

                    sessionCount++;
                    moodSum += session.getMood();

                    Calendar sessionCal = Calendar.getInstance();
                    sessionCal.setTimeInMillis(session.getTimestamp());

                    if (sessionCal.get(Calendar.DAY_OF_YEAR) == todayDay &&
                            sessionCal.get(Calendar.YEAR) == todayYear) {

                        todayMinutesTotal += session.getDuration() / 60;
                    }
                }

                todayMinutes.setText(String.valueOf(todayMinutesTotal));
                streakCount.setText(String.valueOf(sessionCount));

                if (sessionCount > 0) {
                    int avgMood = Math.round((float) moodSum / sessionCount);
                    averageScore.setText(moodToEmoji(avgMood));
                } else {
                    averageScore.setText("—");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePage.this,
                        "Failed to load dashboard data",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getRandomQuote() {
        String[] quotes = {
                "You're doing great — keep going!",
                "Small progress is still progress.",
                "Your future self will thank you.",
                "Stay focused. Stay determined.",
                "Every minute counts.",
                "Believe you can — and you're halfway there."
        };
        return quotes[new Random().nextInt(quotes.length)];
    }

    private String moodToEmoji(int mood) {
        switch (mood) {
            case 1: return "😞";
            case 2: return "😐";
            case 3: return "🙂";
            case 4: return "😁";
            case 5: return "🤩";
            default: return "—";
        }
    }

}
