package com.example.shoppingmanager.database;

/**
 * Created by koo on 17. 7. 6.
 */

public class DiscountInfo {
    private String itemId;
    private String name;
    private String discountType;
    private String price;
    private String discountedPrice;
    private String category;
    private String startTime;
    private String endTime;

    //region getter & setter
    public String getItemId() {
        return itemId;
    }

    void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getDiscountType() {
        return discountType;
    }

    void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getPrice() {
        return price;
    }

    void setPrice(String price) {
        this.price = price;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getCategory() {
        return category;
    }

    void setCategory(String category) {
        this.category = category;
    }

    public String getStartTime() {
        return startTime;
    }

    void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    //endregion
}
