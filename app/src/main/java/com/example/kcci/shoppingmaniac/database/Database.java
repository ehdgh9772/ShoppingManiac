package com.example.kcci.shoppingmaniac.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
/**
 * Created by koo on 17. 7. 6.
 */

public class Database {

    //region Final Values
    public static final String MAIN = "0";
    public static final String MEAT = "1";
    public static final String VEGETABLE = "2";
    public static final String HOME_APPLIENCE = "3";

    public static final String GET_DISCOUNT_INFO = "GetDiscountInfo";
    public static final String GET_PRICE_HISTORY = "GetPriceHistory";
    public static final String GET_ITEM_BY_CATEGORY = "GetItemByCategory";
    public static final String GET_ALL_ITEM = "GetAllItem";
    public static final String GET_ALL_BEACON = "GetAllBeacon";
    public static final String GET_BEACON_BY_CATEGORY = "GetBeaconByCategory";
    public static final String GET_BEACON_BY_BEACON_ID = "GetBeaconByBeaconId";
    public static final String GET_CATEGORY = "GetCategory"; //ToDo : GetCategory 추가

    private final int TYPE_NONE = 0;
    private final int TYPE_JSON = 1;
    private final int TYPE_IMAGE = 2;
    private final String LOG_CLASS = "Database";
    //endregion

    //region Fields
    private ArrayList<DiscountInfo> _discountInfoList;
    private ArrayList<PriceHistory> _priceHistoryList;
    private ArrayList<Item> _itemList;
    private ArrayList<Bitmap> _bitmapList = new ArrayList<>();
    private ArrayList<Beacon> _beaconList = new ArrayList<>();
    //endregion

    //region Requests

    public void requestDiscountInfo(LoadCompleteListener loadCompleteListener) {
        scrap(TYPE_JSON, GET_DISCOUNT_INFO, loadCompleteListener);
        Log.i(LOG_CLASS, "requested");
    }

    public void requestImageFromIndex(int index, LoadCompleteListener loadCompleteListener) {
        scrap(TYPE_IMAGE,
                "images/" + _discountInfoList.get(index).getItemId() + ".png",
                loadCompleteListener);
    }

    public void requestImageList(ArrayList<String> itemIdList, LoadCompleteListener loadCompleteListener) {
        for (int i = 0; i < itemIdList.size(); i++) {
            if (i == itemIdList.size() - 1) {
                requestImage(itemIdList.get(i), loadCompleteListener);
            } else {
                requestImage(itemIdList.get(i), null);
            }
        }
    }

    public void requestImage(String itemId, LoadCompleteListener loadCompleteListener) {
        scrap(TYPE_IMAGE,
                "images/" + itemId + ".png",
                loadCompleteListener);
    }

    public void requestPriceHistory(ArrayList<DiscountInfo> itemList, int index, LoadCompleteListener loadCompleteListener) {
        scrap(TYPE_JSON, GET_PRICE_HISTORY, loadCompleteListener,
                String.valueOf(itemList.get(index).getItemId()));
    }

    public void requestPriceHistory(String itemId, LoadCompleteListener loadCompleteListener) {
        scrap(TYPE_JSON, GET_PRICE_HISTORY, loadCompleteListener,
                String.valueOf(itemId));
    }

    public void requestItemByCategory(String category, LoadCompleteListener loadCompleteListener) {
        scrap(TYPE_JSON, GET_ITEM_BY_CATEGORY, loadCompleteListener, String.valueOf(category));
    }

    public void requestAllItem(LoadCompleteListener loadCompleteListener) {
        scrap(TYPE_JSON, GET_ALL_ITEM, loadCompleteListener);
    }

    public void requestAllBeacon(LoadCompleteListener loadCompleteListener) {
        scrap(TYPE_JSON, GET_ALL_BEACON, loadCompleteListener);
    }

    public void requestBeaconByCategory(String category, LoadCompleteListener loadCompleteListener) {
        scrap(TYPE_JSON, GET_BEACON_BY_CATEGORY, loadCompleteListener, category);
    }

    public void requestBeaconByBeaconId(String category, LoadCompleteListener loadCompleteListener) {
        scrap(TYPE_JSON, GET_BEACON_BY_BEACON_ID, loadCompleteListener, category);
    }

    //endregion

    //region Scrapper
    private void scrap(int scrapType, final String url,
                       final LoadCompleteListener loadCompleteListener, String... args) {

        final String baseUrl = "http://server.raystar.kro.kr:3030/";
        final String LOG = "webScrapper";

        class JSONScrapper extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];

                BufferedReader bufferedReader;
                try {
                    URL url = new URL(baseUrl + uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json).append("\n");
                    }
                    Log.i(LOG, "Downloading");
                    return sb.toString().trim();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }

            protected void onPostExecute(String str) {              //Stored Procedure 추가시 이 부분에 추가
                Log.i(LOG, "Posting");

                try {
                    switch (url) {
                        case GET_DISCOUNT_INFO:
                            setDiscountInfoList(parseToJSON(str));
                            break;
                        case GET_PRICE_HISTORY:
                            setPriceHistoryList(parseToJSON(str));
                            break;
                        case GET_ITEM_BY_CATEGORY:
                            setItemList(parseToJSON(str), GET_ITEM_BY_CATEGORY);
                            break;
                        case GET_ALL_ITEM:
                            setItemList(parseToJSON(str), GET_ALL_ITEM);
                            break;
                        case GET_ALL_BEACON:
                            setBeaconList(parseToJSON(str), GET_ALL_BEACON);
                            break;
                        case GET_BEACON_BY_BEACON_ID:
                            setBeaconList(parseToJSON(str), GET_BEACON_BY_BEACON_ID);
                            break;
                        case GET_BEACON_BY_CATEGORY:
                            setBeaconList(parseToJSON(str), GET_BEACON_BY_CATEGORY);
                            break;
                    }

                    if (loadCompleteListener != null)
                        loadCompleteListener.onLoadComplete();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            private JSONObject parseToJSON(String result) {
                try {
                    return new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return new JSONObject();
                }
            }
        }

        class ImageScrapper extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... params) {
                String uri = params[0];

                try {
                    URL url = new URL(baseUrl + uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    return BitmapFactory.decodeStream(con.getInputStream());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }

            protected void onPostExecute(Bitmap bitmap) {
                Log.i(LOG, "Posting");
                try {
                    setBitmap(bitmap);
                    loadCompleteListener.onLoadComplete();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }

        if (scrapType == TYPE_JSON || scrapType == TYPE_NONE) {
            JSONScrapper jsonScrapper = new JSONScrapper();
            jsonScrapper.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, urlBuilder(url, args));
        } else if (scrapType == TYPE_IMAGE) {
            ImageScrapper imageScrapper = new ImageScrapper();
            imageScrapper.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
        }
    }

    private String urlBuilder(String url, String[] args) {
        if (args.length > 0)
            url += "?";
        for (int i = 0; i < args.length; i++) {
            url += "arg" + i + "=" + args[i];
        }
        return url;
    }
    //endregion

    //region Setter
    private void setDiscountInfoList(JSONObject json) {
        try {
            JSONArray jsArray = json.getJSONArray(GET_DISCOUNT_INFO);
            _discountInfoList = new ArrayList<>();
            for (int i = 0; i < jsArray.length(); i++) {
                JSONObject jsonObj = jsArray.getJSONObject(i);
                DiscountInfo discountInfo = new DiscountInfo();
                discountInfo.setDiscountId(jsonObj.getString("DiscountId"));
                discountInfo.setItemId(jsonObj.getString("ItemId"));
                discountInfo.setName(jsonObj.getString("Name"));
                discountInfo.setDiscountType(jsonObj.getString("DiscountType"));
                discountInfo.setPrice(jsonObj.getString("Price"));
                discountInfo.setDiscountedPrice(jsonObj.getString("DiscountedPrice"));
                discountInfo.setCategory(jsonObj.getString("Category"));
                discountInfo.setStartTime(convertDateTime(jsonObj.getString("StartTime")));
                discountInfo.setEndTime(convertDateTime(jsonObj.getString("EndTime")));

                Log.i("setDCInfo", discountInfo.getStartTime());
                _discountInfoList.add(discountInfo);
                Log.i("tag", "put on array");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPriceHistoryList(JSONObject json) {
        try {
            JSONArray jsArray = json.getJSONArray(GET_PRICE_HISTORY);
            _priceHistoryList = new ArrayList<>();
            for (int i = 0; i < jsArray.length(); i++) {
                JSONObject jsonObj = jsArray.getJSONObject(i);
                PriceHistory priceHistory = new PriceHistory();
                priceHistory.setDate(jsonObj.getString("Date"));
                priceHistory.setPrice(jsonObj.getString("Price"));

                _priceHistoryList.add(priceHistory);
                Log.i("tag", "put on array");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setItemList(JSONObject json, String getType) {
        try {
            JSONArray jsArray = json.getJSONArray(getType);
            _itemList = new ArrayList<>();
            for (int i = 0; i < jsArray.length(); i++) {
                JSONObject jsonObj = jsArray.getJSONObject(i);
                Item item = new Item();
                item.setItemId(jsonObj.getString("ItemId"));
                item.setCategoryId(jsonObj.getString("CategoryId"));
                item.setPrice(jsonObj.getString("Price"));
                item.setName(jsonObj.getString("Name"));
                item.setUnit(jsonObj.getString("Unit"));

                _itemList.add(item);
                Log.i("tag", "put on array");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBeaconList(JSONObject json, String getType) {
        try {
            JSONArray jsArray = json.getJSONArray(getType);
            _beaconList = new ArrayList<>();
            for (int i = 0; i < jsArray.length(); i++) {
                JSONObject jsonObj = jsArray.getJSONObject(i);
                Beacon beacon = new Beacon();

                beacon.setBeaconId(jsonObj.getString("BeaconId"));
                beacon.setName(jsonObj.getString("Name"));

                _beaconList.add(beacon);
            }
            Log.i("tag", "put on array");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBitmap(Bitmap bitmap) {
        _bitmapList.add(bitmap);
    }

    private String convertDateTime(String dateTime) {
        return dateTime.replace("T", "/").replace(dateTime.substring(16), "");
    }
    //endregion

    //region Getter

    public ArrayList<DiscountInfo> getDiscountInfoList() {
        return _discountInfoList;
    }

    public ArrayList<PriceHistory> getPriceHistoryList() {
        return _priceHistoryList;
    }

    public Bitmap getBitmap(int index) {
        return _bitmapList.get(index);
    }

    public ArrayList<Bitmap> getBitmapList() {
        return _bitmapList;
    }

    public ArrayList<Item> getItemList() {
        return _itemList;
    }

    public ArrayList<Beacon> getBeaconList() {
        return _beaconList;
    }

    //endregion

    //region LoadCompleteListener
    private LoadCompleteListener _loadCompleteListener;

    public interface LoadCompleteListener {
        void onLoadComplete();

    }

    public void setLoadCompleteListener(LoadCompleteListener loadCompleteListener) {
        _loadCompleteListener = loadCompleteListener;
        Log.i(LOG_CLASS, "Listener Setted");
    }
    //endregion
}
