package com.developersstack.edumanage.controller;

import com.developersstack.edumanage.db.Database;
import com.developersstack.edumanage.db.DbConnection;
import com.developersstack.edumanage.model.Student;
import com.developersstack.edumanage.model.Teacher;
import com.developersstack.edumanage.view.tm.StudentTm;
import com.developersstack.edumanage.view.tm.TeacherTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public class TeacherFormController {
    public TextField txtSearch;
    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public Button btnSave;
    public TableView<TeacherTm> tblTeachers;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colContact;
    public TableColumn colAddress;
    public TableColumn colOptions;
    public TextField txtContact;
    public AnchorPane teacherContext;

    String searchText ="";

    public void initialize() throws SQLException, ClassNotFoundException {
        colId.setCellValueFactory(new PropertyValueFactory<>("code")); //these names should be exactly the field names in StudentTm.java
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("btn"));
        //Above code segment is for displaying the student data in the table

/*        Now we are coding for once a record in the table is selected, those values are displayed in the text fields
        and if we change anything there and press save student button, the record is updated.*/
        tblTeachers.getSelectionModel().
                selectedItemProperty().
                addListener(((observable, oldValue, newValue) -> {
                    if(null!=newValue){
                        setData(newValue);
                    }

                }));
        setTeacherId();
        setTableData(searchText);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText=newValue;
            try {
                setTableData(newValue);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setData(TeacherTm tc) {
        txtId.setText(tc.getCode());
        txtName.setText(tc.getName());
        txtAddress.setText(tc.getAddress());
        txtContact.setText(tc.getContact());
        btnSave.setText("Update Teacher");
    }


    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashBoardForm");
    }

    public void newTeacherOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        btnSave.setText("Save Teacher");
        setTeacherId();
        clearFields();
    }
    public void saveOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Teacher teacher = new Teacher(
                txtId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                txtContact.getText());

        if(!txtName.getText().isEmpty() &&
                !txtContact.getText().isEmpty() &&
                !txtAddress.getText().isEmpty()){
            if(btnSave.getText().equalsIgnoreCase("Save Teacher")){
                if(saveTeacher(teacher)){
                    new Alert(Alert.AlertType.INFORMATION,"Teacher Saved!").show();
                }else {
                    new Alert(Alert.AlertType.WARNING,"Try Again!").show();
                }
            }else {
                //Update
                if(updateTeacher(teacher)){
                    new Alert(Alert.AlertType.INFORMATION,"Teacher Updated!").show();
                    btnSave.setText("Save Teacher");
                }else {
                    new Alert(Alert.AlertType.WARNING, "Not Found!").show();
                }
            }
            setTeacherId();
            clearFields();
            setTableData(searchText);
        }else{
            new Alert(Alert.AlertType.WARNING, "Please check the fields are not empty and try again!").show();
        }


    }

    private void setTeacherId() throws SQLException, ClassNotFoundException {
        String lastId=getLastId();
        if(lastId!=null){
            String splitData[] = lastId.split("-");
            String lastIdIntegerNumberAsString=splitData[1];
            int lastIntegerIdAsInt= Integer.parseInt(lastIdIntegerNumberAsString);
            lastIntegerIdAsInt++;
            String generatedTeacherId= "T-"+lastIntegerIdAsInt;
            txtId.setText(generatedTeacherId);
        }else{
            txtId.setText("T-1");
        }
    }

    private void setTableData(String searchText) throws SQLException, ClassNotFoundException {
        ObservableList<TeacherTm> obList = FXCollections.observableArrayList();
        for (Teacher tc:searchTeachers(searchText)) {
            Button btn = new Button("Delete");
            TeacherTm tm = new TeacherTm(
                    tc.getCode(),
                    tc.getName(),
                    tc.getAddress(),
                    tc.getContact(),
                    btn);

            btn.setOnAction(e->{

                Alert alert= new Alert(Alert.AlertType.CONFIRMATION,"Are You Sure?",ButtonType.YES,ButtonType.NO);
                Optional<ButtonType> buttonType = alert.showAndWait();
                if(buttonType.get().equals(ButtonType.YES)){
                    try {
                        if (deleteTeacher(tc.getCode())) {
                            new Alert(Alert.AlertType.INFORMATION, "Deleted!").show();
                            setTableData(searchText);
                            setTeacherId();
                        } else {
                            new Alert(Alert.AlertType.WARNING, "Try Again!").show();

                        }
                    }catch (SQLIntegrityConstraintViolationException exception){
                        new Alert(Alert.AlertType.WARNING, "This teacher is already Assigned to a Course. Please check again!").show();
                    } catch (ClassNotFoundException | SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            obList.add(tm);

            }
        tblTeachers.setItems(obList);
        }

    private void clearFields() {
        txtContact.clear();
        txtName.clear();
        txtAddress.clear();

    }
    private boolean saveTeacher(Teacher teacher) throws SQLException, ClassNotFoundException {
        Connection connection= DbConnection.getInstance().getConnection();
        String sql = "INSERT INTO teacher VALUES (?,?,?,?)";
        PreparedStatement statement=connection.prepareStatement(sql);

        statement.setString(1, teacher.getCode());
        statement.setString(2, teacher.getName() );
        statement.setString(3, teacher.getAddress());
        statement.setString(4, teacher.getContact());

        return statement.executeUpdate()>0;
    }
    public static List<Teacher> searchTeachers(String text) throws ClassNotFoundException, SQLException {
        text="%"+text+"%";
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement=connection.prepareStatement
                ("SELECT * FROM teacher WHERE name LIKE ? OR address LIKE ? ");
        preparedStatement.setString(1,text);
        preparedStatement.setString(2,text);
        ResultSet resultSet= preparedStatement.executeQuery();
        List<Teacher> list=new ArrayList<>();
        while (resultSet.next()){
            list.add(new Teacher(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)));
        }
        return list;
    }
    private boolean deleteTeacher(String id) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement=connection.prepareStatement
                ("DELETE FROM teacher WHERE teacherId = ?");
        preparedStatement.setString(1,id);
        return preparedStatement.executeUpdate()>0;

    }
    private String getLastId() throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement=connection.prepareStatement
                ("SELECT teacherId FROM teacher ORDER BY CAST(SUBSTRING(teacherId,3)AS UNSIGNED) DESC LIMIT 1 ");
        ResultSet resultSet= preparedStatement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }
        return null;
    }
    private boolean updateTeacher(Teacher teacher) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement statement=connection.prepareStatement("UPDATE teacher SET name = ?,address=?,contact = ? WHERE teacherId=?");
        statement.setString(1, teacher.getName());
        statement.setObject(2, teacher.getAddress());
        statement.setString(3, teacher.getContact());
        statement.setString(4, teacher.getCode());

        return statement.executeUpdate()>0;
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) teacherContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();

    }
}
