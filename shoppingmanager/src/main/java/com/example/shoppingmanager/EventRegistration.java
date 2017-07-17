package com.example.shoppingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shoppingmanager.database.Database;

public class EventRegistration extends AppCompatActivity {
    String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        final EditText itemId = (EditText) findViewById(R.id.edt_itemId);
        final EditText discountPrice = (EditText) findViewById(R.id.edt_dcPrice);
        final EditText startTime = (EditText) findViewById(R.id.edt_startTime);
        final EditText endTime = (EditText) findViewById(R.id.edt_endTime);
        final EditText discountType = (EditText) findViewById(R.id.edt_dcType);
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

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventRegistration.this, "등록완료", Toast.LENGTH_SHORT).show();
                database.insertDiscountInfo(itemId.getText().toString(),
                        discountPrice.getText().toString(),
                        startTime.getText().toString(),
                        endTime.getText().toString(),
                        discountType.getText().toString(),
                        new Database.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {

                    }
                });
            }
        });//todo 커밋 완성하기
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(data.getData());
        itemId = data.getDataString();
    }
}
