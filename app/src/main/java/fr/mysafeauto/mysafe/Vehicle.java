package fr.mysafeauto.mysafe;

import java.io.Serializable;

/**
 * Created by Rahghul on 05/02/2016.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Vehicle implements Serializable {
    String imei;
    String brand;
    String color;

    public Vehicle(){}

    public Vehicle(String imei, String brand, String color) {
        this.imei = imei;
        this.brand = brand;
        this.color = color;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "imei=" + imei.toString() +
                ", brand='" + brand.toString() + '\'' +
                ", color='" + color.toString() + '\'' +
                '}';
    }
}
