package com.developersstack.edumanage.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.SimpleTimeZone;

import static java.time.Duration.ofSeconds;

public class DashBoardFormController {
    public AnchorPane context;
    public Label lblDate;
    public Label lblTime;

    public void initialize(){
        setData();
    }

    private void setData() {
/*        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String textDate = dateformat.format(date);
        lblDate.setText(textDate);*/

        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        //lblTime.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        Timeline timeline =new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(0),
                        e->{
                    DateTimeFormatter dateFormatter =DateTimeFormatter.ofPattern("hh:mm:ss");
                      lblTime.setText(LocalTime.now().format(dateFormatter));
                        }),
                new KeyFrame(javafx.util.Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    public void logoutOnAction(ActionEvent actionEvent) throws IOException {
        setUi("LoginForm");
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();

    }

    public void openStudentFormOnAction(ActionEvent actionEvent) throws IOException {
        setUi("StudentForm");
    }

    public void openTeacherFormOnAction(ActionEvent actionEvent) throws IOException {
        setUi("TeacherForm");
    }

    public void openIntakesFormOnAction(ActionEvent actionEvent) throws IOException {
        setUi("IntakeForm");
    }

    public void openProgramsFormOnAction(ActionEvent actionEvent) throws IOException {
        setUi("ProgramsForm");
    }

    public void openRegistrationFormOnAction(ActionEvent actionEvent) throws IOException {
        setUi("RegistrationForm");
    }
}
