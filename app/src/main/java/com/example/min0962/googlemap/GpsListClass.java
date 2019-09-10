package com.example.min0962.googlemap;

import java.util.ArrayList;
import java.util.List;

public class GpsListClass {
//    ArrayList<GpsLists> gpsList=new ArrayList<GpsLists>();

    private List<GpsLists> gpsList;

    public GpsListClass(List<GpsLists> gpsList) {
        this.gpsList = gpsList;
    }

    public List<GpsLists> getGpsList() {
        return gpsList;
    }

    public void setGpsList(List<GpsLists> gpsList) {
        this.gpsList = gpsList;
    }
}
