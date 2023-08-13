package com.edumanage.model;

public class Payment {
    private String payId;
    private String studentId;
    private String programId;
    private boolean paymentCompleteness;

    public Payment(String payId, String studentId, String programId, boolean paymentCompleteness) {
        this.payId = payId;
        this.studentId = studentId;
        this.programId = programId;
        this.paymentCompleteness = paymentCompleteness;
    }

    public Payment() {
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
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

    public boolean isPaymentCompleteness() {
        return paymentCompleteness;
    }

    public void setPaymentCompleteness(boolean paymentCompleteness) {
        this.paymentCompleteness = paymentCompleteness;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "payId='" + payId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", programId='" + programId + '\'' +
                ", paymentCompleteness=" + paymentCompleteness +
                '}';
    }
}
