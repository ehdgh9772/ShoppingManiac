package com.example.kcci.shoppingmaniac;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import java.util.Collection;
import java.util.HashMap;


/**
 * Created by bro on 2017. 7. 16..
 */

public class Beacon implements RECOServiceConnectListener,
//        RECOMonitoringListener,
        RECORangingListener
{

    static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";
    static final boolean SCAN_RECO_ONLY = true;
    static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;
    static final boolean DISCONTINUOUS_SCAN = false;
    static final int REQUEST_ENABLE_BT = 1;
    static final int REQUEST_LOCATION = 10;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    protected RECOBeaconManager mRecoManager;
    protected RECOBeaconRegion region;
    private HashMap<Integer, String> regionMap;
    private int beaconRssiCritical = -85;

    private TextView _txtVSpottedConer;
    private View _rootLayout;

//    private Context context;
    private Activity activity;

    public Beacon(Activity activity,
                  TextView _txtVSpottedConer,
                  View _rootLayout) {
        this.activity = activity;
        this._txtVSpottedConer = _txtVSpottedConer;
        this._rootLayout = _rootLayout;

        region = new RECOBeaconRegion(RECO_UUID, 11, "KCCI Mart");
        regionMap = new HashMap<>();
        regionMap.put(111, "entrance");
        regionMap.put(112, "grocery");
        regionMap.put(113, "meat");
        regionMap.put(114, "appliance");
    }

    void scanBeacon() {

        this.getAuthBT();
        mRecoManager = RECOBeaconManager.getInstance(
                activity.getApplicationContext(),
                SCAN_RECO_ONLY,
                ENABLE_BACKGROUND_RANGING_TIMEOUT
        );


        mRecoManager.setRangingListener(this);

//        mRecoManager.setScanPeriod(mScanPeriod);
//        mRecoManager.setSleepPeriod(mSleepPeriod);

        mRecoManager.bind(this);

    }


    private void getAuthBT() {

        //If a user device turns off bluetooth, request to turn it on.
        //사용자가 블루투스를 켜도록 요청합니다.
        mBluetoothManager = (BluetoothManager) activity.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
          activity.startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is not granted.");
                this.requestLocationPermission();
            } else {
                Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is already granted.");
            }
        }
    }

    private void requestLocationPermission() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        Snackbar.make(_rootLayout, "location_permission_rationale", Snackbar.LENGTH_INDEFINITE)
                .setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(
                                activity,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_LOCATION
                        );
                    }
                }).show();
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_LOCATION: {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Snackbar.make(_rootLayout, "location_permission_granted", Snackbar.LENGTH_LONG)
//                            .show();
//                } else {
//                    Snackbar.make(_rootLayout, "location_permission_not_granted", Snackbar
//                            .LENGTH_LONG).show();
//                }
//            }
//            default:
//                break;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            //If the requestDiscountInfo to turn on bluetooth is denied, the app will be finished.
//            //사용자가 블루투스 요청을 허용하지 않았을 경우, 어플리케이션은 종료됩니다.
//            finish();
//            return;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    //region ranging overrides

    /** when some beacons in some ranges itll be fired*/
    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> collection, RECOBeaconRegion recoBeaconRegion) {
        int beaconCounter = 0;
        for (RECOBeacon bc : collection) {
            if ( bc.getRssi() > beaconRssiCritical ) {
                beaconCounter++;
                _txtVSpottedConer.append(
                        regionMap.get(bc.getMinor()) + "\n"
                );
            }
        }
        if ( beaconCounter == 0 ) _txtVSpottedConer.setText(R.string.no_beacons_spotted);
    }

    @Override
    public void rangingBeaconsDidFailForRegion(RECOBeaconRegion recoBeaconRegion, RECOErrorCode recoErrorCode) {

    }


    //endregion


    /**
     * callback when beacon service connected
     */
    @Override
    public void onServiceConnect() {
//        this.start(mRegions);
        this.start(region);
    }

    /**
     * fail to connect beacon service*/
    @Override
    public void onServiceFail(RECOErrorCode recoErrorCode) {

    }


    //    private void start(ArrayList<RECOBeaconRegion> mRegions) {
    private void start(RECOBeaconRegion region) {
//        for (RECOBeaconRegion region : mRegions) {
//            try {
////                region.setRegionExpirationTimeMillis(60*1000L);
//                mRecoManager.startMonitoringForRegion(region);
//            } catch (RemoteException e) {
//                Log.i("RECOMonitoringActivity", "Remote Exception");
//                e.printStackTrace();
//            } catch (NullPointerException e) {
//                Log.i("RecoMonitoringActivity", "Null Pointer Exception");
//                e.printStackTrace();
//            }
//        }
        try {
//            mRecoManager.startMonitoringForRegion(region);
            mRecoManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }




//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
////        this.stop(mRegions);
//        this.stop(region);
//        this.unbind();
//    }

    protected void stop(RECOBeaconRegion regions) {
//    protected void stop(ArrayList<RECOBeaconRegion> regions) {
//        for (RECOBeaconRegion region : regions) {
//            try {
//                mRecoManager.stopMonitoringForRegion(region);
//            } catch (RemoteException e) {
//                Log.i("RecoMonitoringActivity", "Remote Exception");
//                e.printStackTrace();
//            } catch (NullPointerException e) {
//                Log.i("RecoMonitoringActivity", "Null Pointer Exception");
//                e.printStackTrace();
//            }
//        }
        try {
            mRecoManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void unbind() {
        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {
            Log.i("RecoMonitoringActivity", "Remote Exception");
            e.printStackTrace();
        }
    }

}
