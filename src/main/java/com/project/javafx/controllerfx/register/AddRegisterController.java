package com.project.javafx.controllerfx.register;

import com.jfoenix.controls.JFXButton;
import com.project.javafx.model.*;
import com.project.javafx.repository.CourseRepository;
import com.project.javafx.repository.CreditClassRepository;
import com.project.javafx.repository.StudentRepository;
import com.project.javafx.ulti.AlertMaker;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddRegisterController implements Initializable {

    private ObservableList<CreditClass> classObservableList = FXCollections.observableArrayList();
    private ObservableList<CreditCourse> courseObservableList = FXCollections.observableArrayList();

    @FXML
    private JFXButton btnBack;

    @FXML
    private TextField txtStudentID;

    @FXML
    private TextField txtCourseCode;

    @FXML
    private JFXButton btnRegister;

    @FXML
    private TableView<CreditClass> tblCreditClass;

    @FXML
    private TableColumn<CreditClass, String> colClassCode;

    @FXML
    private TableColumn<CreditClass, String> colCourseCode;

    @FXML
    private TableColumn<CreditClass, Number> colEnrolled;

    @FXML
    private TableView<CreditCourse> tblCreditCourse;

    @FXML
    private TableColumn<CreditCourse, String> colCC;

    @FXML
    private TableColumn<CreditCourse, String> colCN;

    @FXML
    private JFXButton btnViewClass;

    @FXML
    private Label lblName;

    @FXML
    private Label lblID;

    @FXML
    private Label lblMajor;

    private CreditStudent student;
    private CreditCourse creditCourse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCols();
        courseObservableList.setAll(CourseRepository.getInstance().findAllCreditCourse());
        tblCreditCourse.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        txtCourseCode.setText(newValue.getCourseCode());
                    } else {
                        txtCourseCode.setText("");
                    }
                });
    }

    private void initCols() {
        tblCreditClass.setItems(classObservableList);
        colClassCode.setCellValueFactory(param -> {
            CreditClass c = param.getValue();
            return new SimpleStringProperty(c.getClassCode());
        });

        colCourseCode.setCellValueFactory(param -> {
            CreditClass c = param.getValue();
            return new SimpleStringProperty(c.getCourse().getCourseCode());
        });

        colEnrolled.setCellValueFactory(param -> {
            CreditClass c = param.getValue();
            return new SimpleIntegerProperty(c.getEnrolled());
        });

        tblCreditCourse.setItems(courseObservableList);
        colCC.setCellValueFactory(param -> {
            CreditCourse c = param.getValue();
            return new SimpleStringProperty(c.getCourseCode());
        });
        colCN.setCellValueFactory(param -> {
            CreditCourse c = param.getValue();
            return new SimpleStringProperty(c.getCourseName());
        });
        lblID.setText("");
        lblName.setText("");
        lblMajor.setText("");
    }

    @FXML
    void goBack(ActionEvent event) {
        ((Stage) btnBack.getScene().getWindow()).close();
    }

    @FXML
    void handleRegister(ActionEvent event) {
        try {
            handleSearchStudent(new ActionEvent());
            handleSearchClass(new ActionEvent());
            CreditClass selectClass = tblCreditClass.getSelectionModel().getSelectedItem();

            if (student == null) throw new IllegalArgumentException("Could not found student !");
            if (creditCourse == null) throw new IllegalArgumentException("Could not found course !");
            if (selectClass == null) throw new IllegalArgumentException("No Class Selected");
            if (student.isTakingCourse(creditCourse))
                throw new IllegalArgumentException(student.getStudentID() + " already took course " + creditCourse.getCourseCode() + " !");
            else if (selectClass.isFull()) throw new IllegalArgumentException("Class is full");
            else if (student.registerCourse(creditCourse)) {
                selectClass.addStudent(student);
                StudentRepository.getInstance().update(student);
                CreditClassRepository.getInstance().update(selectClass);
                AlertMaker.showNotification("Success", "Register update successfully !", AlertMaker.image_checked);
            }
        } catch (IllegalArgumentException e) {
            AlertMaker.showErrorMessage("Error!", e.getMessage());
        }
    }

    @FXML
    void handleSearchStudent(ActionEvent event) {
        String text = txtStudentID.getText();
        if (text.trim().isEmpty()) throw new IllegalArgumentException("No input on Student ID!");
        try {
            Long studentID = Long.valueOf(text);
            Student student = StudentRepository.getInstance().findById(studentID);
            if (student == null) {
                lblID.setText("");
                lblName.setText("");
                lblMajor.setText("");
                AlertMaker.showNotification("Error", "Student Not Found", AlertMaker.image_cross);
            } else {
                if (student instanceof CreditStudent) {
                    lblID.setText(String.valueOf(student.getStudentID()));
                    lblName.setText(student.getLastName() + " " + student.getLastName());
                    lblMajor.setText(((CreditStudent) student).getCreditMajor().getMajorTitle());
                    this.student = (CreditStudent) student;
                } else {
                    AlertMaker.showNotification("Error", "Annual Student cannot register", AlertMaker.image_cross);
                }
            }
        } catch (NumberFormatException e1) {
            AlertMaker.showErrorMessage("Error", "StudentID only contains number !");
        }

    }

    @FXML
    void handleSearchClass(ActionEvent event) {
        String courseCode = txtCourseCode.getText().toUpperCase();
        Course creditCourse = CourseRepository.getInstance().findById(courseCode);
        if (creditCourse == null) {
            AlertMaker.showNotification("Error", "Course Not Found", AlertMaker.image_cross);
        } else {
            if (creditCourse instanceof CreditCourse) {
                classObservableList.setAll(CreditClassRepository.getInstance().getCreditClassOf((CreditCourse) creditCourse));
                this.creditCourse = (CreditCourse) creditCourse;
            } else {
                AlertMaker.showNotification("Error", "Cannot register annual course", AlertMaker.image_cross);
            }
        }
    }
}
