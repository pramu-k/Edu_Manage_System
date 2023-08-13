package com.developersstack.edumanage.view.tm;

import javafx.scene.control.Button;

public class IntakeTm {
    private String intakeId;
    private String intakeName;
    private String startDate;
    private String program;
    private boolean completeState;
    private Button deleteIntake;

    public IntakeTm(String intakeId, String intakeName, String startDate, String program, boolean completeState, Button deleteIntake) {
        this.intakeId = intakeId;
        this.intakeName = intakeName;
        this.startDate = startDate;
        this.program = program;
        this.completeState = completeState;
        this.deleteIntake = deleteIntake;
    }

    public IntakeTm() {
    }

    public String getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(String intakeId) {
        this.intakeId = intakeId;
    }

    public String getIntakeName() {
        return intakeName;
    }

    public void setIntakeName(String intakeName) {
        this.intakeName = intakeName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public boolean isCompleteState() {
        return completeState;
    }

    public void setCompleteState(boolean completeState) {
        this.completeState = completeState;
    }

    public Button getDeleteIntake() {
        return deleteIntake;
    }

    public void setDeleteIntake(Button deleteIntake) {
        this.deleteIntake = deleteIntake;
    }

    @Override
    public String toString() {
        return "IntakeTm{" +
                "intakeId='" + intakeId + '\'' +
                ", intakeName='" + intakeName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", program='" + program + '\'' +
                ", completeState=" + completeState +
                ", deleteIntake=" + deleteIntake +
                '}';
    }
}
