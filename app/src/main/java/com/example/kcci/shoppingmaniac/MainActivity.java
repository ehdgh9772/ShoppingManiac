package com.example.kcci.shoppingmaniac;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kcci.shoppingmaniac.database.Database;
import com.example.kcci.shoppingmaniac.database.DiscountInfo;
import com.example.kcci.shoppingmaniac.database.Item;
import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static com.example.kcci.shoppingmaniac.R.drawable.b;

public class MainActivity extends AppCompatActivity
        implements RECOServiceConnectListener, RECORangingListener
{

    //region field

    public static String LOG_TAG = "MainActivity";
    public static final String EXTRA_ID = "itemId";

    ConstraintLayout _constraintDrawer;
    private RecyclerView _beaconRecyclerView;
    private RecyclerView _recyclerView;
    private View _openDrawerButton;                            //항상 보이게 할 뷰
    private View _rootLayout;
    boolean isPageSlided = false;
    private TextView _txtVSpottedConer;

    Animation _animGrowFromBottom;
    Animation _animSetToBottom;

    ArrayList<DiscountInfo> _discountInfoList;
    ArrayList<Bitmap> _images;
    ArrayList<String> _itemIdList;
    ArrayList<String> _beaconList;

    //beacon field
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


    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initialize();

        initLayout();

        connectBeacons();

        setAnimation();
        addTest();

        viewDiscountInfo();

//        viewItemInfo();
    }

    //region Initialize
//    private void initialize() {
//        initLayout();
//        setAnimation();
//        addTest();
//        beaconManager.scanBeacon();
//    }



    //region initialize layout , test and drawerView animation
    /**
     * 레이아웃 초기화
     */

    private void initLayout() {

        _beaconList = new ArrayList<>();

        _recyclerView = (RecyclerView) findViewById(R.id.recy_main_Item);
        _constraintDrawer = (ConstraintLayout) findViewById(R.id.cons_main_drawer);

        _beaconRecyclerView = (RecyclerView) findViewById(R.id.recy_main_drawer);
        _beaconRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        _beaconRecyclerView.setAdapter(new BeaconRecyclerAdapter(_beaconList, R.layout.each_beacon));

        _constraintDrawer.setVisibility(View.INVISIBLE);
        _constraintDrawer.bringToFront();

        _openDrawerButton = findViewById(R.id.btn_main_drawer);
        _openDrawerButton.bringToFront();

        _openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDrawerView();
            }
        });

        _txtVSpottedConer = (TextView) findViewById(R.id.txtVSpottedConer);
        _rootLayout = findViewById(R.id.cons_main_frame);

//        beaconManager = new BeaconManager(MainActivity.this, _txtVSpottedConer, _rootLayout);

    }

    private void addTest() {
        _beaconList.add(Database.MAIN);

        Button button = (Button) findViewById(R.id.changeList);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _beaconList.add(Database.MEAT);
                _beaconRecyclerView.setAdapter(new BeaconRecyclerAdapter(_beaconList, R.layout.each_beacon));
            }
        });
    }

    private void popDrawerView() {
        if (isPageSlided) {
            Log.i(LOG_TAG, "slide down");
            _constraintDrawer.startAnimation(_animGrowFromBottom);
            _constraintDrawer.setVisibility(View.INVISIBLE);
        } else {
            Log.i(LOG_TAG, "slide up");
            _constraintDrawer.startAnimation(_animSetToBottom);
            _constraintDrawer.setVisibility(View.VISIBLE);
        }
    }

    private void setAnimation() {
        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();
        _animGrowFromBottom = AnimationUtils.loadAnimation(this, R.anim.translate_from_bottom);
        _animSetToBottom = AnimationUtils.loadAnimation(this, R.anim.translate_to_bottom);
        _animGrowFromBottom.setAnimationListener(animationListener);
        _animSetToBottom.setAnimationListener(animationListener);
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isPageSlided = !isPageSlided;
            Log.i(LOG_TAG, "animation terminated isPageSlided is : " + isPageSlided);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
    //endregion

    //region beacon overrides and bt connection

    private void connectBeacons() {

        this.getAuthBT();
        mRecoManager = RECOBeaconManager.getInstance(
                getApplicationContext(),
                SCAN_RECO_ONLY,
                ENABLE_BACKGROUND_RANGING_TIMEOUT
        );

        region = new RECOBeaconRegion(RECO_UUID, 11, "KCCI Mart");
        regionMap = new HashMap<>();
        regionMap.put(111, "entrance");
        regionMap.put(112, "grocery");
        regionMap.put(113, "meat");
        regionMap.put(114, "appliance");

        mRecoManager.setRangingListener(this);
        mRecoManager.bind(this);

    }

    private void getAuthBT() {

        mBluetoothManager =
                (BluetoothManager) getApplicationContext()
                        .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
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

    /**
     * callback when beaconManager service connected
     */
    @Override
    public void onServiceConnect() {
        this.start(region);
    }

    /**
     * fail to connect beaconManager service*/
    @Override
    public void onServiceFail(RECOErrorCode recoErrorCode) {

    }


    private void start(RECOBeaconRegion region) {
        try {
            mRecoManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    protected void stop(RECOBeaconRegion region) {
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
            e.printStackTrace();
        }
    }

    //endregion

    //region Activity overrides
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
        stop(region);
        unbind();
    }

    //endregion

    //region viewToScreen

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

                database.requestImageList(_itemIdList, new Database.LoadCompleteListener() {
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

    private void viewItemInfo(ArrayList<Item> itemList) {

        _recyclerView.setAdapter(new ItemRecyclerAdapter(itemList, R.layout.card_item));
        _recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    //endregion

    //region RecyclerViewAdapters
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

            viewHolder._btnLineChart.setOnClickListener(new OnLineChartClickListener(item.getItemId()));
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

                _img = (ImageView) itemView.findViewById(R.id.imv_discount);
                _discountType = (TextView) itemView.findViewById(R.id.txv_discount_dcType);
                _name = (TextView) itemView.findViewById(R.id.txv_discount_Name);
                _price = (TextView) itemView.findViewById(R.id.txv_item_price);
                _discountedPrice = (TextView) itemView.findViewById(R.id.txv_discount_dcPrice);
                _btnLineChart = (Button) itemView.findViewById(R.id.btn_discount_lineChart);
            }

        }
    }

    class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder> {

        private List<Item> _itemList;
        private int _itemLayout;

        /**
         * 생성자
         *
         * @param items
         * @param itemLayout
         */
        ItemRecyclerAdapter(List<Item> items, int itemLayout) {

            _itemList = items;
            _itemLayout = itemLayout;
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(_itemLayout, viewGroup, false);
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

            Item item = _itemList.get(position);
            viewHolder._img.setImageBitmap(item.getImage());
            viewHolder._name.setText(item.getName());
            viewHolder._price.setText(item.getPrice());
            viewHolder.itemView.setTag(item);

            viewHolder._btnLineChart.setOnClickListener(new OnLineChartClickListener(item.getItemId()));
        }

        @Override
        public int getItemCount() {
            return _itemList.size();
        }

        /**
         * 뷰 재활용을 위한 viewHolder
         */
        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView _img;
            private TextView _name;
            private TextView _price;
            private Button _btnLineChart;

            public ViewHolder(View itemView) {
                super(itemView);

                _img = (ImageView) itemView.findViewById(R.id.imv_item);
                _name = (TextView) itemView.findViewById(R.id.txv_item_Name);
                _price = (TextView) itemView.findViewById(R.id.txv_item_price);
                _btnLineChart = (Button) itemView.findViewById(R.id.btn_item_lineChart);
            }

        }
    }

    class BeaconRecyclerAdapter extends RecyclerView.Adapter<BeaconRecyclerAdapter.ViewHolder> {

        private List<String> _beacons;
        private int _layout;

        /**
         * 생성자
         *
         * @param beacons
         * @param layout
         */
        BeaconRecyclerAdapter(List<String> beacons, int layout) {

            _beacons = beacons;
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
            int paddingSize = (int) getResources().getDimension(R.dimen.beacon_view_holder_padding);
            view.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
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
            switch (_beacons.get(position)) {
                case Database.MAIN:
                    viewHolder._img.setImageResource(b);
                    viewHolder._img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewDiscountInfo();
                        }
                    });
                    break;
                case Database.MEAT:
                    viewHolder._img.setImageResource(R.drawable.c);
                    viewHolder._img.setOnClickListener(new OnCategoryClickListener(Database.MEAT));
                    break;
                case Database.VEGETABLE:
                    viewHolder._img.setImageResource(R.drawable.d);
                    viewHolder._img.setOnClickListener(new OnCategoryClickListener(Database.VEGETABLE));
            }
            viewHolder._textView.setText(_beacons.get(position));
        }

        @Override
        public int getItemCount() {
            return _beacons.size();
        }

        /**
         * 뷰 재활용을 위한 viewHolder
         */
        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView _img;
            private TextView _textView;

            public ViewHolder(View itemView) {
                super(itemView);
                _img = (ImageView) itemView.findViewById(R.id.imv_beacon_image);
                _textView = (TextView) itemView.findViewById(R.id.txv_beacon_text);
            }
        }

        class OnCategoryClickListener implements View.OnClickListener {

            private String _category;

            public OnCategoryClickListener(String category) {

                _category = category;
            }

            @Override
            public void onClick(View v) {
                final Database database = new Database();
                database.requestItemByCategory(_category, new Database.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        database.getItemList();
                        viewItemInfo(database.getItemList());
                    }
                });
            }
        }
    }

    class OnLineChartClickListener implements View.OnClickListener {

        String _itemId;

        public OnLineChartClickListener(String itemId) {

            _itemId = itemId;
        }

        @Override
        public void onClick(View v) {

            Log.i(LOG_TAG, "Line Chart Start...");

            Intent intent = new Intent(getApplicationContext(), LineChartActivity.class);
            intent.putExtra(EXTRA_ID, _itemId);
            startActivity(intent);

        }
    }
    //endregion
}

