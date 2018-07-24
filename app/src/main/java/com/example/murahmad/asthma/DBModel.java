package com.example.murahmad.asthma;

import java.io.Serializable;

public class DBModel implements Serializable {



    private String deviceId;
    private String rssi;
    private String url;
    private String temperature;
    private String humidity;
    private String date;


    public DBModel() {

    }


    public DBModel(String id, String url, String rssi, String temperature, String humidity, String date ) {

        super();
        this.deviceId = id;
        this.url = url;
        this.rssi = rssi;
        this.temperature = temperature;
        this.humidity = humidity;
        this.date = date;

    }



    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
