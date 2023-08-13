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


public class LoginFormController {
    public AnchorPane context;
    public TextField txtEmail;
    public TextField txtPassword;

    public void createAnAccountOnAction(ActionEvent actionEvent) throws IOException {
        setUi("SignUpForm");
    }
    public void forgotPasswordOnAction(ActionEvent actionEvent) throws IOException {
        setUi("ForgotPasswordForm");
    }
    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        String email = txtEmail.getText().toLowerCase();
        String password = txtPassword.getText().trim();
        try {
            User selectedUser = login(email);
            if (selectedUser!=null) {
                if (new PasswordManager().checkPassword(password, selectedUser.getPassword())) {
                    setUi("DashBoardForm");
                } else {
                    new Alert(Alert.AlertType.ERROR, "Wrong Password!").show();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, String.format("User Not Found!(%s)", email)).show();
            }


        } catch (ClassNotFoundException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.toString()).show();
        }
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();

    }
    private User login(String email) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        String sql = "SELECT * FROM user WHERE email =?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1,email);
        ResultSet resultSet= statement.executeQuery();
        if(resultSet.next()){
            User user = new User(resultSet.getString("firstName"),
                    resultSet.getString("lastName"),
                    resultSet.getString("email"),
                    resultSet.getString("password"));
            return user;
        }else{
            return null;
        }

    }
}
