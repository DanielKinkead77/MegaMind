package com.daniel.greencampus;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.widget.ArrayAdapter;

public class StartSession extends AppCompatActivity {

    private EditText inputSubject, inputGoal;
    private Spinner moodSpinner;
    private Button btnBeginSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_session);

        inputSubject = findViewById(R.id.inputSubject);
        inputGoal = findViewById(R.id.inputGoal);
        moodSpinner = findViewById(R.id.moodSpinner);
        btnBeginSession = findViewById(R.id.btnBeginSession);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"😞 Very bad", "😐 Okay", "🙂 Good", "😁 Great", "🤩 Amazing"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(adapter);

        btnBeginSession.setOnClickListener(v -> startSession());
    }

    private void startSession() {
        String subject = inputSubject.getText().toString().trim();
        String goal = inputGoal.getText().toString().trim();
        int mood = moodSpinner.getSelectedItemPosition() + 1;

        if (subject.isEmpty()) {
            Toast.makeText(this, "Enter a subject", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(this, SessionTimer.class);
        i.putExtra("subject", subject);
        i.putExtra("goal", goal);
        i.putExtra("mood", mood);
        startActivity(i);
        finish();
    }
}
