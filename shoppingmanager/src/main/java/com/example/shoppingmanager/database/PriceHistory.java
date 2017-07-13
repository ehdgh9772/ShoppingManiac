package com.example.shoppingmanager.database;

/**
 * Created by koo on 17. 7. 10.
 */

public class PriceHistory {
    private String price;
    private String date;

    //region Getter & Setter
    public String getPrice() {
        return price;
    }

    void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }
    //endregion
}
