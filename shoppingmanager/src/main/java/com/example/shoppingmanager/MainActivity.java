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

        Button imageShooting = (Button) findViewById(R.id.imageShooting);
        Button listInquiry = (Button) findViewById(R.id.listInquiry);
        Button registration = (Button) findViewById(R.id.registration);

//        /**
//         * 뷰 재활용을 위한 viewHolder
//         */
//        class ViewHolder extends RecyclerView.ViewHolder{
//
//            public ImageView img;
//            private TextView _discountType;
//            private TextView _name;
//            private TextView _price;
//            private TextView _discountedPrice;
//            Button _btnLineChart;
//
//            public ViewHolder(View itemView){
//                super(itemView);
//
//                img = (ImageView) itemView.findViewById(R.id.img);
//                _discountType = (TextView) itemView.findViewById(R.id.textDiscountType);
//                _name = (TextView) itemView.findViewById(R.id.textName);
//                _price = (TextView) itemView.findViewById(R.id.textPrice);
//                _discountedPrice = (TextView) itemView.findViewById(R.id.textDiscountedPrice);
//                _btnLineChart =  (Button) itemView.findViewById(R.id.btnLineChart);
//            }
//
//        }
//
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
