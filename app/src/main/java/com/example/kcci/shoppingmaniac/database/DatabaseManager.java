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

public class DatabaseManager {

    private String _protocol = "http://server.raystar.kro.kr:3030/";

    public void request(String requestUrl) {
        String url = _protocol + requestUrl;
        getData(requestUrl);
    }
    //TODO GET방식 요청 오버로딩
//    public void request(String requestUrl, String... params){
//        StringBuilder builder = new StringBuilder(_protocol);
//        builder.append(requestUrl).append("?");
//        for (int i = 0; i < params.length ; i = i + 2) {
//            builder.append(params[i]).append("=")
//        }
//        getData(url);
//    }

    private void getData(final String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];

                BufferedReader bufferedReader;
                try {
                    URL url = new URL(_protocol + uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    Log.i("log", "Downloading");
                    return sb.toString().trim();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return "{}";
                }
            }


            protected void onPostExecute(String str) {
                Log.i("log", "Posting");
                convert(parseToJSON(str), url);
                _loadCompleteListener.onLoadComplete();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    private JSONObject parseToJSON(String result) {
        try {
            JSONObject json = new JSONObject(result);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<DiscountInfo> getDiscountInfos() {
        return _discountInfos;
    }

    private ArrayList<DiscountInfo> _discountInfos;

    private void convert(JSONObject json, String url) {
        if (Objects.equals(url, "discountinfo")) {
            try {
                JSONArray jsArray = json.getJSONArray(url);
                _discountInfos = new ArrayList();
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

                    _discountInfos.add(discountInfo);
                    Log.i("tag", "put on array");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //region LoadCompleteListener
    LoadCompleteListener _loadCompleteListener;

    public interface LoadCompleteListener {
        void onLoadComplete();
    }

    public void setLoadCompleteListener(LoadCompleteListener loadCompleteListener) {
        if (_loadCompleteListener != loadCompleteListener)
            _loadCompleteListener = loadCompleteListener;
    }
    //endregion
}
