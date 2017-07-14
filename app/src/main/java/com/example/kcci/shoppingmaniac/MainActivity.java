package com.example.kcci.shoppingmaniac;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kcci.shoppingmaniac.database.Database;
import com.example.kcci.shoppingmaniac.database.DiscountInfo;
import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECOMonitoringListener;
import com.perples.recosdk.RECOServiceConnectListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RECOServiceConnectListener, RECOMonitoringListener {

    public static String LOG_TAG = "MainActivity";
    public static final String EXTRA_ID = "itemId";

    private RecyclerView _recyclerView;
    private View btnImgDrawerView;                            //항상 보이게 할 뷰
    private WindowManager.LayoutParams _params;  //layout params 객체. 뷰의 위치 및 크기
    private WindowManager _windowManager;          //윈도우 매니저
    private DrawerLayout _drawerLayout;
    private View _drawerView;
    private View _rootLayout;
    boolean isPageSlided = false;

    public static final int DRAWER_COLUMS = 3;
    public static final int DRAWER_ROWS = 3;
    Animation animGrowFromBottom;
    Animation animSetToBottom;
    LinearLayout slideLayout;

    public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";
    public static final boolean SCAN_RECO_ONLY = true;
    public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;
    public static final boolean DISCONTINUOUS_SCAN = false;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION = 10;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    protected RECOBeaconManager mRecoManager;
    protected ArrayList<RECOBeaconRegion> mRegions;
    private long mScanPeriod = 1 * 1000L;
    private long mSleepPeriod = 3 * 1000L;

    ArrayList<DiscountInfo> _discountInfoList;
    ArrayList<Bitmap> _images;
    ArrayList<String> _itemIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
        scanBeacon();
        btnImgDrawerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDrawerView();
            }
        });

        viewDiscountInfo();

//        viewItemInfo();
    }

    /**
     * 레이아웃 초기화
     */

    private void initLayout() {

        slideLayout = (LinearLayout) findViewById(R.id.hiddenLayout);
        _recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMain);
        animGrowFromBottom = AnimationUtils.loadAnimation(this, R.anim.translate_from_bottom);
        animSetToBottom = AnimationUtils.loadAnimation(this, R.anim.translate_to_bottom);

        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();
        animGrowFromBottom.setAnimationListener(animationListener);
        animSetToBottom.setAnimationListener(animationListener);

        btnImgDrawerView = findViewById(R.id.btnDrawer);
        btnImgDrawerView.bringToFront();

    }

    //region Beacon
    private void scanBeacon() {

        getAuthBT();
        mRecoManager = RECOBeaconManager.getInstance(
                getApplicationContext(),
                SCAN_RECO_ONLY,
                ENABLE_BACKGROUND_RANGING_TIMEOUT
        );
        mRegions = generateBeaconRegion();

        mRecoManager.setMonitoringListener(this);
        mRecoManager.setScanPeriod(mScanPeriod);
        mRecoManager.setSleepPeriod(mSleepPeriod);

        mRecoManager.bind(this);

    }


    private void getAuthBT() {

        //If a user device turns off bluetooth, request to turn it on.
        //사용자가 블루투스를 켜도록 요청합니다.
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is not granted.");
                this.requestLocationPermission();
            } else {
                Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is already granted.");
            }
        }
    }

    private void requestLocationPermission() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            return;
        }
        _rootLayout = findViewById(R.id.frameLayout);

        Snackbar.make(_rootLayout, "location_permission_rationale", Snackbar.LENGTH_INDEFINITE)
                .setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_LOCATION
                        );
                    }
                }).show();
    }


    private ArrayList<RECOBeaconRegion> generateBeaconRegion() {
        ArrayList<RECOBeaconRegion> regions = new ArrayList<>();

        regions.add(new RECOBeaconRegion(RECO_UUID, 11, 111, "entrance"));
        regions.add(new RECOBeaconRegion(RECO_UUID, 11, 112, "grocery"));
        regions.add(new RECOBeaconRegion(RECO_UUID, 11, 113, "meat"));
        regions.add(new RECOBeaconRegion(RECO_UUID, 11, 114, "appliance"));

        return regions;
    }

    /**
     * 하단 감지된 비콘 메뉴 생성 및 보이기
     */
    private void popDrawerView() {
        if (isPageSlided) {
            slideLayout.startAnimation(animGrowFromBottom);
//            ArrayList<> getSpottedBeacon();
//            if ()
//            generateConerIcons();
        } else {
            Log.i(LOG_TAG, "slide animation is on");
            slideLayout.setVisibility(View.VISIBLE);
            slideLayout.startAnimation(animSetToBottom);
        }
    }

    //    public dddd getSpottedBeacon() {
//
//    }
//
//    private void generateConerIcons(int detectedBeaconsAmount ) {
//        if ( detectedBeaconsAmount / DRAWER_ROWS == 0 ) return;
//        LinearLayout _targetLayout = (LinearLayout) findViewById(R.id.hiddenLayout);
//        LinearLayout _rowLayout = new LinearLayout(this);
//        ImageView _btnConerIcon = new ImageView(this);
//
//        _rowLayout.setOrientation(LinearLayout.VERTICAL);
//        _btnConerIcon.
//
//        for (int i = 2;  i < DRAWER_COLUMS; i++) {
//            if (i * i > detectedBeaconsAmount) {
//                break;
//            } else {
//            _targetLayout.
//            }
//        }
//    }
    //endregion
    //region activity
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(_rootLayout, "location_permission_granted", Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    Snackbar.make(_rootLayout, "location_permission_not_granted", Snackbar
                            .LENGTH_LONG).show();
                }
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            //If the requestDiscountInfo to turn on bluetooth is denied, the app will be finished.
            //사용자가 블루투스 요청을 허용하지 않았을 경우, 어플리케이션은 종료됩니다.
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stop(mRegions);
        this.unbind();
    }


    protected void stop(ArrayList<RECOBeaconRegion> regions) {
        for (RECOBeaconRegion region : regions) {
            try {
                mRecoManager.stopMonitoringForRegion(region);
            } catch (RemoteException e) {
                Log.i("RecoMonitoringActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RecoMonitoringActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }

    private void unbind() {
        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {
            Log.i("RecoMonitoringActivity", "Remote Exception");
            e.printStackTrace();
        }
    }

    //endregion
    //region beacon2
    @Override
    public void didEnterRegion(RECOBeaconRegion recoBeaconRegion, Collection<RECOBeacon> collection) {
        ////////비콘 범위 진입 시 콜백
        TextView drawerTxt = (TextView) findViewById(R.id.txtVNoSpotted);
        drawerTxt.setText(recoBeaconRegion.getUniqueIdentifier());
    }

    @Override
    public void didExitRegion(RECOBeaconRegion recoBeaconRegion) {

    }

    @Override
    public void didStartMonitoringForRegion(RECOBeaconRegion recoBeaconRegion) {

    }

    @Override
    public void didDetermineStateForRegion(RECOBeaconRegionState recoBeaconRegionState, RECOBeaconRegion recoBeaconRegion) {

    }

    @Override
    public void monitoringDidFailForRegion(RECOBeaconRegion recoBeaconRegion, RECOErrorCode recoErrorCode) {

    }

    @Override
    public void onServiceConnect() {
        this.start(mRegions);
    }

    @Override
    public void onServiceFail(RECOErrorCode recoErrorCode) {

    }

    private void start(ArrayList<RECOBeaconRegion> mRegions) {
        for (RECOBeaconRegion region : mRegions) {
            try {
//                region.setRegionExpirationTimeMillis(60*1000L);
                mRecoManager.startMonitoringForRegion(region);
            } catch (RemoteException e) {
                Log.i("RECOMonitoringActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RecoMonitoringActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }
    //endregion

    /**
     * Discount 정보
     */
    private void viewDiscountInfo() {
        final Database database = new Database();
        database.requestDiscountInfo(new Database.LoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                _itemIdList = new ArrayList<>();
                _discountInfoList = database.getDiscountInfoList();
                for (int i = 0; i < _discountInfoList.size(); i++) {
                    _itemIdList.add(_discountInfoList.get(i).getItemId());
                }

                database.requestImage(_itemIdList, new Database.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        _images = new ArrayList<>();
                        for (int i = 0; i < _discountInfoList.size(); i++) {
                            _images.add(database.getBitmap(i));
                        }
                        _recyclerView.setAdapter(new DiscountRecyclerAdapter(_discountInfoList, _images, R.layout.card_discount));
                        _recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        _recyclerView.setItemAnimator(new DefaultItemAnimator());
                    }
                });
            }
        });

    }

    private void viewItemInfo() {

        List<DiscountInfo> ItemList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            DiscountInfo discountInfo = new DiscountInfo();

            ItemList.add(discountInfo);
        }
        _recyclerView.setAdapter(new ItemRecyclerAdapter(ItemList, R.layout.card_item));
        _recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    //region DiscountAdapter
    class DiscountRecyclerAdapter extends RecyclerView.Adapter<DiscountRecyclerAdapter.ViewHolder> {

        private List<DiscountInfo> _discountInfoList;
        private List<Bitmap> _imageList;
        private int _layout;

        /**
         * 생성자
         *
         * @param discountInfoList
         * @param layout
         */
        DiscountRecyclerAdapter(List<DiscountInfo> discountInfoList, List<Bitmap> imageList, int layout) {

            _discountInfoList = discountInfoList;
            _imageList = imageList;
            _layout = layout;
        }

        /**
         * 레이아웃을 만들어서 Holer에 저장
         *
         * @param viewGroup
         * @param viewType
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(_layout, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        /**
         * listView getView 를 대체
         * 넘겨 받은 데이터를 화면에 출력하는 역할
         *
         * @param viewHolder
         * @param position
         */
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            final DiscountInfo item = _discountInfoList.get(position);
            viewHolder._discountType.setText(item.getDiscountType());
            viewHolder._name.setText(item.getName());
            viewHolder._price.setText(item.getPrice());
            viewHolder._discountedPrice.setText(item.getDiscountedPrice());
            viewHolder._img.setImageBitmap(_imageList.get(position));
            viewHolder.itemView.setTag(item);

            viewHolder._btnLineChart.setOnClickListener(new View.OnClickListener() {
                public static final String LOG_TAG = "SpottedBeacons";

                @Override
                public void onClick(View view) {
                    switch (view.getId()) {

                        case R.id.btnLineChart:

                            Log.i(LOG_TAG, "Line Chart Start...");

                            Intent intent = new Intent(getApplicationContext(), LineChartActivity.class);
                            intent.putExtra(EXTRA_ID, item.getItemId());
                            startActivity(intent);

//                break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return _discountInfoList.size();
        }

        /**
         * 뷰 재활용을 위한 viewHolder
         */
        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView _img;
            private TextView _discountType;
            private TextView _name;
            private TextView _price;
            private TextView _discountedPrice;
            private Button _btnLineChart;

            public ViewHolder(View itemView) {
                super(itemView);

                _img = (ImageView) itemView.findViewById(R.id.img);
                _discountType = (TextView) itemView.findViewById(R.id.textDiscountType);
                _name = (TextView) itemView.findViewById(R.id.textName);
                _price = (TextView) itemView.findViewById(R.id.textPrice);
                _discountedPrice = (TextView) itemView.findViewById(R.id.textDiscountedPrice);
                _btnLineChart = (Button) itemView.findViewById(R.id.btnLineChart);
            }

        }
    }

    //endregion
    //region ItemAdapter
    class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder> {

        private List<DiscountInfo> itemList;
        private int itemLayout;

        /**
         * 생성자
         *
         * @param items
         * @param itemLayout
         */
        ItemRecyclerAdapter(List<DiscountInfo> items, int itemLayout) {

            this.itemList = items;
            this.itemLayout = itemLayout;
        }

        /**
         * 레이아웃을 만들어서 Holer에 저장
         *
         * @param viewGroup
         * @param viewType
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
            return new ViewHolder(view);
        }


        /**
         * listView getView 를 대체
         * 넘겨 받은 데이터를 화면에 출력하는 역할
         *
         * @param viewHolder
         * @param position
         */
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            DiscountInfo item = itemList.get(position);
            viewHolder._name.setText(item.getName());
            viewHolder._price.setText(item.getPrice());

            viewHolder._img.setBackgroundResource(R.drawable.a);

            viewHolder.itemView.setTag(item);

            viewHolder._btnLineChart.setOnClickListener(new View.OnClickListener() {
                public static final String LOG_TAG = "SpottedBeacons";

                @Override
                public void onClick(View view) {
                    switch (view.getId()) {

                        case R.id.btnLineChart:

                            Log.i(LOG_TAG, "Line Chart Start...");

                            Intent intent = new Intent(getApplicationContext(), LineChartActivity.class);
                            startActivity(intent);

//                break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        /**
         * 뷰 재활용을 위한 viewHolder
         */
        class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView _img;
            private TextView _name;
            private TextView _price;
            Button _btnLineChart;

            public ViewHolder(View itemView) {
                super(itemView);

                _img = (ImageView) itemView.findViewById(R.id.img);
                _name = (TextView) itemView.findViewById(R.id.textName);
                _price = (TextView) itemView.findViewById(R.id.textPrice);
                _btnLineChart = (Button) itemView.findViewById(R.id.btnLineChart);
            }

        }
    }
    //endregion

    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isPageSlided) slideLayout.setVisibility(View.INVISIBLE);
            isPageSlided = !isPageSlided;
            Log.i(LOG_TAG, "animation terminated isPageSlided is : " + isPageSlided);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}

