package com.daniel.greencampus;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;


import java.text.SimpleDateFormat;
import java.util.*;

public class Analytics extends AppCompatActivity {

    private BarChart chart;
    private Button btnMood, btnDuration;
    private TextView detailValue, detailExtra, detailTimestamp;

    private final List<StudySession> sessions = new ArrayList<>();
    private final List<String> xLabels = new ArrayList<>();

    private enum Mode { MOOD, DURATION }
    private Mode currentMode = Mode.MOOD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        chart = findViewById(R.id.barChart);
        btnMood = findViewById(R.id.btnMood);
        btnDuration = findViewById(R.id.btnDuration);
        detailValue = findViewById(R.id.detailValue);
        detailExtra = findViewById(R.id.detailExtra);
        detailTimestamp = findViewById(R.id.detailTimestamp);


        setupChart();
        setupButtons();
        loadSessions();

        findViewById(R.id.backArrow).setOnClickListener(v -> finish());

    }

    private void setupButtons() {
        btnMood.setOnClickListener(v -> {
            currentMode = Mode.MOOD;
            showMoodGraph();
        });

        btnDuration.setOnClickListener(v -> {
            currentMode = Mode.DURATION;
            showDurationGraph();
        });
    }

    private void setupChart() {
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setDrawGridLines(false);

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int i = (int) e.getX();
                if (i >= 0 && i < sessions.size()) {
                    showDetails(sessions.get(i));
                }
            }

            @Override public void onNothingSelected() {}
        });
    }

    private void loadSessions() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users")
                .child(uid).child("sessions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        sessions.clear();
                        for (DataSnapshot s : snap.getChildren()) {
                            StudySession ss = s.getValue(StudySession.class);
                            if (ss != null) sessions.add(ss);
                        }

                        sessions.sort(Comparator.comparingLong(StudySession::getTimestamp));
                        generateXLabels();
                        showMoodGraph();
                    }

                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
    }

    private void generateXLabels() {
        xLabels.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        for (StudySession s : sessions)
            xLabels.add(sdf.format(new Date(s.getTimestamp())));
    }

    private void showMoodGraph() {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < sessions.size(); i++)
            entries.add(new BarEntry(i, sessions.get(i).getMood()));

        render(entries, "Mood", Color.parseColor("#1565C0"));
    }

    private void showDurationGraph() {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < sessions.size(); i++)
            entries.add(new BarEntry(i, sessions.get(i).getDuration() / 60f));

        render(entries, "Minutes", Color.parseColor("#2E7D32"));
    }

    private void render(List<BarEntry> entries, String label, int color) {
        BarDataSet set = new BarDataSet(entries, label);
        set.setColor(color);
        set.setValueTextSize(12f);
        set.setValueFormatter(intFormatter);
        BarData data = new BarData(set);
        chart.setData(data);

        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setGranularity(1f);
        x.setGranularityEnabled(true);
        x.setLabelCount(xLabels.size(), true);
        x.setAxisMinimum(0f);
        x.setAxisMaximum(sessions.size() - 1);
        x.setValueFormatter(new IndexAxisValueFormatter(xLabels));


        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setValueFormatter(intFormatter);
        chart.getAxisRight().setEnabled(false);

        if (currentMode == Mode.MOOD) {
            chart.getAxisLeft().setAxisMinimum(1f);
            chart.getAxisLeft().setAxisMaximum(5f);
        }

        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }



    private void showDetails(StudySession s) {

        String time = new SimpleDateFormat(
                "dd MMM yyyy • HH:mm",
                Locale.getDefault()
        ).format(new Date(s.getTimestamp()));

        if (currentMode == Mode.MOOD) {
            detailValue.setText("Mood: " + moodEmoji(s.getMood()));
            detailExtra.setText("Goal: " + safe(s.getGoal()));
        } else {
            detailValue.setText("Duration: " + (s.getDuration() / 60) + " mins");
            detailExtra.setText("Subject: " + safe(s.getSubject()));
        }

        detailTimestamp.setText("Time: " + time);
    }
    private String safe(String s) {
        return (s == null || s.isEmpty()) ? "—" : s;
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

    private final ValueFormatter intFormatter = new ValueFormatter() {
        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    };

}