package org.smartregister.opd.pojos;

import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-13
 */
public class OpdVisitSummaryResultModel {

    private String testName;
    private String testResult;
    private String diagnosis;
    private String diseaseCode;
    private OpdVisitSummary.Treatment treatment;
    private String dosage;
    private String duration;
    private Date visitDate;
    private String disease;
    private String diagnosisType;


    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDiseaseCode() {
        return diseaseCode;
    }

    public void setDiseaseCode(String diseaseCode) {
        this.diseaseCode = diseaseCode;
    }

    public OpdVisitSummary.Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(OpdVisitSummary.Treatment treatment) {
        this.treatment = treatment;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(String diagnosisType) {
        this.diagnosisType = diagnosisType;
    }

    public static class Treatment {

        private String medicine;
        private String dosage;
        private String duration;

        public String getMedicine() {
            return medicine;
        }

        public void setMedicine(String medicine) {
            this.medicine = medicine;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }
}
