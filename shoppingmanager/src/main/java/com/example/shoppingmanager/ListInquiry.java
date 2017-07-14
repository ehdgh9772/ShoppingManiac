package com.example.shoppingmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shoppingmanager.database.Database;
import com.example.shoppingmanager.database.Item;

import java.util.ArrayList;
import java.util.List;

public class ListInquiry extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());

        ArrayList<Item> items=new ArrayList<>();

        items.add(new Item());

//        recyclerView.setAdapter(new MyRecyclerAdapter(_items,R.layout.activity_list_inquiry));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        List<CardViewContent> list = new ArrayList<>();
        list.add(new CardViewContent());
        recyclerView.setAdapter(new ManagerRecyclerAdapter(list, R.layout.card_discount));

        final Database database = new Database();
        database.requestDiscountInfo(new Database.LoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                database.getDiscountInfoList();
            }
        });
//        for(int i=0;i<5;i++) _items.add(item[i]);

        //recyclerView.setAdapter(new MyRecyclerAdapter(getApplicationContext(),albumList,R.layout.activity_list_inquiry));
    }

    private class ManagerRecyclerAdapter extends RecyclerView.Adapter<ManagerRecyclerAdapter.ViewHolder> {

        int _layoutId;
        List<CardViewContent> _itemList;

        public ManagerRecyclerAdapter(List<CardViewContent> itemList, int layoutId) {
            super();
            _layoutId = layoutId;
            _itemList = itemList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(_layoutId,parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            CardViewContent item = _itemList.get(position);
        }

        @Override
        public int getItemCount() {
            return _itemList.size();
        }
        /**
         * 뷰 재활용을 위한 viewHolder
         */
        class ViewHolder extends RecyclerView.ViewHolder{

//        public ImageView img;
//        public TextView textTitle;

            public ViewHolder(View itemView){
                super(itemView);
                TextView textView = (TextView) findViewById(R.id.textDiscountType);

            }

        }

    }
}
