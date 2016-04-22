package com.gundersen.kristian.cph_toilets;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONObject;

import java.net.URL;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap uiGoogleMap;
    private JSONObject savedJson;

    /// SET UP REFERENCES
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap));
        supportMapFragment.getMapAsync(this);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("Json",savedJson.toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedString = savedInstanceState.getString("Json");
        try {
            savedJson = new JSONObject(savedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //SETTING UP THE MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                runInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void runInfo() {
        Logger.getAnonymousLogger().info("--> runinfo()");
        Intent intent = new Intent(this,AppInfo.class);
        startActivity(intent);
    }



    //CONFIGURING MAP
    public void onMapReady (GoogleMap googleMap) {
        Logger.getAnonymousLogger().info("MAP READY");
        uiGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getMinZoomLevel();
        ////// NEXT LINE IS A PLACEHOLDER. It should be generated from the user´s position. the last arg is zoom level.
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.6815890, 12.5290920), 12.0f));


        Logger.getAnonymousLogger().info("MAP SEEKING JSON");
        if (savedJson != null) {
            Logger.getAnonymousLogger().info("JSON FILE ALREADY EXISTS");
            GeoJsonLayer layer = new GeoJsonLayer(uiGoogleMap, savedJson);
            layer.addLayerToMap();
            Logger.getAnonymousLogger().info("MAP GOT EXISTING JSON");
        }
        else {
            Logger.getAnonymousLogger().info("DOWNLOADING JSON");
            new AddJsonToMapAsync().execute();
        }

    }


// ASYNC TASK TO FETCH JSON AND PUT IT ON THE MAP
    public class AddJsonToMapAsync extends AsyncTask<Void, String, JSONObject> {

        /*// PROGRESS SPINNER, IGNORE UNTIL IMPORTANT STUFF WORKS
        private ProgressDialog spinner;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            spinner.setMessage("Please wait...");
            spinner.setCancelable(true);
            spinner.show();
        }
        //*/

        @Override
        protected JSONObject doInBackground(Void... Args) {

            return new JsonHandling().FetchJson();
        }

        @Override
        protected void onPostExecute(JSONObject geoJson) {
            savedJson = geoJson;
            GeoJsonLayer layer = new GeoJsonLayer(uiGoogleMap, geoJson);
            layer.addLayerToMap();
            Logger.getAnonymousLogger().info("MAP GOT DOWNLOADED JSON");


        }

    }

}
