package gr.atc.yds.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.clients.YDSApiClient;
import gr.atc.yds.enums.Message;
import gr.atc.yds.models.Project;
import gr.atc.yds.models.ProjectDetails;
import gr.atc.yds.utils.Util;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        //Set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Add back btn to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Get arguments
        String projectId = getIntent().getExtras().getString("projectId");
        Util.log("project details: " + projectId);

        //Load project
        if(projectId != null)
            loadProjectDetails(projectId);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Back btn clicked
        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    //Load project
    private void loadProjectDetails(String projectId){

        showLoader();
        YDSApiClient client = new YDSApiClient();
        client.getProjectDetails(projectId, new YDSApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {

                hideLoader();

                //Show project
                ProjectDetails project = (ProjectDetails) object;
                showProjectDetails(project);
            }

            @Override
            public void onFailure(Message message) {

            }
        });
    }

    //Show project
    private void showProjectDetails(ProjectDetails project){


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
