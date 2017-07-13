package com.example.kcci.shoppingmaniac;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kcci.shoppingmaniac.database.Database;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView _recyclerView;
    private TextView _PopupView;                            //항상 보이게 할 뷰
    private WindowManager.LayoutParams _params;  //layout params 객체. 뷰의 위치 및 크기
    private WindowManager _windowManager;          //윈도우 매니저
    private DrawerLayout _drawerLayout;
    private View _drawerView;

    public static String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
        _PopupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Hi", Toast.LENGTH_SHORT).show();
            }
        });
        viewDiscountInfo();
        viewItemInfo();
    }

    /**
     * 레이아웃 초기화
     */

    private void initLayout(){

        _recyclerView = (RecyclerView)findViewById(R.id.recyclerViewMain);

        _PopupView = new TextView(this);                                         //뷰 생성
        _PopupView.setText("...");                        //텍스트 설정
        _PopupView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); //텍스트 크기 18sp
        _PopupView.setTextColor(Color.BLUE);                                  //글자 색상
        _PopupView.setBackgroundColor(Color.argb(127, 0, 255, 255)); //텍스트뷰 배경 색
        _params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,//항상 최 상위. 터치 이벤트 받을 수 있음.
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  //포커스를 가지지 않음
                PixelFormat.TRANSLUCENT);                                        //투명
        _params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;                   //왼쪽 상단에 위치하게 함.

        _windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);  //윈도우 매니저
        _windowManager.addView(_PopupView, _params);      //윈도우에 뷰 넣기. permission 필요.

        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final Database database = new Database();
        database.requestItemByCategory(1, new Database.LoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                Log.i("i", database.getItemList().get(0).getName());
            }
        });
    }

    /**
     * Discount 정보
     */
    private void viewDiscountInfo(){

        List<CardViewContent> DiscountList = new ArrayList<>();

        for (int i =0; i<10; i ++){

            CardViewContent cardViewContent = new CardViewContent();
            cardViewContent.setDiscountType("할인종류");
            cardViewContent.setName("상품명");
            cardViewContent.setPrice("원가");
            cardViewContent.setDiscountedPrice("할인가");
            cardViewContent.setImage(BitmapFactory.decodeResource(getResources(),R.drawable.a));
            DiscountList.add(cardViewContent);
        }
        _recyclerView.setAdapter(new DiscountRecyclerAdapter(DiscountList,R.layout.card_discount));
        _recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void viewItemInfo(){

        List<CardViewContent> ItenList = new ArrayList<>();

        for (int i =0; i<10; i ++){

            CardViewContent cardViewContent = new CardViewContent();
            cardViewContent.setName("상품명");
            cardViewContent.setPrice("원가");
            cardViewContent.setImage(BitmapFactory.decodeResource(getResources(),R.drawable.a));
            ItenList.add(cardViewContent);
        }
        _recyclerView.setAdapter(new ItemRecyclerAdapter(ItenList,R.layout.card_item));
        _recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder> {

        private List<CardViewContent> itemList;
        private int itemLayout;

        /**
         * 생성자
         * @param items
         * @param itemLayout
         */
        ItemRecyclerAdapter(List<CardViewContent> items , int itemLayout){

            this.itemList = items;
            this.itemLayout = itemLayout;
        }

        /**
         * 레이아웃을 만들어서 Holer에 저장
         * @param viewGroup
         * @param viewType
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout,viewGroup,false);
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

            CardViewContent item = itemList.get(position);
            viewHolder._name.setText(item.getName());
            viewHolder._price.setText(item.getPrice());

            viewHolder.img.setBackgroundResource(R.drawable.a);
            viewHolder.itemView.setTag(item);

            viewHolder._btnLineChart.setOnClickListener(new View.OnClickListener() {
                public static final String LOG_TAG = "ViewHolder";

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
        class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView img;
            private TextView _name;
            private TextView _price;
            Button _btnLineChart;

            public ViewHolder(View itemView){
                super(itemView);

                img = (ImageView) itemView.findViewById(R.id.img);
                _name = (TextView) itemView.findViewById(R.id.textName);
                _price = (TextView) itemView.findViewById(R.id.textPrice);
                _btnLineChart =  (Button) itemView.findViewById(R.id.btnLineChart);
            }

        }
    }

    class DiscountRecyclerAdapter extends RecyclerView.Adapter<DiscountRecyclerAdapter.ViewHolder> {

        private List<CardViewContent> albumList;
        private int itemLayout;

        /**
         * 생성자
         * @param items
         * @param itemLayout
         */
        DiscountRecyclerAdapter(List<CardViewContent> items , int itemLayout){

            this.albumList = items;
            this.itemLayout = itemLayout;
        }

        /**
         * 레이아웃을 만들어서 Holer에 저장
         * @param viewGroup
         * @param viewType
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout,viewGroup,false);
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

            CardViewContent item = albumList.get(position);
            viewHolder._discountType.setText(item.getDiscountType());
            viewHolder._name.setText(item.getName());
            viewHolder._price.setText(item.getPrice());
            viewHolder._discountedPrice.setText(item.getDiscountedPrice());

            viewHolder.img.setBackgroundResource(R.drawable.a);
            viewHolder.itemView.setTag(item);

            viewHolder._btnLineChart.setOnClickListener(new View.OnClickListener() {
                public static final String LOG_TAG = "ViewHolder";

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
            return albumList.size();
        }

        /**
         * 뷰 재활용을 위한 viewHolder
         */
        class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView img;
            private TextView _discountType;
            private TextView _name;
            private TextView _price;
            private TextView _discountedPrice;
            Button _btnLineChart;

            public ViewHolder(View itemView){
                super(itemView);

                img = (ImageView) itemView.findViewById(R.id.img);
                _discountType = (TextView) itemView.findViewById(R.id.textDiscountType);
                _name = (TextView) itemView.findViewById(R.id.textName);
                _price = (TextView) itemView.findViewById(R.id.textPrice);
                _discountedPrice = (TextView) itemView.findViewById(R.id.textDiscountedPrice);
                _btnLineChart =  (Button) itemView.findViewById(R.id.btnLineChart);
            }

        }
    }


    //This is a default proximity uuid of the RECO
    public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";

    /**
     * SCAN_RECO_ONLY:
     *
     * If true, the application scans RECO beacons only, otherwise it scans all beacons.
     * It will be used when the instance of RECOBeaconManager is created.
     *
     * true일 경우 레코 비콘만 스캔하며, false일 경우 모든 비콘을 스캔합니다.
     * RECOBeaconManager 객체 생성 시 사용합니다.
     */
    public static final boolean SCAN_RECO_ONLY = true;

    /**
     * ENABLE_BACKGROUND_RANGING_TIMEOUT:
     *
     * If true, the application stops to range beacons in the entered region automatically in 10 seconds (background),
     * otherwise it continues to range beacons. (It affects the battery consumption.)
     * It will be used when the instance of RECOBeaconManager is created.
     *
     * 백그라운드 ranging timeout을 설정합니다.
     * true일 경우, 백그라운드에서 입장한 region에서 ranging이 실행 되었을 때, 10초 후 자동으로 정지합니다.
     * false일 경우, 계속 ranging을 실행합니다. (배터리 소모율에 영향을 끼칩니다.)
     * RECOBeaconManager 객체 생성 시 사용합니다.
     */
    public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;

    /**
     * DISCONTINUOUS_SCAN:
     *
     * There is a known android bug that some android devices scan BLE devices only once.
     * (link: http://code.google.com/p/android/issues/detail?id=65863)
     * To resolve the bug in our SDK, you can use setDiscontinuousScan() method of the RECOBeaconManager.
     * This method is to set whether the device scans BLE devices continuously or discontinuously.
     * The default is set as FALSE. Please set TRUE only for specific devices.
     *
     * 일부 안드로이드 기기에서 BLE 장치들을 스캔할 때, 한 번만 스캔 후 스캔하지 않는 버그(참고: http://code.google.com/p/android/issues/detail?id=65863)가 있습니다.
     * 해당 버그를 SDK에서 해결하기 위해, RECOBeaconManager에 setDiscontinuousScan() 메소드를 이용할 수 있습니다.
     * 해당 메소드는 기기에서 BLE 장치들을 스캔할 때(즉, ranging 시에), 연속적으로 계속 스캔할 것인지, 불연속적으로 스캔할 것인지 설정하는 것입니다.
     * 기본 값은 FALSE로 설정되어 있으며, 특정 장치에 대해 TRUE로 설정하시길 권장합니다.
     */
    public static final boolean DISCONTINUOUS_SCAN = false;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION = 10;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private View mLayout;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mLayout = findViewById(R.id.mainLayout);
//
//        View includedLayout = (View) findViewById(R.id.reco_settings_values );
//        TextView mRecoOnlyText = (TextView) includedLayout.findViewById(R.id.recoRecoonlySetting);
//        if( SCAN_RECO_ONLY ) {
//            mRecoOnlyText.setText(R.string.settings_result_true);
//        } else {
//            mRecoOnlyText.setText(R.string.settings_result_false);
//        }
//
//        TextView mDiscontinuousText = (TextView) includedLayout.findViewById(R.id.recoDiscontinouosSetting);
//        if( DISCONTINUOUS_SCAN ) {
//            mDiscontinuousText.setText(R.string.settings_result_true);
//        } else {
//            mDiscontinuousText.setText(R.string.settings_result_false);
//        }
//
//        TextView mBackgroundRangingTimeoutText = (TextView) includedLayout.findViewById(R.id.recoBackgroundtimeoutSetting);
//        if( ENABLE_BACKGROUND_RANGING_TIMEOUT ) {
//            mBackgroundRangingTimeoutText.setText(R.string.settings_result_true);
//        } else {
//            mBackgroundRangingTimeoutText.setText(R.string.settings_result_false);
//        }
//
//        //If a user device turns off bluetooth, requestDiscountInfo to turn it on.
//        //사용자가 블루투스를 켜도록 요청합니다.
//        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = mBluetoothManager.getAdapter();
//
//        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
//        }
//
//        /**
//         * In order to use RECO SDK for Android API 23 (Marshmallow) or higher,
//         * the location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is required.
//         * Please refer to the following permission guide and sample code provided by Google.
//         *
//         * 안드로이드 API 23 (마시멜로우)이상 버전부터, 정상적으로 RECO SDK를 사용하기 위해서는
//         * 위치 권한 (ACCESS_COARSE_LOCATION 혹은 ACCESS_FINE_LOCATION)을 요청해야 합니다.
//         * 권한 요청의 경우, 구글에서 제공하는 가이드를 참고하시기 바랍니다.
//         *
//         * http://www.google.com/design/spec/patterns/permissions.html
//         * https://github.com/googlesamples/android-RuntimePermissions
//         */
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is not granted.");
//                this.requestLocationPermission();
//            } else {
//                Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is already granted.");
//            }
//        }
//    }

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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch(requestCode) {
//            case REQUEST_LOCATION : {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Snackbar.make(mLayout, R.string.location_permission_granted, Snackbar.LENGTH_LONG).show();
//                } else {
//                    Snackbar.make(mLayout, R.string.location_permission_not_granted, Snackbar.LENGTH_LONG).show();
//                }
//            }
//            default :
//                break;
//        }
//
//
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if(this.isBackgroundMonitoringServiceRunning(this)) {
//            ToggleButton toggle = (ToggleButton)findViewById(R.id.backgroundMonitoringToggleButton);
//            toggle.setChecked(true);
//        }
//
//        if(this.isBackgroundRangingServiceRunning(this)) {
//            ToggleButton toggle = (ToggleButton)findViewById(R.id.backgroundRangingToggleButton);
//            toggle.setChecked(true);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * In order to use RECO SDK for Android API 23 (Marshmallow) or higher,
     * the location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is required.
     *
     * This sample project requests "ACCESS_COARSE_LOCATION" permission only,
     * but you may requestDiscountInfo "ACCESS_FINE_LOCATION" permission depending on your application.
     *
     * "ACCESS_COARSE_LOCATION" permission is recommended.
     *
     * 안드로이드 API 23 (마시멜로우)이상 버전부터, 정상적으로 RECO SDK를 사용하기 위해서는
     * 위치 권한 (ACCESS_COARSE_LOCATION 혹은 ACCESS_FINE_LOCATION)을 요청해야 합니다.
     *
     * 본 샘플 프로젝트에서는 "ACCESS_COARSE_LOCATION"을 요청하지만, 필요에 따라 "ACCESS_FINE_LOCATION"을 요청할 수 있습니다.
     *
     * 당사에서는 ACCESS_COARSE_LOCATION 권한을 권장합니다.
     *
     */
//    private void requestLocationPermission() {
//        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
//            return;
//        }
//
//        Snackbar.make(mLayout, R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                .setAction(R.string.ok, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
//                    }
//                })
//                .show();
//    }
//
//    public void onMonitoringToggleButtonClicked(View v) {
//        ToggleButton toggle = (ToggleButton)v;
//        if(toggle.isChecked()) {
//            Log.i("MainActivity", "onMonitoringToggleButtonClicked off to on");
//            Intent intent = new Intent(this, com.iot.finalproject.RecoBackgroundMonitoringService.class);
//            startService(intent);
//        } else {
//            Log.i("MainActivity", "onMonitoringToggleButtonClicked on to off");
//            stopService(new Intent(this, com.iot.finalproject.RecoBackgroundMonitoringService.class));
//        }
//    }
//
//    public void onRangingToggleButtonClicked(View v) {
//        ToggleButton toggle = (ToggleButton)v;
//        if(toggle.isChecked()) {
//            Log.i("MainActivity", "onRangingToggleButtonClicked off to on");
//            Intent intent = new Intent(this, com.iot.finalproject.RecoBackgroundRangingService.class);
//            startService(intent);
//        } else {
//            Log.i("MainActivity", "onRangingToggleButtonClicked on to off");
//            stopService(new Intent(this, com.iot.finalproject.RecoBackgroundRangingService.class));
//        }
//    }
//
//    public void onButtonClicked(View v) {
//        Button btn = (Button)v;
//        if(btn.getId() == R.id.monitoringButton) {
//            final Intent intent = new Intent(this, com.iot.finalproject.RecoMonitoringActivity.class);
//            startActivity(intent);
//        } else {
//            final Intent intent = new Intent(this, com.iot.finalproject.RecoRangingActivity.class);
//            startActivity(intent);
//        }
//    }
//
//    private boolean isBackgroundMonitoringServiceRunning(Context context) {
//        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
//        for(ActivityManager.RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
//            if(com.iot.finalproject.RecoBackgroundMonitoringService.class.getName().equals(runningService.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean isBackgroundRangingServiceRunning(Context context) {
//        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
//        for(ActivityManager.RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
//            if(com.iot.finalproject.RecoBackgroundRangingService.class.getName().equals(runningService.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
}

