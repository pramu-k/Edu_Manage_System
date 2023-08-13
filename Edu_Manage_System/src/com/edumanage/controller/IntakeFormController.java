package com.edumanage.controller;

import com.edumanage.db.DbConnection;
import com.edumanage.model.Intake;
import com.edumanage.model.Program;
import com.edumanage.view.tm.IntakeTm;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IntakeFormController {
    
    public AnchorPane context;
    public TextField txtIntakeId;

    public TextField txtName;
    
    public TextField txtSearch;
    public Button btnSave;

    public TableColumn colId;
    public TableColumn colIntake;
    public TableColumn colStartDate;
    public TableColumn colProgram;
    public TableColumn colCompleteState;
    public TableColumn colOptions;
    public DatePicker txtStartDate;
    public TableView<IntakeTm> tblIntake;
    public ComboBox<String> cmbPrograms;

    String searchText="";
    ArrayList<String> programsArray = new ArrayList<>();


    public void initialize() throws SQLException, ClassNotFoundException {
        addProgramsToCmb();

        colId.setCellValueFactory(new PropertyValueFactory<>("intakeId"));
        colIntake.setCellValueFactory(new PropertyValueFactory<>("intakeName"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colProgram.setCellValueFactory(new PropertyValueFactory<>("program"));
        colCompleteState.setCellValueFactory(new PropertyValueFactory<>("completeState"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("deleteIntake"));

        tblIntake.getSelectionModel().
                selectedItemProperty().
                addListener(((observable, oldValue, newValue) -> {
                    if(null!=newValue){
                        setData(newValue);
                    }

                }));
        setIntakeId();
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
    private void addProgramsToCmb() throws SQLException, ClassNotFoundException {
        for (Program program:ProgramsFormController.searchPrograms("")) {
            programsArray.add(program.getCode()+":"+program.getName());
            ObservableList<String> observableList= FXCollections.observableArrayList(programsArray);
            cmbPrograms.setItems(observableList);
        }
    }

    private void setTableData(String searchText) throws SQLException, ClassNotFoundException {
        ObservableList<IntakeTm> observableList=FXCollections.observableArrayList();
        for (Intake intake:searchIntakes(searchText)) {
            Button btn = new Button("Delete");
            IntakeTm tm= new IntakeTm(
                    intake.getIntakeId(),
                    intake.getIntakeName(),
                    new SimpleDateFormat("yyyy-MM-dd").format(intake.getStartDate()),
                    intake.getProgramId(),
                    intake.isIntakeCompleteness(),
                    btn);
            observableList.add(tm);

        }
        tblIntake.setItems(observableList);

    }
    private String getLastId() throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement=connection.prepareStatement
                ("SELECT intakeId FROM intakes ORDER BY CAST(SUBSTRING(intakeId,4)AS UNSIGNED) DESC LIMIT 1 ");
        ResultSet resultSet= preparedStatement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }else {
            return null;
        }
    }

    private void setIntakeId() {
        try {
            String lastId=getLastId();
            if(null!=lastId){
                String lastIdAsString=lastId.split("-")[1];
                int lastIdAsInteger=Integer.parseInt(lastIdAsString);
                lastIdAsInteger++;
                String generatedIntakeId ="In-"+lastIdAsInteger;
                txtIntakeId.setText(generatedIntakeId);
            }else {
                txtIntakeId.setText("In-1");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    private String getProgramId(String text){
        if(text!=null){
            String programId=text.split(":")[0];
            return programId;
        }else {
            return "";
        }
    }
    private String getTeacherId(String programId) throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement=connection.prepareStatement
                ("SELECT teacher_teacherId FROM programs WHERE programId=? ");
        preparedStatement.setString(1, getProgramId(cmbPrograms.getValue()));
        ResultSet resultSet= preparedStatement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }else {
            return "";
        }
    }

    private void setData(IntakeTm tm) {
        txtIntakeId.setText(tm.getIntakeId());
        txtStartDate.setValue(LocalDate.parse(tm.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        txtName.setText(tm.getIntakeName());
        cmbPrograms.setValue(tm.getProgram());
    }


    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashBoardForm");
    }

    public void newIntakeOnAction(ActionEvent actionEvent) {
        clearFields();
    }

    public void saveOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Intake saveIntakeObj= new Intake(
                txtIntakeId.getText(),
                java.util.Date.from(txtStartDate.getValue().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant()),
                txtName.getText(),
                false,
                getProgramId(cmbPrograms.getValue()),
                getTeacherId(getProgramId(cmbPrograms.getValue())));
        if(saveIntake(saveIntakeObj)){
            new Alert(Alert.AlertType.INFORMATION,"Teacher Saved!").show();
            setTableData(searchText);
        }else{
            new Alert(Alert.AlertType.WARNING,"Something went wrong. Try Again!").show();
        }

    }
    private boolean saveIntake(Intake intake) throws SQLException, ClassNotFoundException {

        Connection connection= DbConnection.getInstance().getConnection();
        String sql = "INSERT INTO intakes VALUES (?,?,?,?,?,?)";
        PreparedStatement statement=connection.prepareStatement(sql);

        statement.setString(1, intake.getIntakeId());
        statement.setObject(2, intake.getStartDate());
        statement.setString(3, intake.getIntakeName());
        statement.setBoolean(4, intake.isIntakeCompleteness());
        statement.setString(5,intake.getProgramId());
        statement.setString(6, intake.getTeacherId());
        return statement.executeUpdate()>0;
    }
    private List<Intake> searchIntakes(String searchText) throws SQLException, ClassNotFoundException {
        searchText="%"+searchText+"%";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement=connection.prepareStatement
                ("SELECT * FROM intakes WHERE intakeId LIKE ? OR intakeName LIKE ? ");
        preparedStatement.setString(1,searchText);
        preparedStatement.setString(2,searchText);
        ResultSet resultSet=preparedStatement.executeQuery();
        List<Intake> intakesearchList=new ArrayList<>();
        while (resultSet.next()){
            intakesearchList.add(new Intake(
                    resultSet.getString(1),
                    resultSet.getDate(2),
                    resultSet.getString(3),
                    resultSet.getBoolean(4),
                    resultSet.getString(5),
                    resultSet.getString(6)));
        }
        return intakesearchList;
    }
    private void clearFields(){
        txtStartDate.setValue(null);
        txtName.clear();
        cmbPrograms.setValue(null);
        setIntakeId();
    }
    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();

    }
}
