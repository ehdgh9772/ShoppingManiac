package com.example.shoppingmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shoppingmanager.database.Database;
import com.example.shoppingmanager.database.DiscountInfo;

import java.util.ArrayList;
import java.util.List;

public class DiscountList extends AppCompatActivity{

    public static final String EXTRA_IMAGE = "Image";
    public static final String EXTRA_DISCOUNT_INFO = "DiscountInfo";
    RecyclerView _recyclerView;
    ArrayList<DiscountInfo> _discountInfoList;
    private ArrayList<String> _itemIdList;
    private ArrayList<Bitmap> _images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        _recyclerView=(RecyclerView)findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

//        recyclerView.setAdapter(new MyRecyclerAdapter(_items,R.layout.activity_list_inquiry));
        _recyclerView.setHasFixedSize(true);
        _recyclerView.setLayoutManager(layoutManager);

        viewDiscountInfo();
//        for(int i=0;i<5;i++) _items.add(item[i]);

        //recyclerView.setAdapter(new MyRecyclerAdapter(getApplicationContext(),albumList,R.layout.activity_list_inquiry));
    }

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
            final Bitmap bitmap = _imageList.get(position);
            viewHolder._discountType.setText(item.getDiscountType());
            viewHolder._name.setText(item.getName());
            viewHolder._price.setText(item.getPrice());
            viewHolder._discountedPrice.setText(item.getDiscountedPrice());
            viewHolder._img.setImageBitmap(bitmap);
            viewHolder.itemView.setTag(item);
            viewHolder._startTime.setText(item.getStartTime());
            viewHolder._endTime.setText(item.getEndTime());
            viewHolder._btnLineChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(EXTRA_DISCOUNT_INFO, item);
                    Intent intent = new Intent(DiscountList.this, EventModification.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
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
            private TextView _startTime;
            private TextView _endTime;
            private Button _btnLineChart;

            public ViewHolder(View itemView) {
                super(itemView);

                _img = (ImageView) itemView.findViewById(R.id.imv_discount);
                _discountType = (TextView) itemView.findViewById(R.id.txv_discount_dcType);
                _name = (TextView) itemView.findViewById(R.id.txv_discount_Name);
                _price = (TextView) itemView.findViewById(R.id.txv_item_price);
                _discountedPrice = (TextView) itemView.findViewById(R.id.txv_discount_dcPrice);
                _startTime = (TextView) itemView.findViewById(R.id.txv_discount_startDate);
                _endTime = (TextView) itemView.findViewById(R.id.txv_discount_endDate);
                _btnLineChart = (Button) itemView.findViewById(R.id.btn_discount_lineChart);
            }

        }
    }
}
