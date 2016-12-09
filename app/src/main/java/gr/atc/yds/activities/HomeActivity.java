package gr.atc.yds.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.adapters.ProjectListAdapter;
import gr.atc.yds.clients.YDSApiClient;
import gr.atc.yds.controllers.App;
import gr.atc.yds.controllers.Authenticator;
import gr.atc.yds.enums.Message;
import gr.atc.yds.enums.ViewMode;
import gr.atc.yds.models.Project;
import gr.atc.yds.utils.Util;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ViewMode viewMode;
    private ListView projectsListView;
    private View projectsMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        viewMode = ViewMode.LIST_VIEW;
        projectsListView = (ListView) findViewById(R.id.activityHome_projectsListView);
        projectsMapFragment = findViewById(R.id.activityHome_mapFragment);
        projectsMapFragment.setVisibility(View.GONE);

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
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Set map
        map = googleMap;

        //Show the current location icon
        if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            map.setMyLocationEnabled(true);

        loadProjects();
    }

    //Load projects
    private void loadProjects(){

        showLoader();
        YDSApiClient client = new YDSApiClient();
        client.getProjects(new YDSApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {

                hideLoader();
                List<Project> projects = (List<Project>) object;

                //Show projects
                showProjectsOnList(projects);
                showProjectsOnMap(projects);
            }

            @Override
            public void onFailure(Message message) {

            }
        });
    }

    //Switch to list view. Show Projects as list
    private void switchToListView(){

        //Change ViewMode
        viewMode = ViewMode.LIST_VIEW;

        //Refresh toolbar menu, by calling 'onCreateOptionsMenu()'
        invalidateOptionsMenu();

        //Hide map
        projectsMapFragment.setVisibility(View.GONE);

        //Show list
        projectsListView.setVisibility(View.VISIBLE);

    }

    //Switch to map view. Show Projects on map
    private void switchToMapView(){

        //Change ViewMode
        viewMode = ViewMode.MAP_VIEW;

        //Refresh toolbar menu, by calling 'onCreateOptionsMenu()'
        invalidateOptionsMenu();

        //Hide list
        projectsListView.setVisibility(View.GONE);

        //Show map
        projectsMapFragment.setVisibility(View.VISIBLE);

    }

    //Show projects on list
    private void showProjectsOnList(List<Project> projects){

        ListView projectListView = (ListView) findViewById(R.id.activityHome_projectsListView);
        ProjectListAdapter projectListAdapter = new ProjectListAdapter(getApplicationContext(), projects);

        projectListView.setAdapter(projectListAdapter);

    }

    //Show projects on map
    private void showProjectsOnMap(List<Project> projects){

        if(map == null)
            return;



    }

    //Logout
    private void logout(){

        Authenticator auth = new Authenticator();
        auth.signOut(new Authenticator.ResponseListener() {
            @Override
            public void onSuccess() {

                //Start SignInActivity
                Intent i = new Intent(HomeActivity.this, SignInActivity.class);
                startActivity(i);
            }

            @Override
            public void onFailure(Message message) {
                Util.showToast(message);
            }
        });
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
