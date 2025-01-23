package com.odinseyenavigation;

public class qrdata {


    String number;
    String question;
    String value;
    String answer;
    String time;
    int points;



    String queimagename;


    int scans;

    public qrdata() {
    }

    public qrdata(String number, String question, String value, int scans, String answer, String time, int points, String queimagename) {
        this.number = number;
        this.question = question;
        this.value = value;
        this.scans = scans;
        this.answer = answer;
        this.time = time;
        this.points = points;
        this.queimagename = queimagename;
    }

    public String getQueimagename() {
        return queimagename;
    }

    public void setQueimagename(String queimagename) {
        this.queimagename = queimagename;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getScans() {
        return scans;
    }

    public void setScans(int scans) {
        this.scans = scans;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
