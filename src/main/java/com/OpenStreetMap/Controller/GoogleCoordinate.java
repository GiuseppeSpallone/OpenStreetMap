package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Node;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GoogleCoordinate {

    public double[] loadGoogleCoordinate(String query) {
        try {

            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?address=");
            sb.append(URLEncoder.encode(query, "UTF-8"));

            sb.append("&key=AIzaSyAMdK0shvI_--o7XYuRpx2UxyqQP8pqReg");

            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String line, outputString = "";
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                outputString += line;
            }

            JSONObject j = new JSONObject(outputString);
            String status = j.getString("status");
            if (status.equals("OK")) {
                JSONArray results = j.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    double lat = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double lon = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    return new double[]{lat, lon};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
}
