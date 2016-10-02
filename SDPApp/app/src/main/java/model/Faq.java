package model;

/**
 * Created by kisungtae on 21/09/2016.
 */

public class Faq {
    private String question;
    private String answer;

    public Faq() {
    }

    public String getAnswer() {
        return answer;
    }

    //for test, remove me
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    //for test, remove me
    public void setQuestion(String question) {
        this.question = question;
    }
}