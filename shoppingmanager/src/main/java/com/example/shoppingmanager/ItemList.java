package com.example.shoppingmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shoppingmanager.database.Database;
import com.example.shoppingmanager.database.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemList extends AppCompatActivity {

    public static final int REQUEST_CODE = 500;
    //todo 적절한 클래스명으로 변경
    public static final String ITEM_ID = "ItemId";
    public static final String ITEM_IMAGE = "ItemImage";

    List<Item> _list;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        _list = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        final Database database = new Database();
        database.requestAllItem(new Database.LoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                _list = database.getItemList();
                ArrayList<String> idList = new ArrayList<>();
                for (int i = 0; i < _list.size(); i++) {
                    idList.add(_list.get(i).getItemId());
                }
                database.requestImageList(idList, new Database.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        ArrayList<Bitmap> images = new ArrayList<>();
                        for (int i = 0; i < _list.size(); i++) {
                            images.add(database.getBitmap(i));
                        }
                        recyclerView.setAdapter(new ManagerRecyclerAdapter(_list, images, R.layout.card_item));
                    }
                });
            }
        });


//        for(int i=0;i<5;i++) _items.add(item[i]);

        //recyclerView.setAdapter(new MyRecyclerAdapter(getApplicationContext(),albumList,R.layout.activity_list_inquiry));
    }

    private class ManagerRecyclerAdapter extends RecyclerView.Adapter<ManagerRecyclerAdapter.ViewHolder> {

        private final ArrayList<Bitmap> _bitmapList;
        private int _layoutId;
        private List<Item> _itemList;

        public ManagerRecyclerAdapter(List<Item> itemList, ArrayList<Bitmap> bitmapList, int layoutId) {
            super();
            _layoutId = layoutId;
            _itemList = itemList;
            _bitmapList = bitmapList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(_layoutId, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final int _position = position;

            Item item = _itemList.get(position);
            holder._img.setImageBitmap(_bitmapList.get(position));
            holder._itemName.setText(item.getName());
            holder._itemPrice.setText(item.getPrice());
            holder.selection.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra(ITEM_ID, _list.get(_position).getItemId());
                            Bitmap src = _bitmapList.get(_position);
                            if (src != null) {
                                Bitmap resized = Bitmap.createScaledBitmap(src, src.getWidth() / 2, src.getHeight() / 2, true);
                                intent.putExtra(ITEM_IMAGE, resized);
                            }
                            setResult(REQUEST_CODE, intent);
                            finish();
                        }
                    }
            );
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
            private TextView _itemName;
            private TextView _itemPrice;
            private Button selection;

            public ViewHolder(View itemView) {
                super(itemView);
                _img = (ImageView) itemView.findViewById(R.id.imv_card_item);
                _itemName = (TextView) itemView.findViewById(R.id.txv_card_itemName);
                _itemPrice = (TextView) itemView.findViewById(R.id.txv_card_itemPrice);
                selection = (Button) itemView.findViewById(R.id.selection);
            }
        }
    }
}
