package gr.atc.yds.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.clients.YDSApiClient;
import gr.atc.yds.controllers.Authenticator;
import gr.atc.yds.enums.Message;
import gr.atc.yds.enums.ViewMode;
import gr.atc.yds.fragments.ProjectsListFragment;
import gr.atc.yds.fragments.ProjectsMapFragment;
import gr.atc.yds.models.Project;
import gr.atc.yds.utils.Util;

public class HomeActivity extends AppCompatActivity implements ProjectsListFragment.Listener, ProjectsMapFragment.Listener {

    private List<Project> projects;
    private Gson gson;
    private ViewMode viewMode;
    private View listFragmentContainer;
    private View mapFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init
        initUI();
        projects = null;
        gson = new Gson();
        listFragmentContainer = findViewById(R.id.activityHome_listFragment);
        mapFragmentContainer = findViewById(R.id.activityHome_mapFragment);

        switchToListView();
        loadProjects();
    }

    //Initialize UI
    private void initUI(){

        //Set layout
        setContentView(R.layout.activity_home);

        //Set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Set title
        setTitle(getString(R.string.activityHomeTitle));

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
    public void onProjectMarkerClicked(String projectId){

        Util.log("project clicked: " + projectId);

        //Show project details
        startProjectActivity(projectId);
    }

    @Override
    public void onProjectItemClicked(String projectId){

        Util.log("project clicked: " + projectId);

        //Show project details
        startProjectActivity(projectId);
    }

    private void startProjectActivity(String projectId){

        Intent i = new Intent(HomeActivity.this, ProjectActivity.class);
        i.putExtra("projectId", projectId);
        startActivity(i);
    }

    //Load projects
    private void loadProjects(){

        showLoader();
        YDSApiClient client = new YDSApiClient();
        client.getProjects(new YDSApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {

                hideLoader();
                projects = (List<Project>) object;

                showProjects();
            }

            @Override
            public void onFailure(Message message) {

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
    private void showProjects(){

        showProjectsInListFragment();
        showProjectsInMapFragment();

    }

    private void showProjectsInListFragment(){

        ProjectsListFragment projectsListFragment = ProjectsListFragment.newInstance(gson.toJson(projects));

        //Attach fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activityHome_listFragment, projectsListFragment);
        ft.commit();
    }

    private void showProjectsInMapFragment(){

        ProjectsMapFragment projectsMapFragment = ProjectsMapFragment.newInstance(gson.toJson(projects));

        //Attach fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activityHome_mapFragment, projectsMapFragment);
        ft.commit();

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
