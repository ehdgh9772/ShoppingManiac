package com.example.shoppingmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.shoppingmanager.database.Database;

import java.util.ArrayList;
import java.util.List;

public class ListInquiry extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_inquiry);

        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        final Database database = new Database();
        database.requestDiscountInfo(new Database.LoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                database.getDiscountInfoArray();
            }
        });

        List<Item> items=new ArrayList<>();
        Item[] item=new Item[5];
//        item[0]=new Item(R.drawable.a,"#1", 2000, "1", "1");
//        item[1]=new Item(R.drawable.b,"#2");
//        item[2]=new Item(R.drawable.c,"#3");
//        item[3]=new Item(R.drawable.d,"#4");
//        item[4]=new Item(R.drawable.e,"#5");

        for(int i=0;i<5;i++) items.add(item[i]);

        //recyclerView.setAdapter(new MyRecyclerAdapter(getApplicationContext(),albumList,R.layout.activity_list_inquiry));
    }
}
