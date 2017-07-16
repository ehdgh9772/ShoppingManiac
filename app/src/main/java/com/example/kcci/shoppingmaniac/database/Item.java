package com.example.kcci.shoppingmaniac.database;

import android.graphics.Bitmap;

/**
 * Created by koo on 17. 7. 13.
 */

public class Item {

    private String _itemId;
    private Bitmap _image;
    private String _categoryId;
    private String _name;
    private String _unit;
    private String _price;

    public String getItemId() {
        return _itemId;
    }

    public void setItemId(String itemId) {
        _itemId = itemId;
    }

    public Bitmap getImage() {
        return _image;
    }

    public void setImage(Bitmap image) {
        _image = image;
    }

    public String getCategoryId() {
        return _categoryId;
    }

    void setCategoryId(String categoryId) {
        _categoryId = categoryId;
    }

    public String getName() {
        return _name;
    }

    void setName(String name) {
        _name = name;
    }

    public String getUnit() {
        return _unit;
    }

    void setUnit(String unit) {
        _unit = unit;
    }

    public String getPrice() {
        return _price;
    }

    void setPrice(String price) {
        _price = price;
    }
}
