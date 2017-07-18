package com.example.kcci.shoppingmaniac.database;

import android.graphics.Bitmap;

/**
 * Created by koo on 17. 7. 6.
 */

public class DiscountInfo {
    //region field
    private Bitmap _image;
    private String _discountId;
    private String _itemId;
    private String _name;
    private String _discountType;
    private String _price;
    private String _discountedPrice;
    private String _category;
    private String _startTime;
    private String _endTime;
    //endregion

    //region getter & setter
    public String getDiscountId() {
        return _discountId;
    }

    void setDiscountId(String discountId) {
        _discountId = discountId;
    }

    public String getItemId() {
        return _itemId;
    }

    void setItemId(String itemId) {
        this._itemId = itemId;
    }

    public String getName() {
        return _name;
    }

    void setName(String name) {
        this._name = name;
    }

    public String getDiscountType() {
        return _discountType;
    }

    void setDiscountType(String discountType) {
        this._discountType = discountType;
    }

    public String getPrice() {
        return _price;
    }

    void setPrice(String price) {
        this._price = price;
    }

    public String getDiscountedPrice() {
        return _discountedPrice;
    }

    void setDiscountedPrice(String discountedPrice) {
        this._discountedPrice = discountedPrice;
    }

    public String getCategory() {
        return _category;
    }

    void setCategory(String category) {
        this._category = category;
    }

    public String getStartTime() {
        return _startTime;
    }

    void setStartTime(String startTime) {
        this._startTime = startTime;
    }

    public String getEndTime() {
        return _endTime;
    }

    void setEndTime(String endTime) {
        this._endTime = endTime;
    }

    public Bitmap getImage() {
        return _image;
    }

    public void setImage(Bitmap image) {
        _image = image;
    }
    //endregion
}
