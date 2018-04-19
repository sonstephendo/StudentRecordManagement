package com.project.javafx.model;

public class StudentResult {

    private double midtermPoint;
    private double finalPoint;
    private double score;
    private double scale;

    public StudentResult(double scale) {
        this.midtermPoint = -1;
        this.finalPoint = -1;
        this.score = -1;
        this.scale = scale;
    }

    public StudentResult(double midtermPoint, double finalPoint) {
        this.midtermPoint = midtermPoint;
        this.finalPoint = finalPoint;
        this.score = calculateScore();
    }

    private double calculateScore() {
        if (midtermPoint != -1 && finalPoint != -1) {
            double result = midtermPoint * scale + finalPoint * (1 - scale);
            return Math.round(result * 10.0) / 10.0;
        }
        return -1;
    }

    // getter and setter
    public double getMidtermPoint() {
        return midtermPoint;
    }

    public double getFinalPoint() {
        return finalPoint;
    }

    public double getScore() {
        calculateScore();
        return score;
    }

    public double getScoreTranfer() {
        if (5.5 < score && score <= 7.0) return (score + 0.5) / 3.0;
        else if (7.0 < score && score <= 8.0) return (score - 3.45) / 1.42;
        else if (8.0 < score && score <= 9.0) return (score / 2.5);
        else if (9.0 < score && score <= 10.0) return (score / 2.5);
        else return 1.9; // < 2.0 means false;
    }


}
