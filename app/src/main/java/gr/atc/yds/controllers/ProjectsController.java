package gr.atc.yds.controllers;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.clients.YDSApiClient;
import gr.atc.yds.enums.Message;
import gr.atc.yds.models.Project;
import gr.atc.yds.utils.Util;

/**
 * Created by ipapas on 12/09/17.
 */

public class ProjectsController {

    //Callback
    public interface ResponseListener {
        public void onSuccess(Object object);
        public void onFailure(Message message);
    }

    //Projects are loaded via pagination, so we use offset in order to request the next page of projects
    private int offset;

    //Search area
    private LatLngBounds searchAreaBounds;

    public ProjectsController(){

        //Default search area
        double lat = Double.parseDouble(App.getContext().getResources().getString(R.string.DEFAULT_LAT));
        double lon = Double.parseDouble(App.getContext().getResources().getString(R.string.DEFAULT_LON));
        double radius = Double.parseDouble(App.getContext().getResources().getString(R.string.DEFAULT_RADIUS_IN_KILOMETERS));
        this.searchAreaBounds = Util.toBounds(lat, lon, radius);

        //Init offset
        this.offset = 0;
    }

    public void setSearchArea(double lat, double lon){
        setSearchArea(lat, lon, Double.parseDouble(App.getContext().getResources().getString(R.string.DEFAULT_RADIUS_IN_KILOMETERS)));
    }

    public void setSearchArea(double lat, double lon, double radius){
        setSearchArea(Util.toBounds(lat, lon, radius));
    }

    public void setSearchArea(LatLngBounds bounds){
        this.searchAreaBounds = bounds;
        this.offset = 0;
    }

    public void loadProjects(final ResponseListener responseListener){

        YDSApiClient client = YDSApiClient.getInstance();
        client.getProjects(searchAreaBounds.southwest, searchAreaBounds.northeast, offset, new YDSApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {

                List<Project> projects = (List<Project>) object;
                offset += projects.size();

                responseListener.onSuccess(projects);
            }

            @Override
            public void onFailure(Message message) {
                responseListener.onFailure(message);
            }
        });

    }

}
