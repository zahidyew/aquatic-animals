package com.example.aquaticanimals.questions;

public class Question {
    private int questionId, quizId;
    private String ques, choiceA, choiceB, choiceC, choiceD, answer;

    public Question(int questionId, String ques, String choiceA, String choiceB, String choiceC, String choiceD, String answer, int quizId) {
        this.questionId = questionId;
        this.ques = ques;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.answer = answer;
        this.quizId = quizId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getQues() {
        return ques;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public String getAnswer() {
        return answer;
    }

    public int getQuizId() {
        return quizId;
    }
}
