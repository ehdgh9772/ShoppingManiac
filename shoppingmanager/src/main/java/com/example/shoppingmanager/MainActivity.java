package com.example.shoppingmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingmanager.database.Database;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button itemRegisteration = (Button) findViewById(R.id.itemRegistration);
        Button listInquiry = (Button) findViewById(R.id.listInquiry);

        itemRegisteration.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(MainActivity.this,ItemRegistration.class);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "아이템 등록 화면으로 이동", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        listInquiry.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(MainActivity.this,ListInquiry.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "아이템 조회화면으로 이동", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }
}
