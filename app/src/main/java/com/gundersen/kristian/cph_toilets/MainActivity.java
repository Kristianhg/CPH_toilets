package com.gundersen.kristian.cph_toilets;




import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap uiGoogleMap;
    private JSONObject savedJson;

    /**
     * Request code for location permission request.
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

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
        savedInstanceState.putString("Json", savedJson.toString());
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
        if(savedJson != null) {

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
        Intent intent = new Intent(this,AppInfo.class);
        startActivity(intent);
    }


    //CONFIGURING MAP
    public void onMapReady (GoogleMap googleMap) {
        uiGoogleMap = googleMap;
        uiGoogleMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        uiGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        uiGoogleMap.getMinZoomLevel();
        uiGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        ////// NEXT LINE IS A PLACEHOLDER. It should be generated from the user´s position. the last arg is zoom level.
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.6815890, 12.5290920), 12.0f));


        if (savedJson != null) {
            GeoJsonLayer layer = new GeoJsonLayer(uiGoogleMap, savedJson);
            layer.addLayerToMap();
        }
        else {
            new AddJsonToMapAsync().execute();
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (uiGoogleMap != null) {
            // Access to the location has been granted to the app.
            uiGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }



    // ASYNC TASK TO FETCH JSON AND PUT IT ON THE MAP
    public class AddJsonToMapAsync extends AsyncTask<Void, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... Args) {

            return new JsonHandling().FetchJson();
        }

        @Override
        protected void onPostExecute(JSONObject geoJson) {
            savedJson = geoJson;
            if (uiGoogleMap != null) {
                GeoJsonLayer layer = new GeoJsonLayer(uiGoogleMap, geoJson);
                layer.addLayerToMap();
            }

        }

    }

    //Getting permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}