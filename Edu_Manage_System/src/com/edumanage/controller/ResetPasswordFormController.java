package com.edumanage.controller;

import com.edumanage.db.DbConnection;
import com.edumanage.util.security.PasswordManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResetPasswordFormController {
    public AnchorPane context;
    public TextField txtPassword;

    String selectedEmail="";
    String searchText="";

    public void setUserData(String email) {
        selectedEmail = email;
        System.out.println(selectedEmail);
    }

    public void changePasswordOnAction(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {
        //Optional<User> selectedUser = Database.userTable.stream().filter(e -> e.getEmail().equals(selectedEmail)).findFirst();
        if(searchUsers(selectedEmail)!=null){
            String newPassword= new PasswordManager().encrypt(txtPassword.getText());
            if(changePassword(newPassword,selectedEmail)){
                new Alert(Alert.AlertType.INFORMATION, "Password Successfully Changed!").show();
                setUi("LoginForm");
            }else {
                new Alert(Alert.AlertType.ERROR, "Something Went Wrong. Try Again!").show();
            }
        }else {
            new Alert(Alert.AlertType.ERROR, "Given Email Not Found!").show();
        }
/*        if (selectedUser.isPresent()){
            selectedUser.get().setPassword(new PasswordManager().encrypt(txtPassword.getText()));
            setUi("LoginForm");
        }else{
            new Alert(Alert.AlertType.ERROR, "Not Found").show();
        }*/
    }
    private void setUi(String location) throws IOException{
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/" + location + ".fxml"))));
        stage.centerOnScreen();
    }
    private String searchUsers(String searchText) throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement=connection.prepareStatement
                ("SELECT email FROM user WHERE email=? ");
        preparedStatement.setString(1,searchText);
        ResultSet resultSet= preparedStatement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }else {
            return null;
        }

    }
    private boolean changePassword(String newPassword,String email) throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement=connection.prepareStatement
                ("UPDATE user SET password=? WHERE email=? ");
        preparedStatement.setString(1,newPassword);
        preparedStatement.setString(2,email);
        return (preparedStatement.executeUpdate()>0);

    }

    public void btnBackToLoginOnAction(ActionEvent actionEvent) throws IOException {
        setUi("LoginForm");
    }
}
