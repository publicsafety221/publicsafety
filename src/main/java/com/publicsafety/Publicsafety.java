package com.publicsafety;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/* 
  Колекція, в якій зберігатися документ баз даних MongoDB, 
  що представляє сутність запису з таблиці publicsafety.
*/
@Document(collection = "publicsafety")
public class Publicsafety {
    @Id
    private String id;
    private String policeOfficer;
    private String surveillanceOperator;
    private String citizen;
    private String incidentDate;
    private String cameraLocations;
    private String incidentType;
    private String requestStatus;
    private int policeExperienceYears;
    private String observationCenterAddress;
    private String observationCenterPhone;

    public Publicsafety(String policeOfficer, String surveillanceOperator, String citizen, String incidentDate, 
        String cameraLocations, String incidentType, String requestStatus, int policeExperienceYears, 
        String observationCenterAddress, String observationCenterPhone) {
        this.policeOfficer = policeOfficer;
        this.surveillanceOperator = surveillanceOperator;
        this.citizen = citizen;
        this.incidentDate = incidentDate;
        this.cameraLocations = cameraLocations;
        this.incidentType = incidentType;
        this.requestStatus = requestStatus;
        this.policeExperienceYears = policeExperienceYears;
        this.observationCenterAddress = observationCenterAddress;
        this.observationCenterPhone = observationCenterPhone;
    }

    public String getId() {
        return id;
    }

    public String getPoliceOfficer() {
        return policeOfficer;
    }

    public String getSurveillanceOperator() {
        return surveillanceOperator;
    }

    public String getCitizen() {
        return citizen;
    }

    public String getIncidentDate() {
        return incidentDate;
    }

    public String getCameraLocations() {
        return cameraLocations;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public int getPoliceExperienceYears() {
        return policeExperienceYears;
    }

    public String getObservationCenterAddress() {
        return observationCenterAddress;
    }

    public String getObservationCenterPhone() {
        return observationCenterPhone;
    }

    @Override
    public String toString() {
        return "Publicsafety {" +
                " id=\"" + id + "\"\n" +
                " policeOfficer=\"" + policeOfficer + "\"\n" +
                " surveillanceOperator=\"" + surveillanceOperator + "\"\n" +
                " citizen=\"" + citizen + "\"\n" +
                " incidentDate=\"" + incidentDate + "\"\n" +
                " cameraLocations=\"" + cameraLocations + "\"\n" +
                " incidentType=\"" + incidentType + "\"\n" +
                " requestStatus=\"" + requestStatus + "\"\n" +
                " policeExperienceYears=\"" + policeExperienceYears + "\"\n" +
                " observationCenterAddress=\"" + observationCenterAddress + "\"\n" +
                " observationCenterPhone=\"" + observationCenterPhone + "\"\n" +
                "}";
    }
}