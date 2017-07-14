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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView _recyclerView;
    private View btnImgDrawerView;                            //항상 보이게 할 뷰
    private WindowManager.LayoutParams _params;  //layout params 객체. 뷰의 위치 및 크기
    private WindowManager _windowManager;          //윈도우 매니저
    private DrawerLayout _drawerLayout;
    private View _drawerView;
    boolean isPageSlided = false;

    Animation translateBottomAnimaion;
    Animation translateTopAnimaion;
    LinearLayout slideLayout;

    public static String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slideLayout = (LinearLayout) findViewById(R.id.hiddenLayout);
        _recyclerView = (RecyclerView)findViewById(R.id.recyclerViewMain);





        initLayout();

        btnImgDrawerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isPageSlided) {
                    slideLayout.startAnimation(translateBottomAnimaion);
                } else {
                    Log.i(LOG_TAG, "slide animation is on");
                    slideLayout.setVisibility(View.VISIBLE);
                    slideLayout.startAnimation(translateTopAnimaion);
                }
            }
        });
        viewDiscountInfo();
        viewItemInfo();
    }

    /**
     * 레이아웃 초기화
     */

    private void initLayout() {
        translateBottomAnimaion = AnimationUtils.loadAnimation(this, R.anim.translate_from_bottom);
        translateTopAnimaion = AnimationUtils.loadAnimation(this, R.anim.translate_to_bottom);

        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();
        translateBottomAnimaion.setAnimationListener(animationListener);
        translateTopAnimaion.setAnimationListener(animationListener);

        btnImgDrawerView = findViewById(R.id.btnDrawer);
        btnImgDrawerView.bringToFront();
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

    private class SlidingPageAnimationListener implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(isPageSlided)  slideLayout.setVisibility(View.INVISIBLE);
            isPageSlided = !isPageSlided;
            Log.i(LOG_TAG, "animation terminated isPageSlided is : " + isPageSlided);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    //This is a default proximity uuid of the RECO
    public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";


    public static final boolean SCAN_RECO_ONLY = true;


    public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;


    public static final boolean DISCONTINUOUS_SCAN = false;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION = 10;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private View mLayout;


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
    }

}

