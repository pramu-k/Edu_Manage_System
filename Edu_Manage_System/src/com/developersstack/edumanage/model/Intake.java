package com.developersstack.edumanage.model;

import java.util.Date;

public class Intake {
    private String intakeId;
    private Date startDate;
    private String intakeName;
    private boolean intakeCompleteness;
    private String programId;
    private String teacherId;


    public Intake(String intakeId, Date startDate, String intakeName, boolean intakeCompleteness, String programId, String teacherId) {
        this.intakeId = intakeId;
        this.startDate = startDate;
        this.intakeName = intakeName;
        this.intakeCompleteness = intakeCompleteness;
        this.programId = programId;
        this.teacherId = teacherId;
    }

    public Intake() {
    }
    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(String intakeId) {
        this.intakeId = intakeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getIntakeName() {
        return intakeName;
    }

    public void setIntakeName(String intakeName) {
        this.intakeName = intakeName;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public boolean isIntakeCompleteness() {
        return intakeCompleteness;
    }

    public void setIntakeCompleteness(boolean intakeCompleteness) {
        this.intakeCompleteness = intakeCompleteness;
    }

    @Override
    public String toString() {
        return "Intake{" +
                "intakeId='" + intakeId + '\'' +
                ", startDate=" + startDate +
                ", intakeName='" + intakeName + '\'' +
                ", intakeCompleteness=" + intakeCompleteness +
                ", programId='" + programId + '\'' +
                ", teacherId='" + teacherId + '\'' +
                '}';
    }
}
