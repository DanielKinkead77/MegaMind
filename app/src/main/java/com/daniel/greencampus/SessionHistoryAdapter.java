package com.daniel.greencampus;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.*;

public class SessionHistoryAdapter extends RecyclerView.Adapter<SessionHistoryAdapter.Holder> {

    private final List<StudySession> sessions;

    public SessionHistoryAdapter(List<StudySession> sessions) {
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new Holder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_session, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int i) {
        StudySession s = sessions.get(i);

        h.subject.setText(
            s.getSubject() == null || s.getSubject().isEmpty()
                ? "General"
                : s.getSubject()
        );

        h.goal.setText(
            s.getGoal() == null || s.getGoal().isEmpty()
                ? ""
                : "Goal: " + s.getGoal()
        );

        String moodEmoji;
        switch (s.getMood()) {
            case 1: moodEmoji = "😞"; break;
            case 2: moodEmoji = "😐"; break;
            case 3: moodEmoji = "🙂"; break;
            case 4: moodEmoji = "😁"; break;
            case 5: moodEmoji = "🤩"; break;
            default: moodEmoji = "—";
        }

        String time = "—";
        if (s.getTimestamp() > 0) {
            time = new SimpleDateFormat(
                "dd MMM • HH:mm",
                Locale.getDefault()
            ).format(new Date(s.getTimestamp()));
        }

        h.meta.setText(
            "Mood: " + moodEmoji +" • " + (s.getDuration() / 60) + " min" +" • " + time
        );
    }


    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView subject, goal, meta;

        Holder(View v) {
            super(v);
            subject = v.findViewById(R.id.textSubject);
            goal = v.findViewById(R.id.textGoal);
            meta = v.findViewById(R.id.textMoodTime);
        }
    }
}
