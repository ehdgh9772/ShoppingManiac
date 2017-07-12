package com.example.kcci.shoppingmaniac;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by CHJ on 2017-07-06.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private List<CardViewContent> albumList;
    private int itemLayout;

    /**
     * 생성자
     * @param items
     * @param itemLayout
     */
    public MyRecyclerAdapter(List<CardViewContent> items , int itemLayout){

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

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    /**
     * 뷰 재활용을 위한 viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView img;
        private TextView _discountType;
        private TextView _name;
        private TextView _price;
        private TextView _discountedPrice;

        public ViewHolder(View itemView){
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.img);
            _discountType = (TextView) itemView.findViewById(R.id.textDiscountType);
            _name = (TextView) itemView.findViewById(R.id.textName);
            _price = (TextView) itemView.findViewById(R.id.textPrice);
            _discountedPrice = (TextView) itemView.findViewById(R.id.textDiscountedPrice);
        }

    }
}
