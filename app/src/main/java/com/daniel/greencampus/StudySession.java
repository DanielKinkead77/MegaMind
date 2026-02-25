package com.daniel.greencampus;

public class StudySession {

    private int mood;
    private long duration;
    private long timestamp;
    private String subject;
    private String goal;

    public StudySession() {}

    public StudySession(int mood, long duration, long timestamp, String subject, String goal) {
        this.mood = mood;
        this.duration = duration;
        this.timestamp = timestamp;
        this.subject = subject;
        this.goal = goal;
    }

    public int getMood() { return mood; }
    public void setMood(int mood) { this.mood = mood; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
}
