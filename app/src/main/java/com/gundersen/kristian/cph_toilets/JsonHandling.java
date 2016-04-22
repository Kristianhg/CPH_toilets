package com.gundersen.kristian.cph_toilets;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kristiangundersen on 22/04/16.
 */
public class JsonHandling {
    private JSONObject geoJson;
    private HttpURLConnection urlConnection;

    public JSONObject FetchJson (String urlString){


        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            geoJson =  new JSONObject(result.toString());

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        return geoJson;
    }


}
