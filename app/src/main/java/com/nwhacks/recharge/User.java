package com.nwhacks.recharge;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class User {

    private String name;
    private double longitude;
    private double latitude;
    private ArrayList<String> chargersOwned = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }

    public void resetUsername(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return new LatLng(this.longitude, this.latitude);
    }

    public void addChargers(String charger) {
        this.chargersOwned.add(charger);
    }
}
