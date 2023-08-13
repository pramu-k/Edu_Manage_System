package com.edumanage.controller;

import com.edumanage.db.DbConnection;
import com.edumanage.model.User;
import com.edumanage.util.security.PasswordManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class SignUpFormController {
    public AnchorPane context;
    public TextField txtFirstName;
    public TextField txtEmail;
    public TextField txtLastName;
    public TextField txtPassword;

    public void signUpOnAction(ActionEvent actionEvent) throws IOException {
        String email = txtEmail.getText().toLowerCase();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String password = new PasswordManager().encrypt(txtPassword.getText().trim());
        User createdUser = new User(email, firstName, lastName, password);
        try {
            boolean isSaved = signup(createdUser);

            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "Welcome to the system!").show();
                setUi("LoginForm");
            }else {
                new Alert(Alert.AlertType.WARNING, "Try Again!").show();
            }

        }catch (SQLException | ClassNotFoundException e1){
            new Alert(Alert.AlertType.ERROR, e1.toString()).show();

        }

    }

    private boolean signup(User user) throws ClassNotFoundException, SQLException {
        //Create a connection
        Connection connection = DbConnection.getInstance().getConnection();
        // if you do not enter the url correctly, a communicationsException error will appear.
        // write a sql
        // String sql = "INSERT INTO user VALUES ('" + user.getEmail() + "','" + user.getFirstName() + "','" + user.getLastName() + "','" + user.getPassword() + "')";
        String sql = "INSERT INTO user VALUES (?,?,?,?)";
        //Create statement
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getFirstName());
        statement.setString(3, user.getLastName());
        statement.setString(4, user.getPassword());
        // set sql into the statement and execute

        return statement.executeUpdate()>0;
    }

    public void alreadyHaveAnAccountOnAction(ActionEvent actionEvent) throws IOException {
        setUi("LoginForm");
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/" + location + ".fxml"))));
        stage.centerOnScreen();
    }

}
    //===============================

