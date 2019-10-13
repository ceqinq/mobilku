package com.legacies.legacies.Tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.List;


public class SGps implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationListener locationListener;

    public SGps(Context context) {
        this.context = context;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }





    public void on(LocationListener locationListener) {

        this.locationListener = locationListener;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }


    public void off() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }








    public static double jarak (Location lokasi1 , Location lokasi2){
        double jarak = lokasi1.distanceTo(lokasi2);
        Log.d("DISTANCE",""+jarak);
        return jarak;
    }

    public static double jarak (double lat1 , double long1, double lat2, double long2){
        Location lokasi1 = new Location("");
        lokasi1.setLatitude(lat1); lokasi1.setLongitude(long1);

        Location lokasi2 = new Location("");
        lokasi2.setLatitude(lat2); lokasi2.setLongitude(long2);

        double jarak = lokasi1.distanceTo(lokasi2);
        Log.d("DISTANCE",""+jarak);
        return jarak;
    }


    public static boolean isInLocation (Location lokasi1 , Location lokasi2, int radius){
        double jarak = lokasi1.distanceTo(lokasi2);
        Log.d("DISTANCE",""+jarak);
        return jarak <= radius;
    }

    public static boolean isInLocation (double lat1 , double long1, double lat2, double long2, int radius){

        Location lokasi1 = new Location("");
        lokasi1.setLatitude(lat1); lokasi1.setLongitude(long1);

        Location lokasi2 = new Location("");
        lokasi2.setLatitude(lat2); lokasi2.setLongitude(long2);

        double jarak = lokasi1.distanceTo(lokasi2);
        Log.d("DISTANCE",""+jarak);
        return jarak <= radius;
    }



    public static boolean isMockLocation(Location location) {
        return location != null && location.isFromMockProvider();
    }




    public static void reqHighGps(final Activity activity) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //"All location settings are satisfied."
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //"Location settings are not satisfied. Show the user a dialog to upgrade location settings "

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(activity, 8);
                        } catch (IntentSender.SendIntentException e) {
                            //"PendingIntent unable to execute request."
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //"Location settings are inadequate, and cannot be fixed here. Dialog not created."
                        break;
                }
            }
        });
    }






    public class Geocoding {

        Address address;

        public Geocoding(Context context, double latitude , double longitude)   {

            Geocoder geocoder = new Geocoder(context);
            List<Address> listAddress = null;
            try {
                listAddress = geocoder.getFromLocation(latitude,longitude,1);
                address = listAddress.get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public String getNegara (){
            return address.getCountryName();
        }

        public String getProvinsi (){
            return address.getAdminArea();
        }

        public String getKota (){
            return address.getSubAdminArea();
        }

        public String getFull (){
            return address.getAddressLine(0);
        }

    }






}

