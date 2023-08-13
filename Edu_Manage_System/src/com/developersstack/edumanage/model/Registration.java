package com.developersstack.edumanage.model;

import java.util.Date;

public class Registration {
    private String regId;
    private Date regDate;
    private boolean paymentCompleteness;
    private String studentId;
    private String programId;


    public Registration(String regId, Date regDate, boolean paymentCompleteness, String studentId, String programId) {
        this.regId = regId;
        this.regDate = regDate;
        this.paymentCompleteness = paymentCompleteness;
        this.studentId = studentId;
        this.programId = programId;

    }

    public Registration() {
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public boolean isPaymentCompleteness() {
        return paymentCompleteness;
    }

    public void setPaymentCompleteness(boolean paymentCompleteness) {
        this.paymentCompleteness = paymentCompleteness;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }



    @Override
    public String toString() {
        return "Registration{" +
                "regId='" + regId + '\'' +
                ", regDate=" + regDate +
                ", paymentCompleteness=" + paymentCompleteness +
                ", studentId='" + studentId + '\'' +
                ", programId='" + programId + '\'' +
                '}';
    }
}
