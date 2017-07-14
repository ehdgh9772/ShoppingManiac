package com.example.shoppingmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.shoppingmanager.database.Database;

public class PriceModification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_modification);

        EditText itemId = (EditText) findViewById(R.id.edt_itemId);
        EditText selectedDate = (EditText) findViewById(R.id.edt_selectedDate);
        EditText registrationPrice = (EditText) findViewById(R.id.edt_registrationPrice);
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

        final String item = itemId.getText().toString();
        final String date = selectedDate.getText().toString();
        final String price = registrationPrice.getText().toString();

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.insertPrice(item, date, price,
                        new Database.LoadCompleteListener() {
                            @Override
                            public void onLoadComplete() {

                            }
                        });
            }
        });
    }
}
