package fr.mysafeauto.mysafe.Services.Coordinate;

import java.sql.Date;

/**
 * Created by Rahghul on 08/02/2016.
 */
public class Coordinate {
    String id;
    String latitude;
    String longitude;
    String speed;
    String battery;
    Date dateTime;

    public Coordinate(){};

    public Coordinate(String id, String latitude, String longitude, String speed, String battery, Date dateTime) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.battery = battery;
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "id='" + id + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", speed='" + speed + '\'' +
                ", battery='" + battery + '\'' +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }

}
