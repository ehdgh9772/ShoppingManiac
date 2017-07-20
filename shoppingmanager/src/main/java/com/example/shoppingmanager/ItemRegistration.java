package com.example.shoppingmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shoppingmanager.database.Category;
import com.example.shoppingmanager.database.Database;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Objects;


public class ItemRegistration extends AppCompatActivity {

    private final int PICK_FROM_ALBUM = 100;
    ImageView iv;
    private FileInputStream mFileInputStream;
    private URL connectUrl;
    private BreakIterator mEdityEntry;
    ArrayList<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_registration);

        Button searchItem = (Button) findViewById(R.id.btn_searchItemId);
        Button registration = (Button) findViewById(R.id.btn_commitItem);
        final EditText itemName = (EditText) findViewById(R.id.edt_itemName);
        final Spinner spinner = (Spinner) findViewById(R.id.spn_itemCategory);
        final EditText itemUnit = (EditText) findViewById(R.id.edt_itemUnit);
        final EditText itemPrice = (EditText) findViewById(R.id.edt_itemPrice);

        final Database database = new Database();
        categoryList = new ArrayList<>();
        setCategory();

        ArrayList<String> spinnerItemtem = new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            spinnerItemtem.add(categoryList.get(i).getName());
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItemtem);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        searchItem.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, PICK_FROM_ALBUM);
                    }
                }
        );

        registration.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = itemName.getText().toString();
                        String categoryId = categoryList.get(spinner.getSelectedItemPosition()).getId();
                        String unit = itemUnit.getText().toString();
                        String price = itemPrice.getText().toString();
                        if (!Objects.equals(name, "") && !Objects.equals(categoryId, "") &&
                                !Objects.equals(unit, "") && !Objects.equals(price, "")) {
                            database.insertItem(name, categoryId, unit, price, null);
                            Toast.makeText(ItemRegistration.this, "등록완료", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ItemRegistration.this, "입력하지 않은 파라미터가 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void setCategory() {
        Category c1 = new Category();
        c1.setId("1");
        c1.setName("육류");
        categoryList.add(c1);

        Category c2 = new Category();
        c2.setId("2");
        c2.setName("청과류");
        categoryList.add(c2);

        Category c3 = new Category();
        c3.setId("3");
        c3.setName("가전");
        categoryList.add(c3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bitmap image_bitmap = null;
            try {
                image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ImageView image = (ImageView) findViewById(R.id.imv_item_registration);
                //배치해놓은 ImageView에 set
                image.setImageBitmap(image_bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
