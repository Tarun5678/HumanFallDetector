package com.example.falldetector;

import java.util.Date;

public class SensorData {
    private String accX, accY, accZ;
    private int Fall;
    private String date;
    public SensorData() {

    }

    public SensorData(String accX, String accY, String accZ,int Fall) {
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
    }
    public String getAccX() {
        return accX;
    }

    public String getAccY() {
        return accY;
    }

    public String getAccZ() {

        return accZ;
    }


    public int getFall() {
        return Fall;
    }
}
