package com.developersstack.edumanage.controller;

import com.developersstack.edumanage.db.Database;
import com.developersstack.edumanage.db.DbConnection;
import com.developersstack.edumanage.model.Program;
import com.developersstack.edumanage.model.Student;
import com.developersstack.edumanage.model.Teacher;
import com.developersstack.edumanage.view.tm.ProgramTm;
import com.developersstack.edumanage.view.tm.TechAddTm;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProgramsFormController {

    public AnchorPane context;


    public TextField txtId;


    public TextField txtName;


    public TextField txtCost;


    public TextField txtTechnology;


    public TableView<TechAddTm> tblTechnologies;


    public TableColumn<?, ?> colTCode;


    public TableColumn<?, ?> colTName;


    public TableColumn<?, ?> colTRemove;


    public ComboBox<String> cmbTeacher;


    public TableView<ProgramTm> tblPrograms;


    public TableColumn<?, ?> colId;


    public TableColumn<?, ?> colProgram;


    public TableColumn<?, ?> colTeacher;


    public TableColumn<?, ?> colTech;


    public TableColumn<?, ?> colCost;


    public TableColumn<?, ?> colOption;


    public TextField txtSearch;
    public Button btn;
    ObservableList<TechAddTm> tmList = FXCollections.observableArrayList();
    String searchText="";

    public void initialize() throws SQLException, ClassNotFoundException {
        setProgramId();
        setTeachers();
        setTableData(searchText);

        colId.setCellValueFactory(new PropertyValueFactory<>("code"));
        colProgram.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        colTech.setCellValueFactory(new PropertyValueFactory<>("btnTech"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

        colTCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colTName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTRemove.setCellValueFactory(new PropertyValueFactory<>("btn"));

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                    searchText=newValue;
                    setTableData(newValue);});




    }

    ArrayList<String> teachersArray = new ArrayList<>();

    private void setTeachers() throws SQLException, ClassNotFoundException {
        for(Teacher t: TeacherFormController.searchTeachers("")){
            teachersArray.add(t.getCode()+"."+t.getName());
        }
        ObservableList<String> obList = FXCollections.observableArrayList(teachersArray);
        cmbTeacher.setItems(obList);
    }

    private void setProgramId() throws SQLException, ClassNotFoundException {
        String lastProgramId=getLastProgramId();
        if(lastProgramId!=null){
            String splitData[] = lastProgramId.split("-");
            String lastIdIntegerNumberAsString=splitData[1];
            int lastIntegerIdAsInt= Integer.parseInt(lastIdIntegerNumberAsString);
            lastIntegerIdAsInt++;
            String generatedStudentId= "P-"+lastIntegerIdAsInt;
            txtId.setText(generatedStudentId);
        }else{
            txtId.setText("P-1");
        }
    }


    public void backToHomeOnAction(ActionEvent event) throws IOException {
        setUi("DashBoardForm");

    }


    public void newProgramOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        clearFields();
    }


    public void saveProgramOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        if(!txtName.getText().isEmpty()&&!txtCost.getText().isEmpty()&&cmbTeacher.getValue()!=null&&tmList!=null){
            String[] selectedTechs = new String[tmList.size()];
            StringBuilder buildString=new StringBuilder();
            int pointer = 0;
            for (TechAddTm t : tmList) {
                selectedTechs[pointer] = t.getName();
                buildString.append(t.getName());
                buildString.append(",");
                pointer++;
            }
            if(buildString.length()>0){
                buildString.setLength(buildString.length()-1);
            }
            String techsToSaveInDb=buildString.toString();

            if (btn.getText().equals("Save Program")) {
                Program program = new Program(
                        txtId.getText(),
                        txtName.getText(),
                        selectedTechs,
                        cmbTeacher.getValue().split("\\.")[0],
                        Double.parseDouble(txtCost.getText())
                );
                if(saveProgram(program,techsToSaveInDb)){
                    new Alert(Alert.AlertType.INFORMATION, "Saved").show();
                    tmList.clear();
                    clearFields();
                }else{
                    new Alert(Alert.AlertType.WARNING, "Try Again!").show();
                }
                //Database.programTable.add(program);


                setTableData(searchText);
            }

        }else {
            new Alert(Alert.AlertType.WARNING, "Check and Try Again!").show();
        }

    }

    private void setTableData(String searchText) {
        ObservableList<ProgramTm> programsTmList = FXCollections.observableArrayList();
        try {
            for (Program p : searchPrograms(searchText)) {
                Button techButton = new Button("show Tech");
                Button removeButton = new Button("Delete");
                ProgramTm tm = new ProgramTm(
                        p.getCode(),
                        p.getName(),
                        p.getTeacherId(),
                        techButton,
                        p.getCost(),
                        removeButton
                );
                removeButton.setOnAction(event -> {
                    Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to delete the record?",ButtonType.YES,ButtonType.NO);
                    Optional<ButtonType> buttonType=alert.showAndWait();
                    if(buttonType.get().equals(ButtonType.YES)){
                        try {
                            if(deleteProgram(tm.getCode())){
                                new Alert(Alert.AlertType.INFORMATION,"Deleted").show();
                                setTableData(searchText);
                                setProgramId();
                            }else {
                                new Alert(Alert.AlertType.WARNING,"Try Again!").show();
                            }
                        } catch (ClassNotFoundException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                });
                programsTmList.add(tm);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        tblPrograms.setItems(programsTmList);
    }
    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();

    }


    public void addTechOnAction(ActionEvent actionEvent) {
        if (!isExists(txtTechnology.getText().trim())) {
            Button btn = new Button("Remove");
            TechAddTm tm = new TechAddTm(
                    tmList.size() + 1, txtTechnology.getText().trim(), btn
            );
            tmList.add(tm);
            tblTechnologies.setItems(tmList);
            txtTechnology.clear();
        } else {
            txtTechnology.selectAll();
            new Alert(Alert.AlertType.WARNING, "Already Exists").show();
        }
    }
    private boolean isExists(String tech) {
        for (TechAddTm tm : tmList) {
            if (tm.getName().equals(tech)) {
                return true;
            }
        }
        return false;
    }
    private boolean saveProgram(Program programdata,String techData) throws SQLException, ClassNotFoundException {
        if(!techData.isEmpty()){
            Connection connection= DbConnection.getInstance().getConnection();
            String sql = "INSERT INTO programs VALUES (?,?,?,?,?)";
            PreparedStatement statement=connection.prepareStatement(sql);

            statement.setString(1, programdata.getCode());
            statement.setString(2, programdata.getName() );
            statement.setString(3, techData);
            statement.setDouble(4,programdata.getCost());
            statement.setString(5,programdata.getTeacherId());
            return statement.executeUpdate()>0;
        }else {
            return false;
        }


    }
    public static List<Program> searchPrograms(String text) throws ClassNotFoundException, SQLException {
        text = "%" + text + "%";
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement
                ("SELECT * FROM programs WHERE programId LIKE ? OR programName LIKE ? OR teacher_teacherId LIKE ? ");
        preparedStatement.setString(1, text);
        preparedStatement.setString(2, text);
        preparedStatement.setString(3, text);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Program> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new Program(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3).split(","),
                    resultSet.getString(5),
                    resultSet.getDouble(4)));
        }
        return list;
    }
    private String getLastProgramId() throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement=connection.prepareStatement(
            "SELECT programId FROM programs ORDER BY CAST(SUBSTRING(programId,3) AS UNSIGNED) DESC LIMIT 1;");
        ResultSet resultSet= preparedStatement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }else {
            return null;
        }
    }

    private boolean deleteProgram(String id) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        PreparedStatement preparedStatement=connection.prepareStatement
                ("DELETE FROM programs WHERE programId = ?");
        preparedStatement.setString(1,id);
        return preparedStatement.executeUpdate()>0;

    }
    private void clearFields() throws SQLException, ClassNotFoundException {
        txtName.clear();
        txtCost.clear();
        cmbTeacher.setValue(null);
        setProgramId();
    }
}

