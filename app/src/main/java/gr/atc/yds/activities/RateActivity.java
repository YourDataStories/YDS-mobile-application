package gr.atc.yds.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import gr.atc.yds.R;
import gr.atc.yds.clients.YDSApiClient;
import gr.atc.yds.controllers.Authenticator;
import gr.atc.yds.enums.Message;
import gr.atc.yds.models.ProjectDetails;
import gr.atc.yds.utils.Util;

public class RateActivity extends PrivateActivity {

    private String projectId;
    private String projectTitle;
    private float projectRate;
    private Authenticator auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        //Get arguments
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            projectId = getIntent().getExtras().getString("projectId");
            projectTitle = getIntent().getExtras().getString("projectTitle");
            projectRate = getIntent().getExtras().getFloat("projectRate");
        }
        else{
            projectId = null;
            projectTitle = null;
            projectRate = -1;
        }

        //Init
        initUI();
        auth = new Authenticator();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_rate_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            //Back btn clicked
            case android.R.id.home:
                finish();
                break;

            //Rate btn clicked
            case R.id.rate:
                rateProject();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    //Init UI
    private void initUI(){

        //Set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Add back btn to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Set title
        setTitle(getString(R.string.activityRateTitle));

        if(projectTitle == null || projectRate == -1)
            return;

        //Project title
        TextView title = (TextView) findViewById(R.id.activityRate_title);
        title.setText(projectTitle);

        //Project rating
        RatingBar ratingBar = (RatingBar) findViewById(R.id.activityRate_ratingBar);
        ratingBar.setRating(projectRate);
    }

    //Rate project
    private void rateProject(){

        if(projectId == null || !auth.isUserLoggedIn())
            return;

        //Get current user's identity
        String username = auth.getUsername();

        //Read rating from UI
        RatingBar ratingBar = (RatingBar) findViewById(R.id.activityRate_ratingBar);
        final float rating = ratingBar.getRating();

        //Submit rating
        showLoader();
        YDSApiClient client = new YDSApiClient();
        client.rateProject(projectId, rating, username, new YDSApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {

                hideLoader();

                //Send result to ProjectActivity
                Intent data = new Intent();
                data.putExtra("rating", rating);

                if (getParent() == null)
                    setResult(Activity.RESULT_OK, data);
                else
                    getParent().setResult(Activity.RESULT_OK, data);

                finish();

            }

            @Override
            public void onFailure(Message message) {
                Util.showToast(message);
            }
        });

    }

    //Show loader
    private void showLoader(){

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activityRate_progressBar);
        progressBar.setVisibility(View.VISIBLE);

    }

    //Hide loader
    private void hideLoader(){

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activityRate_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

    }
}
