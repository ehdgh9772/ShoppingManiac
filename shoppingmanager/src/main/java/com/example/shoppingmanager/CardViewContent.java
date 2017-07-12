package com.example.shoppingmanager;

import android.graphics.Bitmap;

/**
 * Created by CHJ on 2017-07-06.
 */

public class CardViewContent {

    private String _discountType;
    private String _name;
    private String _price;
    private String _discountedPrice;
    private Bitmap _image;

    public Bitmap getImage() {
        return _image;
    }

    public void setImage(Bitmap image) {
        _image = image;
    }

    public String getDiscountType() {
        return _discountType;
    }

    public void setDiscountType(String discountType) {
        _discountType = discountType;
    }

    public String getDiscountedPrice() {
        return _discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        _discountedPrice = discountedPrice;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getPrice() {
        return _price;
    }

    public void setPrice(String price) {
        this._price = price;
    }
}
