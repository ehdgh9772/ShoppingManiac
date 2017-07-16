package com.example.kcci.shoppingmaniac.database;

/**
 * Created by koo on 17. 7. 16.
 */

public class Beacon {

    private String _BeaconId;
    private String _Name;

    public String getBeaconId() {
        return _BeaconId;
    }

    void setBeaconId(String beaconId) {
        _BeaconId = beaconId;
    }

    public String getName() {
        return _Name;
    }

    void setName(String name) {
        _Name = name;
    }
}
