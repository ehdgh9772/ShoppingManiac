package com.example.kcci.shoppingmaniac.database;

/**
 * Created by koo on 17. 7. 16.
 */

public class Beacon {

    private String _beaconRegionName;
    private int _beaconMinor;
    private int _beaconImg;

    public String get_beaconRegionName() {
        return _beaconRegionName;
    }

    public void set_beaconRegionName(String _beaconRegionName) {
        this._beaconRegionName = _beaconRegionName;
    }

    public int get_beaconMinor() {
        return _beaconMinor;
    }

    public void set_beaconMinor(int _beaconMinor) {
        this._beaconMinor = _beaconMinor;
    }

    public int get_beaconImg() {
        return _beaconImg;
    }

    public void set_beaconImg(int _beaconImg) {
        this._beaconImg = _beaconImg;
    }
}
