package com.developersstack.edumanage.controller;

import com.developersstack.edumanage.db.DbConnection;
import com.developersstack.edumanage.model.Payment;
import com.developersstack.edumanage.model.Program;
import com.developersstack.edumanage.model.Registration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistrationFormController {

    public AnchorPane context;
    public TextField txtRegId;

    public ComboBox<String> cmbProgram;
    public RadioButton rdbtnPaid;
    public ToggleGroup paidState;
    public RadioButton rdbtnPending;
    public TextField txtRegStudentId;
    ArrayList<String> programsArray = new ArrayList<>();

    public void initialize() throws SQLException, ClassNotFoundException {
        setRegId(getLastRegId());
        addProgramsToCmb();
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashBoardForm");
    }

    public void registerOnAction(ActionEvent actionEvent) throws  ClassNotFoundException {
        try{
            if(!txtRegStudentId.getText().isEmpty() && cmbProgram.getValue()!=null){
                Registration registration=new Registration(
                        txtRegId.getText(),
                        new Date(),
                        rdbtnPaid.isSelected()?true:false,
                        txtRegStudentId.getText(),
                        cmbProgram.getValue().split(":")[0]);

                Payment payment=new Payment(
                        setPayId(getLastPayId()),
                        txtRegStudentId.getText(),
                        cmbProgram.getValue().split(":")[0],
                        rdbtnPaid.isSelected()?true:false);
                if(saveData(registration,payment)){
                    new Alert(Alert.AlertType.INFORMATION, "Student Successfully Registered!").show();
                    clearFields();
                }else{
                    new Alert(Alert.AlertType.WARNING, "Try Again!").show();
                }
            }else{
                new Alert(Alert.AlertType.WARNING, "Please check all the relevant fields are filled and try again!").show();
            }
        }catch (SQLIntegrityConstraintViolationException e){
            new Alert(Alert.AlertType.WARNING, "Invalid student. Please check and try again!").show();
        }catch (SQLException e){
            e.printStackTrace();
        }




    }
    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();

    }
    public boolean saveData(Registration regDetails, Payment payment) throws SQLException, ClassNotFoundException {
        Connection connection= DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        try {
            if(rdbtnPaid.isSelected()){
                PreparedStatement preparedStatement1 = connection.prepareStatement(
                        "INSERT INTO payments VALUES(?,?,?,?)");
                preparedStatement1.setString(1, payment.getPayId());
                preparedStatement1.setString(2, payment.getStudentId());
                preparedStatement1.setString(3, payment.getProgramId());
                preparedStatement1.setBoolean(4, true);
                boolean isSaved = preparedStatement1.executeUpdate()>0;
                if(isSaved){
                    PreparedStatement preparedStatement2 = connection.prepareStatement(
                            "INSERT INTO registration VALUES(?,?,?,?,?)");
                    preparedStatement2.setString(1, regDetails.getRegId());
                    preparedStatement2.setObject(2, regDetails.getRegDate());
                    preparedStatement2.setBoolean(3, regDetails.isPaymentCompleteness());
                    preparedStatement2.setString(4, regDetails.getStudentId());
                    preparedStatement2.setString(5, regDetails.getProgramId());

                    if(preparedStatement2.executeUpdate()>0){
                        connection.commit();
                        return true;
                    }
                    connection.rollback();
                }
                return false;
            }else {
                PreparedStatement preparedStatement3 = connection.prepareStatement(
                        "INSERT INTO registration VALUES(?,?,?,?,?)");
                preparedStatement3.setString(1, regDetails.getRegId());
                preparedStatement3.setObject(2, regDetails.getRegDate());
                preparedStatement3.setBoolean(3, false);
                preparedStatement3.setString(4, regDetails.getStudentId());
                preparedStatement3.setString(5, regDetails.getProgramId());

                if(preparedStatement3.executeUpdate()>0){
                    connection.commit();
                    return true;
                }
                return false;
            }

        }catch (SQLException e){
            connection.rollback();
            throw e;
        }finally {
            connection.setAutoCommit(true);
        }
    }
    private String getLastRegId() throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement=connection.prepareStatement
                ("SELECT registrationId FROM registration ORDER BY CAST(SUBSTRING(registrationId,3)AS UNSIGNED) DESC LIMIT 1 ");
        ResultSet resultSet= preparedStatement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }else {
            return null;
        }
    }
    private String getLastPayId() throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement=connection.prepareStatement
                ("SELECT payId FROM payments ORDER BY CAST(SUBSTRING(payId,4)AS UNSIGNED) DESC LIMIT 1 ");
        ResultSet resultSet= preparedStatement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }else {
            return null;
        }
    }
    private void setRegId(String lastRegId) {
        if(null!=lastRegId){
            String lastId1AsString= lastRegId.split("-")[1];
            int lastIdAsInteger=Integer.parseInt(lastId1AsString);
            lastIdAsInteger++;
            String generatedRegId ="R-"+lastIdAsInteger;
            txtRegId.setText(generatedRegId);
        }else {
            txtRegId.setText("R-1");
        }

    }
    private String setPayId(String lastPayId) {
        if(null!=lastPayId){
            String lastId1AsString= lastPayId.split("-")[1];
            int lastIdAsInteger=Integer.parseInt(lastId1AsString);
            lastIdAsInteger++;
            String generatedRegId ="PM-"+lastIdAsInteger;
            return generatedRegId;
        }else {
            return "PM-1";
        }

    }
    private void addProgramsToCmb() throws SQLException, ClassNotFoundException {
        for (Program program: ProgramsFormController.searchPrograms("")) {
            programsArray.add(program.getCode()+ ":"+program.getName());
        }
        ObservableList<String> programsObList= FXCollections.observableArrayList(programsArray);
        cmbProgram.setItems(programsObList);
    }
    private void clearFields() throws SQLException, ClassNotFoundException {
        txtRegStudentId.clear();
        setRegId(getLastRegId());
        cmbProgram.setValue(null);
    }
}
