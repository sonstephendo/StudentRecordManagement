package com.project.javafx.model;

import com.project.javafx.repository.CourseRepository;
import com.project.javafx.repository.StudentRepository;

import java.time.LocalDate;

public class AnnualStudent extends Student implements Registerable {

    private AnnualClass annualClass;
    private StudyYear studyYear;
    private double AVG;

    public AnnualStudent(long studentID, String firstName, String lastName, String gender, LocalDate birthday, String phone, String email, String address, AnnualClass annualClass) {
        super(studentID, firstName, lastName, gender, birthday, phone, email, address, EduSystem.ANNUAL);
        this.annualClass = annualClass;
        this.studyYear = StudyYear.FIRST_YEAR;
    }

    // METHOD
    @Override
    public boolean registerCourse(String courseCode) {
        Course course = CourseRepository.getInstance().findById(courseCode);
        if (course instanceof CreditCourse || course == null) {
            return false;
        } else {
            if (!takenCourses.containsKey(courseCode)) {
                takenCourses.put(courseCode, new StudentResult(course.getScale()));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateStudentResult(String courseCode, double midtermPoint, double finalPoint) {
        if (midtermPoint > 10 || midtermPoint < 0 || finalPoint > 10 || finalPoint < 0) {
            return false;
        } else if (takenCourses.containsKey(courseCode)){
            takenCourses.put(courseCode, new StudentResult(midtermPoint, finalPoint));
            return true;
        }
        return false;
    }

    @Override
    public StudentResult getGradeResult(String courseCode) {
        return takenCourses.get(courseCode);
    }

    @Override
    public boolean checkAbleToGraduated() {
        return graduateAble;
    }

    private boolean graduateAble = false;

    public void updateStudyYear() {
        switch (studyYear) {
            case FIRST_YEAR:
                if (passedAllCourseInYear() || calculateAVG() > 5.0)
                    studyYear = StudyYear.SECOND_YEAR;
                takenCourses.clear();
                break;
            case SECOND_YEAR:
                if (passedAllCourseInYear() || calculateAVG() > 5.0)
                    studyYear = StudyYear.THIRD_YEAR;
                takenCourses.clear();
                break;
            case THIRD_YEAR:
                if (passedAllCourseInYear() || calculateAVG() > 5.0)
                    studyYear = StudyYear.FOURTH_YEAR;
                takenCourses.clear();
                break;
            case FOURTH_YEAR:
                if (passedAllCourseInYear() || calculateAVG() > 5.0);
                    graduateAble = true;
                takenCourses.clear();
                break;
            default:
                break;
        }
    }

    private boolean passedAllCourseInYear() {
        for (StudentResult result: takenCourses.values()) {
//            Course course = CourseRepository.getInstance().findById(courseCode);
//            StudentResult result = course.getResult(getStudentID());
            if (result.getScore() < 4) {
                return false;
            }
        }
        return true;
    }

    private double calculateAVG() {
        double sum = 0;
        for (StudentResult result: takenCourses.values()) {
            sum += result.getScore();
        }
        AVG = Math.round(sum / takenCourses.size() * 10.0) / 10.0;
        return AVG;
    }

    public void regiterClass(AnnualClass annualClass) {
        this.annualClass = annualClass;
        this.studyYear = StudyYear.FIRST_YEAR;
    }

    // getter and setter
    public AnnualClass getAnnualClass() {
        return annualClass;
    }

    public void setAnnualClass(AnnualClass annualClass) {
        this.annualClass = annualClass;
    }

    public StudyYear getStudyYear() {
        return studyYear;
    }

    public String getStudyYearStr() {
        return studyYear.toString();
    }

    public double getAVG() {
        return AVG;
    }

    public enum StudyYear {
        FIRST_YEAR("First Year"),
        SECOND_YEAR("Second Year"),
        THIRD_YEAR("Third Year"),
        FOURTH_YEAR("Fourth Year");

        private final String text;

        StudyYear(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
