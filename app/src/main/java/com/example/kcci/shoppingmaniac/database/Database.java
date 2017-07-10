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
import java.util.Objects;

/**
 * Created by koo on 17. 7. 6.
 */

public class Database {
    private final int EXTRA_JSON = 1;
    private final int EXTRA_IMAGE = 2;

    private final String DISCOUNT_INFO = "discountinfo";
    private final String PRICE_HISTORY = "pricehistory";
    private final String LOG = "Database";

    public void requestDiscountInfo(LoadCompleteListener loadCompleteListener) {
        scrap(EXTRA_JSON, DISCOUNT_INFO, loadCompleteListener);
        Log.i(LOG, "requested");
    }

    public void requestImage(int index, LoadCompleteListener loadCompleteListener){
        scrap(EXTRA_IMAGE,
                "images/" + _discountInfoArray.get(index).itemId + ".png",
                loadCompleteListener);
    }

    public void requestPriceHistory(int index, LoadCompleteListener loadCompleteListener){
        scrap(EXTRA_JSON,
                PRICE_HISTORY + "?arg0=" + _discountInfoArray.get(index).itemId,
                loadCompleteListener);
    }

    private ArrayList<DiscountInfo> _discountInfoArray;

    public ArrayList<DiscountInfo> getDiscountInfoArray() {
        return _discountInfoArray;
    }

    private void setDiscountInfoArray(JSONObject json) {
        try {
            JSONArray jsArray = json.getJSONArray(DISCOUNT_INFO);
            _discountInfoArray = new ArrayList<>();
            for (int i = 0; i < jsArray.length(); i++) {
                JSONObject c = jsArray.getJSONObject(i);
                DiscountInfo discountInfo = new DiscountInfo();
                discountInfo.itemId = c.getString("ItemId");
                discountInfo.name = c.getString("Name");
                discountInfo.discountType = c.getString("DiscountType");
                discountInfo.price = c.getString("Price");
                discountInfo.discountedPrice = c.getString("DiscountedPrice");
                discountInfo.category = c.getString("Category");
                discountInfo.startTime = c.getString("StartTime");
                discountInfo.endTime = c.getString("EndTime");

                _discountInfoArray.add(discountInfo);
                Log.i("tag", "put on array");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<PriceHistory> _priceHistoryArray;

    public ArrayList<PriceHistory> getPriceHistoryArray() {
        return _priceHistoryArray;
    }

    private void setPriceHistoryArray(JSONObject json) {
        try {
            JSONArray jsArray = json.getJSONArray(PRICE_HISTORY);
            _priceHistoryArray = new ArrayList<>();
            for (int i = 0; i < jsArray.length(); i++) {
                JSONObject c = jsArray.getJSONObject(i);
                PriceHistory priceHistory = new PriceHistory();
                priceHistory.date = c.getString("Date");
                priceHistory.price = c.getString("Price");

                _priceHistoryArray.add(priceHistory);
                Log.i("tag", "put on array");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Bitmap> _bitmapArray = new ArrayList<>();

    public Bitmap getBitmap(int index){
        return _bitmapArray.get(index);
    }

    public ArrayList<Bitmap> getBitmapArray() {
        return _bitmapArray;
    }

    public void setBitmap(Bitmap bitmap) {
        _bitmapArray.add(bitmap);
    }

    //region LoadCompleteListener
    private LoadCompleteListener _loadCompleteListener;

    public interface LoadCompleteListener {
        void onLoadComplete();
    }

    public void setLoadCompleteListener(LoadCompleteListener loadCompleteListener) {
        _loadCompleteListener = loadCompleteListener;
        Log.i(LOG, "Listener Setted");
    }
    //endregion

    private void scrap(int scrapType, final String url, final LoadCompleteListener loadCompleteListener) {

        final String protocol = "http://server.raystar.kro.kr:3030/";
        final String LOG = "webScrapper";

        class JSONScrapper extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];

                BufferedReader bufferedReader;
                try {
                    URL url = new URL(protocol + uri);
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
                    return "{}";
                }
            }

            protected void onPostExecute(String str) {
                Log.i(LOG, "Posting");
                if (Objects.equals(url, DISCOUNT_INFO))
                    setDiscountInfoArray(parseToJSON(str));
                else if (Objects.equals(url, PRICE_HISTORY))
                    setPriceHistoryArray(parseToJSON(str));

                loadCompleteListener.onLoadComplete();
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

                BufferedReader bufferedReader;
                try {
                    URL url = new URL(protocol + uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    return BitmapFactory.decodeStream(con.getInputStream());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }

            protected void onPostExecute(Bitmap bitmap) {
                Log.i(LOG, "Posting");
                setBitmap(bitmap);
                loadCompleteListener.onLoadComplete();
            }
        }

        if(scrapType == EXTRA_JSON){
            JSONScrapper g = new JSONScrapper();
            g.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
        }

        else if(scrapType == EXTRA_IMAGE){
            ImageScrapper i = new ImageScrapper();
            i.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
        }
    }
}
