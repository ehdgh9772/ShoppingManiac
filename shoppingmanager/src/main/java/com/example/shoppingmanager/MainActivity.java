package com.example.shoppingmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button imageShooting = (Button) findViewById(R.id.imageShooting);
        Button listInquiry = (Button) findViewById(R.id.listInquiry);
        Button registration = (Button) findViewById(R.id.registration);

        imageShooting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(MainActivity.this,ImageShooting.class);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "촬영 화면으로 이동", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        listInquiry.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(MainActivity.this,ListInquiry.class);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "목록 조회화면으로 이동", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        registration.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }
        );
    }
}
