package com.example.shoppingmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shoppingmanager.database.Database;

import java.util.Objects;

import static com.example.shoppingmanager.ItemList.REQUEST_CODE;

public class PriceRegistration extends AppCompatActivity {

    EditText itemIdEditText;
    ImageView _imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_modification);

        itemIdEditText = (EditText) findViewById(R.id.edt_itemId);
        final EditText selectedDate = (EditText) findViewById(R.id.edt_selectedDate);
        final EditText registrationPrice = (EditText) findViewById(R.id.edt_registrationPrice);
        Button searchItemIdButton = (Button) findViewById(R.id.btn_searchItemId);
        Button commitButton = (Button) findViewById(R.id.btn_commitEvent);
        _imageView = (ImageView) findViewById(R.id.imv_event_registration);

        final Database database = new Database();

        searchItemIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ItemList.class);
                startActivityForResult(intent, REQUEST_CODE);
                //intent.getExtras().getString("itemId");
            }
        });

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = selectedDate.getText().toString();
                String price = registrationPrice.getText().toString();

                if(!Objects.equals(date, "") && !Objects.equals(price, "")) {
                    database.insertPrice(itemIdEditText.getText().toString()
                            , date
                            , price,
                            new Database.LoadCompleteListener() {
                                @Override
                                public void onLoadComplete() {

                                }
                            });
                    Toast.makeText(PriceRegistration.this, "입력 완료!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(PriceRegistration.this, "입력하지 않은 파라미터가 있습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            String ItemId = data.getStringExtra(ItemList.ITEM_ID);
            itemIdEditText.setText(ItemId);
            _imageView.setImageBitmap((Bitmap) data.getExtras().get(ItemList.ITEM_IMAGE));
        } catch(Exception e) {
            _imageView.setImageResource(R.drawable.no_image);
        }

    }
}
