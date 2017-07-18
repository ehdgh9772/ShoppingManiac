package com.example.kcci.shoppingmaniac.database;

/**
 * Created by koo on 17. 7. 10.
 */

public class PriceHistory {
    //region field
    private String price;
    private String date;
    //endregion

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
