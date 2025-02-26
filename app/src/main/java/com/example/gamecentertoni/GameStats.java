package com.example.gamecentertoni;

public class GameStats {

    private int id;
    private int userId;
    private int score;
    private String time;

    public GameStats() {

    }

    public GameStats(int id, int userId, int score, String time) {
        this.id = id;
        this.userId = userId;
        this.score = score;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
