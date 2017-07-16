package com.example.shoppingmanager.database;

/**
 * Created by koo on 17. 7. 13.
 */

public class Item {

    private String _itemId;
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
