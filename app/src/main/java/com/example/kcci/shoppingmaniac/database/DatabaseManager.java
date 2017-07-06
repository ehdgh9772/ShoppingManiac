package com.example.kcci.shoppingmaniac.database;

import android.os.AsyncTask;

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

public class DatabaseManager {

    private String RESULT = "result";
    private SaleInfo[] _saleInfos;

    public void getData(final String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];

                BufferedReader bufferedReader;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return "ERROR";
                }
            }

            protected void onPostExecute(String str) {
                distributeJSON(parseToJSON(str), url);

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

    private void distributeJSON(JSONObject json, String url) {

        JSONConverter converter = new JSONConverter();
        switch (url) {
            case "selectAllSaleInfo":
                converter.convertToSale(json);
                break;
            default:
                break;
        }
    }

    class JSONConverter {

        void convertToSale(JSONObject json) {
            try {
                JSONArray jsArray = json.getJSONArray(RESULT);
                _saleInfos = new SaleInfo[jsArray.length()];
                for (int i = 0; i < jsArray.length(); i++) {
                    JSONObject c = jsArray.getJSONObject(i);
                    _saleInfos[i] = new SaleInfo();
                    _saleInfos[i].name = c.getString("Name");
                    _saleInfos[i].discountType = c.getString("DiscountType");
                    _saleInfos[i].price = c.getString("Price");
                    _saleInfos[i].discountedPrice = c.getString("DiscountedPrice");
                    _saleInfos[i].category = c.getString("Category");
                }
                _loadCompleteListener.onLoadComplete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    LoadCompleteListener _loadCompleteListener;

    interface LoadCompleteListener {
        void onLoadComplete();
    }

    public void setLoadCompleteListener(LoadCompleteListener loadCompleteListener) {
        if (_loadCompleteListener != loadCompleteListener)
            _loadCompleteListener = loadCompleteListener;
    }
}
