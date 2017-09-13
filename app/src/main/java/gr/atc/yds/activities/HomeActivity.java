package gr.atc.yds.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.clients.YDSApiClient;
import gr.atc.yds.controllers.LocationTracker;
import gr.atc.yds.controllers.ProjectsController;
import gr.atc.yds.enums.Message;
import gr.atc.yds.enums.ViewMode;
import gr.atc.yds.fragments.ProjectsListFragment;
import gr.atc.yds.fragments.ProjectsMapFragment;
import gr.atc.yds.models.Project;
import gr.atc.yds.services.CloseProjectService;
import gr.atc.yds.utils.Util;

import static android.R.id.message;

public class HomeActivity extends PrivateActivity implements ProjectsListFragment.Listener, ProjectsMapFragment.Listener {

    private static final int SHOW_PROJECT_DETAILS_REQUEST = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    private List<Project> projects = new ArrayList<>();
    private Gson gson = new Gson();;
    private ViewMode viewMode;
    private View listFragmentContainer;
    private View mapFragmentContainer;
    private ProjectsListFragment projectsListFragment;
    private ProjectsMapFragment projectsMapFragment;
    private ProjectsController projectsController = new ProjectsController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init
        initUI();
        switchToListView();

        //Location permission is granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            handleLocationPermissionGranted();

        //Location permission is not granted, so request it
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }

    /**
     * Invoked if location permission granted
     */
    private void handleLocationPermissionGranted(){

        //Get current location
        new LocationTracker().getLastLocation(new LocationTracker.Callback() {
            @Override
            public void onLocationUpdate(Location location) {
                Log.i("YDS", String.format("current location: %f,%f", location.getLatitude(), location.getLongitude()));

                //Load projects found around the current location
                projectsController.setSearchArea(location.getLatitude(), location.getLongitude());
                loadProjects();
            }
        });

        //Start CloseProjectService
        CloseProjectService.start(this);
    }

    /**
     * Invoked if location permission denied
     */
    private void handleLocationPermissionDenied(){

        //Load projects found in the default area
        loadProjects();
    }

    //Initialize
    private void initUI(){

        //Set layout
        setContentView(R.layout.activity_home);

        //Set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Set title
        setTitle(getString(R.string.activityHomeTitle));

        listFragmentContainer = findViewById(R.id.activityHome_listFragment);
        mapFragmentContainer = findViewById(R.id.activityHome_mapFragment);

        //Attach project list fragment
        projectsListFragment = ProjectsListFragment.newInstance(gson.toJson(projects));
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activityHome_listFragment, projectsListFragment);
        ft.commit();

        //Attach project map fragment
        projectsMapFragment = ProjectsMapFragment.newInstance(gson.toJson(projects));
        FragmentManager fm2 = getSupportFragmentManager();
        FragmentTransaction ft2 = fm2.beginTransaction();
        ft2.replace(R.id.activityHome_mapFragment, projectsMapFragment);
        ft2.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_home_menu, menu);

        MenuItem action_map_view = menu.findItem(R.id.map_view);
        MenuItem action_list_view = menu.findItem(R.id.list_view);

        switch(viewMode){

            case LIST_VIEW:

                //Hide 'list-view action' item
                action_list_view.setVisible(false);

                //Show 'map-view action' item
                action_map_view.setVisible(true);

                break;

            case MAP_VIEW:

                //Hide 'map-view action' item
                action_map_view.setVisible(false);

                //Show 'list-view action' item
                action_list_view.setVisible(true);

                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.list_view:

                //Switch to list view
                switchToListView();
                return true;

            case R.id.map_view:

                //Switch to map view
                switchToMapView();
                return true;

            case R.id.logout:

                //Logout
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProjectMarkerClicked(Long projectId){

        //Show project details
        startProjectActivity(projectId);
    }

    @Override
    public void onSearchButtonClicked(LatLngBounds bounds) {

        if(projectsListFragment != null)
            projectsListFragment.clearProjects();

        if(projectsMapFragment != null)
            projectsMapFragment.clearProjects();

        projects.clear();
        projectsController.setSearchArea(bounds);
        loadProjects();
    }

    @Override
    public void onProjectItemClicked(Long projectId){

        //Show project details
        startProjectActivity(projectId);
    }

    @Override
    public void onProjectListScrolledToBottom() {

        //Load more projects
        loadProjects();
    }

    private void startProjectActivity(Long projectId){

        Intent i = new Intent(HomeActivity.this, ProjectActivity.class);
        i.putExtra("projectId", projectId);
        startActivityForResult(i, SHOW_PROJECT_DETAILS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //ProjectActivity returned result
        if(requestCode == SHOW_PROJECT_DETAILS_REQUEST && resultCode == Activity.RESULT_OK){

            Long projectId = data.getExtras().getLong("projectId");
            Float average_rating = data.getExtras().getFloat("average_rating");
            Integer num_ratings = data.getExtras().getInt("num_ratings");
            Integer num_comments = data.getExtras().getInt("num_comments");

            //Check if project updated
            if(projects != null){
                for(Project project : projects)
                    if (project.projectId.equals(projectId)) {
                        if (project.num_ratings != num_ratings || project.num_comments != num_comments) {

                            project.average_rating = average_rating;
                            project.num_ratings = num_ratings;
                            project.num_comments = num_comments;
                            projectUpdated(project);
                            break;

                        }
                    }
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:

                if (Util.permissionGranted(grantResults))
                    handleLocationPermissionGranted();
                else
                    handleLocationPermissionDenied();

                break;
        }
    }

    //Project updated (from ProjectActivity)
    private void projectUpdated(Project project){

        if(projectsListFragment != null)
            projectsListFragment.updateProject(project);
    }

    /**
     * Loads projects
     */
    private void loadProjects(){

        showLoader();
        projectsController.loadProjects(new ProjectsController.ResponseListener() {
            @Override
            public void onSuccess(Object object) {
                hideLoader();

                List<Project> projects = (List<Project>) object;
                showProjects(projects);
            }

            @Override
            public void onFailure(Message message) {
                hideLoader();
                Util.showToast(message);
            }
        });
    }

    //Switch to list view. Show Projects as list
    private void switchToListView(){

        viewMode = ViewMode.LIST_VIEW;

        mapFragmentContainer.setVisibility(View.INVISIBLE);
        listFragmentContainer.setVisibility(View.VISIBLE);

        //Refresh toolbar menu, by calling 'onCreateOptionsMenu()'
        invalidateOptionsMenu();
    }

    //Switch to map view. Show Projects on map
    private void switchToMapView(){

        viewMode = ViewMode.MAP_VIEW;

        listFragmentContainer.setVisibility(View.INVISIBLE);
        mapFragmentContainer.setVisibility(View.VISIBLE);

        //Refresh toolbar menu, by calling 'onCreateOptionsMenu()'
        invalidateOptionsMenu();
    }

    //Show projects (inside fragments)
    private void showProjects(List<Project> projects){

        showProjectsInListFragment(projects);
        showProjectsInMapFragment(projects);
    }

    private void showProjectsInListFragment(List<Project> projects){

        if(projectsListFragment != null)
            projectsListFragment.addProjects(projects);
    }

    private void showProjectsInMapFragment(List<Project> projects){

        if(projectsMapFragment != null)
            projectsMapFragment.addProjects(projects);
    }

    //Show loader
    private void showLoader(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activityHome_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    //Hide loader
    private void hideLoader(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activityHome_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
