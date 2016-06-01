package com.hcmp.timer;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Admin on 5/11/2016.
 */
public class JSONParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String surl, Map<String,String> params) {
        // Making HTTP request
        try {
            StringBuilder postData=new StringBuilder();
            for(Map.Entry<String,String> param: params.entrySet()){
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            URL url=new URL(surl);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",String.valueOf(postDataBytes.length) );
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            StringBuilder sb2=new StringBuilder();
            int httpResult = conn.getResponseCode();
            if(httpResult==HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
                String line = null;
                while((line=br.readLine())!=null)
                    sb2.append(line+"\n");
                br.close();
            }
            Log.i("DFJD",sb2.toString());
            jObj = new JSONObject(sb2.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        } catch (Exception e){
            e.printStackTrace();
        }

        // return JSON String
        return jObj;

    }
}
