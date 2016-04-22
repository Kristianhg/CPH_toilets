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
    private static final String staticURLString = (String)"http://wfs-kbhkort.kk.dk/k101/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=k101:toilet&outputFormat=json&SRSNAME=EPSG:4326";


    // SET UP REFERENCES
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap));
        supportMapFragment.getMapAsync(this);
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
        uiGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getMinZoomLevel();
        ////// NEXT LINE IS A PLACEHOLDER. It should be generated from the user´s position. the last arg is zoom level.
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.6815890, 12.5290920), 12.0f));
        new AddJsonToMapAsync().execute(staticURLString);
    }


// ASYNC TASK TO FETCH JSON AND PUT IT ON THE MAP
    public class AddJsonToMapAsync extends AsyncTask<String, String, JSONObject> {

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
        protected JSONObject doInBackground(String... urlStrings) {

            return new JsonHandling().FetchJson(urlStrings[0]);
        }

        @Override
        protected void onPostExecute(JSONObject geoJson) {

            GeoJsonLayer layer = new GeoJsonLayer(uiGoogleMap, geoJson);
            layer.addLayerToMap();

        }

    }

}
