package gr.atc.yds.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gr.atc.yds.R;
import gr.atc.yds.clients.Client;
import gr.atc.yds.clients.YDSApiClient;
import gr.atc.yds.controllers.LocationTracker;
import gr.atc.yds.controllers.NotificationsManager;
import gr.atc.yds.enums.Message;
import gr.atc.yds.models.Project;
import gr.atc.yds.utils.Log;

/**
 * Checks if there is project near the device's location and shows a notification with project's name and link
 */
public class CloseProjectService extends Service {

    private LocationTracker locationTracker;
    private boolean searchingForCloseProjectStarted = false;
    private int locationInterval;
    private int locationFastestInterval;
    private int locationPriority;

    //Keep list of found close projects, so if server returns again the same project, the application doesn't show notification
    private Set<Long> foundCloseProjectIDs;

    //Start service
    public static void start(Context context){

        Intent intent = new Intent(context, CloseProjectService.class);
        context.startService(intent);
    }

    //Stop service
    public static void stop(Context context){

        Intent intent = new Intent(context, CloseProjectService.class);
        context.stopService(intent);
    }

    public CloseProjectService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("YDS", "CloseProjectService created");

        locationTracker = new LocationTracker();
        foundCloseProjectIDs = new HashSet<>();
        locationInterval = getResources().getInteger(R.integer.LOCATION_INTERVAL_MILLIS);
        locationFastestInterval = getResources().getInteger(R.integer.LOCATION_FASTEST_INTERVAL_MILLIS);
        locationPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("YDS", "CloseProjectService started");

        //Start searching for close project
        startSearchingForCloseProject();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("YDS", "CloseProjectService destroyed");

        //Stop searching for close project
        stopSearchingForCloseProject();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Starts searching for close project
     */
    private void startSearchingForCloseProject(){

        if(!searchingForCloseProjectStarted){
            Log.i("YDS", "startSearchingForCloseProject");

            searchingForCloseProjectStarted = true;

            locationTracker.requestLocationUpdates(locationInterval, locationFastestInterval, locationPriority, new LocationTracker.Callback() {
                @Override
                public void onLocationUpdate(Location location) {

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    //Search for close project
                    YDSApiClient.getInstance().getCloseProject(latLng, new Client.ResponseListener() {
                        @Override
                        public void onSuccess(Object object) {

                            if(object instanceof Project) {
                                Project project = (Project) object;
                                closeProjectFound(project);
                            }
                        }

                        @Override
                        public void onFailure(Message message) {
                        }
                    });
                }
            });
        }
    }

    /**
     * Stops searching for close project
     */
    private void stopSearchingForCloseProject(){

        if(searchingForCloseProjectStarted){
            Log.i("YDS", "stopSearchingForCloseProject");

            locationTracker.removeLocationUpdates();
            searchingForCloseProjectStarted = false;
        }
    }

    /**
     * Invoked when a close project is found
     * @param project close project
     */
    private void closeProjectFound(Project project){

        //Show notification only if the close project is the first time that is loaded
        if(!foundCloseProjectIDs.contains(project.projectId)){

            foundCloseProjectIDs.add(project.projectId);
            NotificationsManager.getInstance().showCloseProjectNotification(project.projectId, project.getTitle());
        }

    }
}


