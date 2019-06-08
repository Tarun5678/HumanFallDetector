package com.example.falldetector;

import java.util.Date;

public class SensorData {
    private String accX, accY, accZ;
    private String gyroX,gyroY,gyroZ;
    private int Fall;
    private String date;


    public SensorData(String accX, String accY, String accZ,String gyroX,String gyroY,String gyroZ,int Fall) {
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
        this.gyroX=gyroX;
        this.gyroY=gyroY;
        this.gyroZ=gyroZ;
        this.Fall=Fall;
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
    public String getGyroX(){
        return gyroX;
    }
    public String getGyroY(){
        return gyroY;
    }
    public String getGyroZ(){
        return gyroZ;
    }

    public int getFall() {
        return Fall;
    }
}
