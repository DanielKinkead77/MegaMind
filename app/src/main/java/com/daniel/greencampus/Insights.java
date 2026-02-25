package com.daniel.greencampus;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class Insights extends AppCompatActivity {

    private LineChart chart;
    private Button btnMood, btnDuration;
    private TextView infoText;

    private final List<StudySession> sessions = new ArrayList<>();

    private enum Mode {MOOD, DURATION}

    private Mode currentMode = Mode.MOOD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);

        chart = findViewById(R.id.insightsChart);
        btnMood = findViewById(R.id.btnMood);
        btnDuration = findViewById(R.id.btnDuration);
        infoText = findViewById(R.id.infoText);

        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(v -> finish());

        btnMood.setOnClickListener(v -> {
            currentMode = Mode.MOOD;
            showGraph();
        });

        btnDuration.setOnClickListener(v -> {
            currentMode = Mode.DURATION;
            showGraph();
        });

        loadSessions();

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();

                if (index >= 0 && index < sessions.size()) {
                    updateInfoForSession(sessions.get(index));
                }
            }

            @Override
            public void onNothingSelected() {}
        });


    }

    private void loadSessions() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("sessions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        sessions.clear();
                        for (DataSnapshot s : snap.getChildren()) {
                            StudySession ss = s.getValue(StudySession.class);
                            if (ss != null) sessions.add(ss);
                        }

                        Collections.sort(sessions,
                                Comparator.comparingLong(StudySession::getTimestamp));

                        showGraph();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void showGraph() {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < sessions.size(); i++) {
            StudySession s = sessions.get(i);

            if (currentMode == Mode.MOOD) {
                entries.add(new Entry(i, s.getMood()));
            } else {
                entries.add(new Entry(i, s.getDuration() / 60f));
            }
        }

        String label = currentMode == Mode.MOOD ? "Mood" : "Duration (mins)";

        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(Color.parseColor("#1565C0"));
        set.setCircleColor(Color.parseColor("#1565C0"));
        set.setLineWidth(3f);
        set.setCircleRadius(5f);
        set.setValueTextSize(0f);

        LineData data = new LineData(set);
        chart.setData(data);

        Description d = new Description();
        d.setText("");
        chart.setDescription(d);

        chart.invalidate();

        showLatestInfo();
    }

    private void showLatestInfo() {
        if (sessions.isEmpty()) {
            infoText.setText("No sessions recorded");
            return;
        }

        StudySession s = sessions.get(sessions.size() - 1);

        String moodEmoji;
        switch (s.getMood()) {
            case 1:
                moodEmoji = "😞";
                break;
            case 2:
                moodEmoji = "😐";
                break;
            case 3:
                moodEmoji = "🙂";
                break;
            case 4:
                moodEmoji = "😁";
                break;
            case 5:
                moodEmoji = "🤩";
                break;
            default:
                moodEmoji = "—";
        }

        String date = new SimpleDateFormat(
                "dd MMM yyyy • HH:mm",
                Locale.getDefault()
        ).format(new Date(s.getTimestamp()));

        String text =
                "Goal: " + (s.getGoal() == null ? "-" : s.getGoal()) + "\n" +
                        "Mood: " + moodEmoji + "\n" +
                        "Duration: " + (s.getDuration() / 60) + " mins\n" +
                        "Date: " + date;

        infoText.setText(text);
    }

    private void updateInfoForSession(StudySession s) {

        String date = new SimpleDateFormat(
                "dd MMM yyyy • HH:mm",
                Locale.getDefault()
        ).format(new Date(s.getTimestamp()));

        infoText.setText("Subject: " + s.getSubject() +"\nGoal: " + s.getGoal() +"\nMood: " + moodEmoji(s.getMood()) +"\nDuration: " + (s.getDuration() / 60) + " mins" +"\n" + date);
    }

    private String moodEmoji(int mood) {
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
