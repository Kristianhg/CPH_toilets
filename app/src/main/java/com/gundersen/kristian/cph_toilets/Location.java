package com.gundersen.kristian.cph_toilets;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


/**
 * Created by kristiangundersen on 16/08/16.
 */
public class Location implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener  {
/*public double getLatitude (Context context){


        GoogleApiClient mGoogleApiClient;
        Location mLastLocation;

        // Create an instance of GoogleAPIClient.

            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(context)
                    .addOnConnectionFailedListener(context)
                    .addApi(LocationServices.API)
                    .build();

    mGoogleApiClient.connect();

    // Access to the location has been granted to the app.
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
            mGoogleApiClient);

    return mLastLocation;
        }



    }

*/





    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
