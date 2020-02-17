package com.example.aquaticanimals.quiz;

public class Quiz {
    private int quizId, numOfQues, timeLimit;
    private String quizName, date, time;

    public Quiz(int quizId, String quizName, String date, String time, int numOfQues, int timeLimit) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.date = date;
        this.time = time;
        this.numOfQues = numOfQues;
        this.timeLimit = timeLimit;
    }

    public int getQuizId() {
        return quizId;
    }

    public String getQuizName() {
        return quizName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getNumOfQues() {
        return numOfQues;
    }

    public int getTimeLimit() {
        return timeLimit;
    }
}
