package gr.atc.yds.controllers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import gr.atc.yds.R;
import gr.atc.yds.utils.Log;

/**
 * Created by ipapas on 11/09/17.
 */

public class LocationTracker {

    //Callback
    public interface Callback {
        void onLocationUpdate(Location location);
    }

    private Context context;
    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
    private boolean locationTrackingStarted = false;

    public LocationTracker(){
        context = App.getContext();
    }

    public void requestLocationUpdates(int interval, int fastestInterval, int priority, Callback trackerCallback){

        //Location permission required
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.w("YDS", "Location permission not granted");
            return;
        }

        if(!locationTrackingStarted){
            LocationRequest locationRequest = buildLocationRequest(interval, fastestInterval, priority);
            locationCallback = buildLocationCallback(trackerCallback);
            locationClient = LocationServices.getFusedLocationProviderClient(context);
            locationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    public void removeLocationUpdates(){

        if(locationTrackingStarted){
            locationClient.removeLocationUpdates(locationCallback);
            locationTrackingStarted = false;
        }
    }

    public void getLastLocation(final Callback trackerCallback){

        //Location permission required
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.w("YDS", "Location permission not granted");
            return;
        }

        LocationServices.getFusedLocationProviderClient(context).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                trackerCallback.onLocationUpdate(location);
            }
        });

    }

    /**
     * Builds location request
     * @return LocationRequest
     */
    private LocationRequest buildLocationRequest(int interval, int fastestInterval, int priority){

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(context.getResources().getInteger(R.integer.LOCATION_INTERVAL_MILLIS));
        locationRequest.setFastestInterval(context.getResources().getInteger(R.integer.LOCATION_FASTEST_INTERVAL_MILLIS));
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); //~100m accuracy

        return locationRequest;
    }

    /**
     * Builds location callback that will handle the location updates
     * @return LocationCallback
     */
    private LocationCallback buildLocationCallback(final Callback trackerCallback){

        return new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                trackerCallback.onLocationUpdate(location);

            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
    }
}
