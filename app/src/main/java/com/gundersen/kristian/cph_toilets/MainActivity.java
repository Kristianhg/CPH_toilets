package com.gundersen.kristian.cph_toilets;

/**Portions of this page are reproduced from work created and shared by Google
 *  and used according to terms described in the Creative Commons 3.0 Attribution License.
 *
 *  Portions of this page are modifications based on work created and shared by Google
 *  and used according to terms described in the Creative Commons 3.0 Attribution License.*/



import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
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

    //Flag indicating whether device is online.
    private boolean isNetworkStatusAvailable;

    private GoogleApiClient mGoogleApiClient;
    private Double mLatitude;
    private Double mLongitude;

    /**Needed for the ConnectionCallback GoogleAPI interface */
    @Override
    public void onConnectionSuspended(int i) {

    }


    /**Needed for the onConnectionFailed GoogleAPI interface */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);


            //Checks network status. If offline - gives alert, if online, fetches map and Json.
            isNetworkStatusAvailable = ConnectivityHandling.isNetworkStatusAvialable(this);

            // Create an instance of GoogleAPIClient.
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            mGoogleApiClient.connect();

        }catch( Exception e) {
            e.printStackTrace();
        }}

    /**Connects and disconnects from Google API on start and stop */
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    // When the phone is turned, the app is switched out of, etc. variables are usually emptied. This saves the JSon to a variable that
    //survives the state change.
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (savedJson != null) {
            savedInstanceState.putString("Json", savedJson.toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    /**Gets last location and saves to latitude and longitude variables*/
    @Override
    public void onConnected(Bundle connectionHint) {
        //Toast.makeText(this, "Connected to Google API", Toast.LENGTH_LONG).show();
    Location mLastLocation;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            // Access to the location has been granted to the app.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
           if (mLastLocation != null) {
               //Toast.makeText(this, "Got location", Toast.LENGTH_SHORT).show();
               mLatitude = mLastLocation.getLatitude();
               mLongitude = mLastLocation.getLongitude();
            }

            //Starts fetching map async
            //if (isNetworkStatusAvailable) {
                SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap));
                supportMapFragment.getMapAsync(this);

                //gives alert if offline.
           // } else {
               // Toast.makeText(getApplicationContext(), R.string.network_not_available, Toast.LENGTH_LONG).show();

            //}
        }
    }

    //After the phone has been turned, or app is switched back into, etc. this kicks in.
    //Re-fills variables with the data saved across the state-change.
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedString = savedInstanceState.getString("Json");
        try {
            savedJson = new JSONObject(savedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //if(savedJson != null) {
        //}
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


    //When the map is done loading async, this configures the map and starts adding the JSon to it as points.
    public void onMapReady (GoogleMap googleMap) {
        //Toast.makeText(this, "MAP READY", Toast.LENGTH_LONG).show();
        uiGoogleMap = googleMap;
        uiGoogleMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        uiGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        uiGoogleMap.getMinZoomLevel();
        uiGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        ////// NEXT TASK TO DEVELOP
        ////// THE BELOW LINE IS A PLACEHOLDER. It should be generated from the user´s position. the last arg is zoom level.
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), 15.0f));

        //CHecks that the JSON has been fetched and adds it to the map. If not, it initiates the fetching..
        //this means that the first time the app is run, it will fetch the JSon.
        // If the map is ever re-created, it does not need to re-download.
        if (savedJson != null) {
            GeoJsonLayer layer = new GeoJsonLayer(uiGoogleMap, savedJson);
            layer.addLayerToMap();
        }
        else {
            new AddJsonToMapAsync().execute();
        }
        Toast.makeText(this, "Select toilet for navigation", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
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

            if (geoJson != null) {
                savedJson = geoJson;
                if (uiGoogleMap != null) {
                    GeoJsonLayer layer = new GeoJsonLayer(uiGoogleMap, geoJson);
                    layer.addLayerToMap();
                }
            }
            else {
                Context context = getApplicationContext();
                CharSequence text = "No internet, dude";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }

    }

    //GETTING PERMISSIONS - From Google
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