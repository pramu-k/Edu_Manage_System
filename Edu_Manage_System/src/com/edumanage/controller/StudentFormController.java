package com.edumanage.controller;

import com.edumanage.db.DbConnection;
import com.edumanage.model.Student;
import com.edumanage.view.tm.StudentTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

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

import javafx.stage.Stage;

public class StudentFormController {
    public AnchorPane context;
    public TextField txtId;
    public TextField txtName;
    public DatePicker txtDob;
    public TextField txtAddress;
    public TableView<StudentTm> tblStudents;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colDob;
    public TableColumn colAddress;
    public TableColumn colOptions;
    public Button btnSave;
    public TextField txtSearch;

    String searchText ="";

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id")); //these names should be exactly the field names in StudentTm.java. Here Tm means Table Model.
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("btn"));
        //Above code segment is for displaying the student data in the table

/*        Now we are coding for once a record in the table is selected, those values are displayed in the text fields
        and if we change anything there and press save student button, the record is updated.*/
        tblStudents.getSelectionModel().
                selectedItemProperty().
                addListener(((observable, oldValue, newValue) -> {
                    if(null!=newValue){
                        setData(newValue);
                    }

        }));
        setStudentId();
        setTableData(searchText);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText=newValue;
            setTableData(newValue);
        });
    }

    private void setData(StudentTm tm) {
        txtId.setText(tm.getId());
        txtName.setText(tm.getFullName());
        txtAddress.setText(tm.getAddress());
        txtDob.setValue(LocalDate.parse(tm.getDob(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        btnSave.setText("Update Student");
    }

    private void setTableData(String searchText) {
        ObservableList<StudentTm> obList = FXCollections.observableArrayList();
        try {
            for (Student st:searchStudents(searchText)) {
                    Button btn = new Button("Delete");
                    StudentTm tm = new StudentTm(
                            st.getStudentId(),
                            st.getFullName(),
                            new SimpleDateFormat("YYYY-MM-dd").format(st.getDateOfBirth()),
                            st.getAddress(),
                            btn);

                    btn.setOnAction(e->{
                        Alert alert= new Alert(Alert.AlertType.CONFIRMATION,"Are You Sure?",ButtonType.YES,ButtonType.NO);
                        Optional<ButtonType> buttonType = alert.showAndWait();
                        if(buttonType.get().equals(ButtonType.YES)){
                            try {
                                if(deleteStudent(st.getStudentId())){
                                    new Alert(Alert.AlertType.INFORMATION,"Deleted").show();
                                    setTableData(searchText);
                                    setStudentId();
                                }else{
                                    new Alert(Alert.AlertType.WARNING,"Try Again!").show();
                                };
                            } catch (ClassNotFoundException | SQLException ex) {
                                new Alert(Alert.AlertType.ERROR,e.toString()).show();
                            }
                        }
                    });
                    obList.add(tm);



            }
            tblStudents.setItems(obList);
        }catch (SQLException|ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    private void setStudentId() {
        try {
            String lastId= getLastId();
            if(lastId!=null){
                String splitData[] = lastId.split("-");
                String lastIdIntegerNumberAsString=splitData[1];
                int lastIntegerIdAsInt= Integer.parseInt(lastIdIntegerNumberAsString);
                lastIntegerIdAsInt++;
                String generatedStudentId= "S-"+lastIntegerIdAsInt;
                txtId.setText(generatedStudentId);
            }else{
                txtId.setText("S-1");
            }

        }catch (SQLException|ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    public void saveOnAction(ActionEvent actionEvent) {
        Student student = new Student(
                txtId.getText(),
                txtName.getText(),
                Date.from(txtDob.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                txtAddress.getText()
        );
        if(btnSave.getText().equalsIgnoreCase("Save Student")){
            try {
                if(saveStudent(student)){
                    System.out.println(student);
                    setStudentId();
                    clearFields();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION,"Student Saved!").show();
                }else {
                    new Alert(Alert.AlertType.WARNING,"Try Again!").show();
                }
            }catch (SQLException | ClassNotFoundException e){
                new Alert(Alert.AlertType.ERROR,e.toString()).show();
            }

        }else{
            try {
                if(updateStudent(student)){
                    clearFields();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION,"Student Updated!").show();
                }else {
                    new Alert(Alert.AlertType.WARNING,"Try Again!").show();
                }
            }catch (SQLException | ClassNotFoundException e){
                new Alert(Alert.AlertType.ERROR,e.toString()).show();
            }


        }

    }

    private void clearFields() {
        txtDob.setValue(null);
        txtName.clear();
        txtAddress.clear();

    }

    public void newStudentOnAction(ActionEvent actionEvent) {
        clearFields();
        setStudentId();
        btnSave.setText("Save Student");
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashBoardForm");
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();

    }
    private boolean saveStudent(Student student) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms", "root", "1234");
        String sql = "INSERT INTO student VALUES (?,?,?,?)";
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setString(1, student.getStudentId());
        statement.setString(2, student.getFullName());
        statement.setObject(3, student.getDateOfBirth());
        statement.setString(4, student.getAddress());

        return statement.executeUpdate()>0;
    }
    private boolean updateStudent(Student student) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement statement=connection.prepareStatement("UPDATE student SET fullName = ?,dateOfBirth=?,address = ? WHERE studentId=?");
        statement.setString(1, student.getFullName());
        statement.setObject(2, student.getDateOfBirth());
        statement.setString(3, student.getAddress());
        statement.setString(4, student.getStudentId());

        return statement.executeUpdate()>0;
    }
    private String getLastId() throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement=connection.prepareStatement
                ("SELECT studentId FROM student ORDER BY CAST(SUBSTRING(studentId,3)AS UNSIGNED) DESC LIMIT 1 ");
        ResultSet resultSet= preparedStatement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }
        return null;
    }

    private List<Student> searchStudents(String text) throws ClassNotFoundException, SQLException {
        text="%"+text+"%";
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement=connection.prepareStatement
                ("SELECT * FROM student WHERE fullName LIKE ? OR address LIKE ? ");
        preparedStatement.setString(1,text);
        preparedStatement.setString(2,text);
        ResultSet resultSet= preparedStatement.executeQuery();
        List<Student> list=new ArrayList<>();
        while (resultSet.next()){
            list.add(new Student(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getDate(3),
                    resultSet.getString(4)));
        }
        return list;
    }
    private boolean deleteStudent(String id) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement=connection.prepareStatement
                ("DELETE FROM student WHERE studentId = ?");
        preparedStatement.setString(1,id);
        return preparedStatement.executeUpdate()>0;

    }
}
