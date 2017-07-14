package com.example.shoppingmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shoppingmanager.database.Database;

import java.io.FileInputStream;
import java.net.URL;
import java.text.BreakIterator;


public class ItemRegistration extends AppCompatActivity {

    ImageView iv;
    private FileInputStream mFileInputStream;
    private URL connectUrl;
    private BreakIterator mEdityEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_registration);

        Button imageShooting = (Button) findViewById(R.id.btn_searchItemId);
        Button listInquiry = (Button) findViewById(R.id.listInquiry);
        Button registration = (Button) findViewById(R.id.btn_commitItem);
        EditText itemName = (EditText) findViewById(R.id.edt_itemName);
        EditText itemCategoryId = (EditText) findViewById(R.id.edt_itemCategoryId);
        EditText itemUnit = (EditText) findViewById(R.id.edt_itemUnit);

        final String item_name = itemName.getText().toString();
        final String item_categoryId = itemCategoryId.getText().toString();
        final String item_unit = itemUnit.getText().toString();

        final Database database = new Database();

        imageShooting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, 1);

                        Toast.makeText(getApplicationContext(), "촬영 화면으로 이동", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        registration.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        database.insertItem(item_name, item_categoryId, item_unit,
                                new Database.LoadCompleteListener(){
                                    @Override
                                    public void onLoadComplete() {

                                    }
                                });
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selPhotoUri = data.getData();

    }

}
