package com.example.kcci.shoppingmaniac.database;

import android.os.AsyncTask;
import android.util.Log;

import com.example.kcci.shoppingmaniac.type.*;

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

    private static final String DISCOUNT_INFO = "discountinfo";
    private static final String LOG = "Database";

    public void request(String requestUrl) {
        scrap(requestUrl);
        Log.i(LOG, "requested");
    }
    //TODO GET방식 요청 오버로딩
   /* public void request(String requestUrl, String... params){
        StringBuilder builder = new StringBuilder(_protocol);
        builder.append(requestUrl).append("?");
        for (int i = 0; i < params.length ; i = i + 2) {
            builder.append(params[i]).append("=")
        }
        scrap(url);
    }*/

    public ArrayList<DiscountInfo> getDiscountInfoArray() {
        return _discountInfoArray;
    }

    private ArrayList<DiscountInfo> _discountInfoArray;

    private void setDiscountInfoArray(JSONObject json) {
        try {
            JSONArray jsArray = json.getJSONArray(DISCOUNT_INFO);
            _discountInfoArray = new ArrayList<>();
            for (int i = 0; i < jsArray.length(); i++) {
                JSONObject c = jsArray.getJSONObject(i);
                DiscountInfo discountInfo = new DiscountInfo();
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
        _loadCompleteListener.onLoadComplete();
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

    private void scrap(final String url) {

        final String protocol = "http://server.raystar.kro.kr:3030/";
        final String LOG = "webScrapper";

        class WebScrapper extends AsyncTask<String, Void, String> {

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
                else if (Objects.equals(url, DISCOUNT_INFO))
                    setDiscountInfoArray(parseToJSON(str));
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
        WebScrapper g = new WebScrapper();
        g.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }
}
