package com.example.shoppingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shoppingmanager.database.Database;
import com.example.shoppingmanager.database.DiscountInfo;

import java.util.Objects;

/**
 * Created by koo on 17. 7. 17.
 */

public class EventModification extends AppCompatActivity {

    String itemId;
    EditText itemIdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_modification);

        final DiscountInfo info = (DiscountInfo) getIntent().getSerializableExtra(DiscountList.EXTRA_DISCOUNT_INFO);

        itemIdEditText = (EditText) findViewById(R.id.edt_evmod_itemId);
        final EditText discountPrice = (EditText) findViewById(R.id.edt_evmod_dcPrice);
        final EditText startDate = (EditText) findViewById(R.id.edt_evmod_startDate);
        final EditText endDate = (EditText) findViewById(R.id.edt_evmod_endDate);
        final EditText startTime = (EditText) findViewById(R.id.edt_evmod_startTime);
        final EditText endTime = (EditText) findViewById(R.id.edt_evmod_endTime);
        final EditText discountType = (EditText) findViewById(R.id.edt_evmod_dcType);
        Button commitButton = (Button) findViewById(R.id.btn_commitEventmod);

        discountPrice.setText(info.getDiscountedPrice());
        discountType.setText(info.getDiscountType());

        String startDateTime = info.getStartTime();
        String endDateTime = info.getEndTime();

        /*StringBuilder sb = new StringBuilder(startDateTime);
        sb.deleteCharAt(4);
        startDate.setText(sb.deleteCharAt(7).toString().substring(0, 6));

        StringBuilder sb2 = new StringBuilder(endDateTime);
        sb2.deleteCharAt(4);
        endDate.setText(sb2.deleteCharAt(7).toString().substring(0, 6));

        StringBuilder sb3 = new StringBuilder(startDateTime.substring(11, 16));
        sb3.deleteCharAt(2);
        startTime.setText(sb3);

        StringBuilder sb4 = new StringBuilder(endDateTime.substring(11, 16));
        sb4.deleteCharAt(2);
        endTime.setText(sb4);*/

        final Database database = new Database();

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = discountPrice.getText().toString();
                String startTimeString = buildDateTime(startDate.getText().toString(), startTime.getText().toString());
                String endTimeString = buildDateTime(endDate.getText().toString(), endTime.getText().toString());
                String dcType = discountType.getText().toString();

                if (!Objects.equals(price, "") || !Objects.equals(startTimeString, "") ||
                        !Objects.equals(endTimeString, "") || !Objects.equals(dcType, "")) {
                    database.updateDiscountInfo(
                            info.getDiscountId(), price, startTimeString, endTimeString, dcType, null);
                    Toast.makeText(EventModification.this, "수정완료", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EventModification.this, "입력하지 않은 파라미터가 있습니다.", Toast.LENGTH_SHORT).show();
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
    }
}
