package com.example.min0962.googlemap;

import android.graphics.Movie;

import java.util.ArrayList;

public class Station {

    String name;
    double x_location_info;
    double y_location_info;
    int dust;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX_location_info() {
        return x_location_info;
    }

    public void setX_location_info(float x_location_info) {
        this.x_location_info = x_location_info;
    }

    public double getY_location_info() {
        return y_location_info;
    }

    public void setY_location_info(float y_location_info) {
        this.y_location_info = y_location_info;
    }

    public int getDust() {
        return dust;
    }

    public void setDust(int dust) {
        this.dust = dust;
    }


}
