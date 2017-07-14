package com.example.shoppingmanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button itemRegisteration = (Button) findViewById(R.id.itemRegistration);
        Button eventButton = (Button) findViewById(R.id.btn_eventRegistration);
        Button listInquiry = (Button) findViewById(R.id.listInquiry);
        Button priceModification = (Button) findViewById(R.id.priceModification);

        itemRegisteration.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ItemRegistration.class);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "아이템 등록 화면으로 이동", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventRegistration.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "이벤트 등록화면으로 이동", Toast.LENGTH_SHORT).show();
            }
        });

        listInquiry.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ListInquiry.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "아이템 조회화면으로 이동", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        checkPermissions();

        priceModification.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, PriceModification.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "가격수정 화면으로 이동", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}
