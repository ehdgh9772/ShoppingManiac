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

public class EventRegistration extends AppCompatActivity {

    String itemId;
    EditText itemIdEditText;
    private ImageView _imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        itemIdEditText = (EditText) findViewById(R.id.edt_itemId);
        final EditText discountPrice = (EditText) findViewById(R.id.edt_dcPrice);
        final EditText startDate = (EditText) findViewById(R.id.edt_startDate);
        final EditText endDate = (EditText) findViewById(R.id.edt_endDate);
        final EditText startTime = (EditText) findViewById(R.id.edt_startTime);
        final EditText endTime = (EditText) findViewById(R.id.edt_endTime);
        final EditText discountType = (EditText) findViewById(R.id.edt_dcType);
        Button searchItemIdButton = (Button) findViewById(R.id.btn_searchItemId);
        Button commitButton = (Button) findViewById(R.id.btn_commitEvent);
        _imageView = (ImageView) findViewById(R.id.imv_event_registration);

        final Database database = new Database();

        searchItemIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ItemList.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemId = itemIdEditText.getText().toString();
                String price = discountPrice.getText().toString();
                String startTimeString = buildDateTime(startDate.getText().toString(), startTime.getText().toString());
                String endTimeString = buildDateTime(endDate.getText().toString(), endTime.getText().toString());
                String dcType = discountType.getText().toString();

                if(!Objects.equals(itemId, "") || !Objects.equals(price, "") ||
                        !Objects.equals(startTimeString, "") || !Objects.equals(endTimeString, "") ||
                        !Objects.equals(dcType, "")) {
                    database.insertDiscountInfo(
                            itemId, price, startTimeString, endTimeString, dcType, null);
                    Toast.makeText(EventRegistration.this, "등록완료", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EventRegistration.this, "입력하지 않은 파라미터가 있습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });//todo 커밋 완성하기
    }

    private String buildDateTime(String date, String time) {
        return date + time + "00";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String ItemId = data.getStringExtra(ItemList.ITEM_ID);
        itemIdEditText.setText(ItemId);
        _imageView.setImageBitmap((Bitmap) data.getExtras().get(ItemList.ITEM_IMAGE));
    }
}
