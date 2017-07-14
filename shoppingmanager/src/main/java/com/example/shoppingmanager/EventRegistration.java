package com.example.shoppingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.shoppingmanager.database.Database;

public class EventRegistration extends AppCompatActivity {

    String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);


        EditText discountedPriceText = (EditText) findViewById(R.id.edt_dcPrice);
        EditText startTimeText = (EditText) findViewById(R.id.edt_startTime);
        EditText endTimeText = (EditText) findViewById(R.id.edt_endTime);
        EditText discountTypeText = (EditText) findViewById(R.id.edt_dcType);
        Button searchItemIdButton = (Button) findViewById(R.id.btn_searchItemId);
        Button commitButton = (Button) findViewById(R.id.btn_commitEvent);

        final Database database = new Database();

        searchItemIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchItem.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });

        final String discountPrice = discountedPriceText.getText().toString();
        final String startTime = startTimeText.getText().toString();
        final String endTime = endTimeText.getText().toString();
        final String discountType = discountTypeText.getText().toString();


        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.insertDiscountInfo(itemId, discountPrice, startTime, endTime, discountType,
                        new Database.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(data.getData());
        itemId = data.getDataString();
    }


}
