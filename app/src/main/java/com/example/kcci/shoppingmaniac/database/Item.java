package com.example.kcci.shoppingmaniac.database;

/**
 * Created by koo on 17. 7. 13.
 */

public class Item {
    private String _categoryId;
    private String _name;
    private String _unit;
    private String _price;

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
