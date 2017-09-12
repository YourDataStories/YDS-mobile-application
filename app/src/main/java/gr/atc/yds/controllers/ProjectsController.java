package gr.atc.yds.controllers;

import java.util.ArrayList;
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
    private double lat;
    private double lon;
    private double radius;

    public ProjectsController(){
        offset = 0;

        //Default search area
        this.lat = Double.parseDouble(App.getContext().getResources().getString(R.string.DEFAULT_LAT));
        this.lon = Double.parseDouble(App.getContext().getResources().getString(R.string.DEFAULT_LON));
        this.radius = Double.parseDouble(App.getContext().getResources().getString(R.string.DEFAULT_RADIUS_IN_KILOMETERS));
    }

    public void setSearchArea(double lat, double lon){

        double radius = Double.parseDouble(App.getContext().getResources().getString(R.string.DEFAULT_RADIUS_IN_KILOMETERS));
        setSearchArea(lat, lon, radius);
    }

    public void setSearchArea(double lat, double lon, double radius){

        //Set new search area
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;

        //Reset
        offset = 0;
    }

    public void loadProjects(final ResponseListener responseListener){

        YDSApiClient client = YDSApiClient.getInstance();
        client.getProjects(lat, lon, radius, offset, new YDSApiClient.ResponseListener() {
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
