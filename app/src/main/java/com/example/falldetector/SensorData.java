package com.example.falldetector;

public class SensorData {
    private String accX, accY, accZ;

    //    private String sensorId;
//    private String gyroX,gyroY,gyroz;
    public SensorData() {

    }

    public SensorData(String accX, String accY, String accZ) {
//        this.sensorId=sensorId;
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
//        this.gyroX=gyroX;
//        this.gyroY=gyroY;
//        this.gyroz=gyroZ;
    }

    //    public String getSensorId(){
//        return sensorId;
//    }
    public String getAccX() {
        return accX;
    }

    public String getAccY() {
        return accY;
    }

    public String getAccZ() {

        return accZ;
    }

//    public String getGyroX() {
//        return gyroX;
//    }
//
//    public String getGyroY() {
//        return gyroY;
//    }
//
//    public String getGyroz() {
//        return gyroz;
//    }
}
