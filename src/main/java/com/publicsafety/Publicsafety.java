package com.publicsafety;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/* 
  Колекція, в якій зберігатися документ баз даних MongoDB, що представляє сутність запису з таблиці publicsafety.
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

    public Publicsafety(String policeOfficer, String surveillanceOperator, String citizen, String incidentDate, String cameraLocations, String incidentType, String requestStatus, int policeExperienceYears, String observationCenterAddress, String observationCenterPhone) {
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