package gr.atc.yds.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.adapters.CommentListAdapter;
import gr.atc.yds.clients.YDSApiClient;
import gr.atc.yds.controllers.App;
import gr.atc.yds.controllers.Authenticator;
import gr.atc.yds.enums.Message;
import gr.atc.yds.fragments.ProjectMapFragment;
import gr.atc.yds.models.Comment;
import gr.atc.yds.models.ProjectDetails;
import gr.atc.yds.utils.Util;

public class ProjectActivity extends PrivateActivity implements CommentListAdapter.OnCommentReactionListener{

    private static final int RATE_PROJECT_REQUEST = 1;
    private static final int COMMENT_PROJECT_REQUEST = 2;

    private Gson gson;
    private ProjectDetails project;
    private Boolean projectCollapsed;

    private ImageView collapseBtn;
    private TextView description;
    private RelativeLayout beneficiaryLayout;
    private RelativeLayout completionOfPaymentsLayout;
    private RelativeLayout projectIdLayout;
    private ViewGroup detailsView;
    private ListView commentListView;
    private CommentListAdapter commentListAdapter;
    private ViewGroup footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        //Get arguments
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String projectId = extras.getString("projectId");
            loadProject(projectId);
        }
        //Restore (in case of destroy)
        else if(savedInstanceState != null){
            String projectId = savedInstanceState.getString("projectId");
            loadProject(projectId);
        }

        //Init
        project = null;
        projectCollapsed = true;
        gson = new Gson();
        initUI();
        setUIEventListeners();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //Save current state
        if(project != null)
            outState.putString("projectId", project.id);

        super.onSaveInstanceState(outState);
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
        setTitle("");

        LayoutInflater inflater = getLayoutInflater();

        commentListView = (ListView) findViewById(R.id.activityProject_commentListView);
        detailsView = (ViewGroup) inflater.inflate(R.layout.item_project_details, commentListView, false);
        footerView = (ViewGroup) inflater.inflate(R.layout.item_more_comments, commentListView, false);

        collapseBtn = (ImageView) detailsView.findViewById(R.id.activityProject_collapseBtn);
        description = (TextView) detailsView.findViewById(R.id.activityProject_description);
        beneficiaryLayout = (RelativeLayout) detailsView.findViewById(R.id.activityProject_beneficiaryRelativeLayout);
        completionOfPaymentsLayout = (RelativeLayout) detailsView.findViewById(R.id.activityProject_completionOfPaymentsRelativeLayout);
        projectIdLayout = (RelativeLayout) detailsView.findViewById(R.id.activityProject_projectIdRelativeLayout);
    }

    //Set UI event listeners
    private void setUIEventListeners (){

        if(detailsView == null || footerView == null)
            return;

        //Collapse btn clicked
        ImageView collapseBtn = (ImageView) detailsView.findViewById(R.id.activityProject_collapseBtn);
        collapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleProject();
            }
        });

        //Rate btn clicked
        Button rateBtn = (Button) detailsView.findViewById(R.id.activityProject_ratingBtn);
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(project == null)
                    return;

                //Start RateActivity
                Intent i = new Intent(ProjectActivity.this, RateActivity.class);
                i.putExtra("projectId", project.id);
                i.putExtra("projectTitle", project.title_en);
                i.putExtra("projectRate", project.average_rating);
                startActivityForResult(i, RATE_PROJECT_REQUEST);

            }
        });

        //'Add comment' btn clicked
        LinearLayout addCommentBtn = (LinearLayout) detailsView.findViewById(R.id.newCommentView);
        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(project == null)
                    return;

                //Start CommentActivity
                Intent i = new Intent(ProjectActivity.this, CommentActivity.class);
                i.putExtra("projectId", project.id);
                startActivityForResult(i, COMMENT_PROJECT_REQUEST);

            }
        });

        //'Show all comments' btn clicked
        Button showAllBtn = (Button) footerView.findViewById(R.id.seeAllCommentsBtn);
        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadComments();
            }
        });

    }

    //Project was rated
    public void projectRated(float rating){

        if(project == null)
            return;

        project.rate(rating);

        //Update UI
        if(project.num_ratings != null){
            TextView numOfRatings= (TextView) detailsView.findViewById(R.id.activityProject_numOfRatings);
            numOfRatings.setText(Integer.toString(project.num_ratings));
        }
        if(project.average_rating != null){
            RatingBar averageRatingBar = (RatingBar) detailsView.findViewById(R.id.activityProject_ratingBar);
            averageRatingBar.setRating(project.average_rating);

            TextView averageRating = (TextView) detailsView.findViewById(R.id.activityProject_rating);
            averageRating.setText(String.format("%.1f", project.average_rating));
        }

    }

    //Project was commented
    public void projectCommented(Comment comment){

        if(project == null)
            return;

        //Update list of comments
        if(commentListAdapter != null)
            commentListAdapter.insert(comment, 0);

    }

    @Override
    public void onCommentLike(Comment comment) {

        YDSApiClient client = new YDSApiClient();
        Authenticator auth = new Authenticator();

        String username = auth.getUsername();
        client.likeComment(comment.id, username, new YDSApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {
            }

            @Override
            public void onFailure(Message message) {
                Util.showToast(message);
            }
        });
    }

    @Override
    public void onCommentDislike(Comment comment) {

        YDSApiClient client = new YDSApiClient();
        Authenticator auth = new Authenticator();

        String username = auth.getUsername();
        client.dislikeComment(comment.id, username, new YDSApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {
            }

            @Override
            public void onFailure(Message message) {
                Util.showToast(message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //RateActivity returned result
        if(requestCode == RATE_PROJECT_REQUEST && resultCode == Activity.RESULT_OK){

            float rating = data.getExtras().getFloat("rating");
            projectRated(rating);

            Util.log("Project rated: " + rating);
        }

        //CommentActivity returned result
        else if(requestCode == COMMENT_PROJECT_REQUEST && resultCode == Activity.RESULT_OK){

            Comment comment = gson.fromJson(data.getExtras().getString("comment"), Comment.class);
            projectCommented(comment);

            Util.log("Project commented: " + comment.text);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Back btn clicked
        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    //Toggle (collapse/expand) project details
    private void toggleProject(){

        if(projectCollapsed)
            expandProject();
        else
            collapseProject();
    }

    //Collapse project details
    private void collapseProject(){

        projectCollapsed = true;

        collapseBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_arrow_down));

        if(project.description_en != null)
            description.setText(collapseDescription(project.description_en));

        beneficiaryLayout.setVisibility(View.GONE);
        completionOfPaymentsLayout.setVisibility(View.GONE);
        projectIdLayout.setVisibility(View.GONE);

    }

    private String collapseDescription(String description){

        int descriptionCollapsedLength = getResources().getInteger(R.integer.COLLAPSED_DESCRIPTION_LENGTH);
        String collapsedDescription = description;

        if(description.length() > descriptionCollapsedLength){
            collapsedDescription = description.substring(0, descriptionCollapsedLength);
            collapsedDescription += "...";
        }

        return collapsedDescription;

    }

    //Expand project details
    private void expandProject(){

        projectCollapsed = false;

        collapseBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_arrow_up));

        if(project.description_en != null)
            description.setText(project.description_en);

        beneficiaryLayout.setVisibility(View.VISIBLE);
        completionOfPaymentsLayout.setVisibility(View.VISIBLE);
        projectIdLayout.setVisibility(View.VISIBLE);
    }

    //Load project
    private void loadProject(String projectId){

        hideContent();
        showLoader();

        YDSApiClient client = new YDSApiClient();
        client.getProjectDetails(projectId, new YDSApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {

                hideLoader();
                showContent();

                //Show project
                project = (ProjectDetails) object;
                showProject();
            }

            @Override
            public void onFailure(Message message) {
                hideLoader();
                Util.showToast(message);
            }
        });
    }

    //Show project
    private void showProject(){

        if(project == null || detailsView == null)
            return;

        String currency = App.getContext().getString(R.string.CURRENCY);

        //Set activity title
        setTitle(project.title_en);

        //Comments
        commentListAdapter = new CommentListAdapter(this, project.comments);
        commentListView.addHeaderView(detailsView, null, false);
        commentListView.addFooterView(footerView, null, false);
        commentListView.setAdapter(commentListAdapter);

        //Map
        attachMapFragment();

        //Title
        if(project.title_en != null){
            TextView title = (TextView) detailsView.findViewById(R.id.activityProject_title);
            title.setText(project.title_en);
        }

        //Description
        if(project.description_en != null){
            TextView description = (TextView) detailsView.findViewById(R.id.activityProject_description);
            description.setText(collapseDescription(project.description_en));
        }

        //Beneficiary
        if(project.buyer_translation_en != null && project.buyer_translation_en.size() > 0){
            TextView beneficiary = (TextView) detailsView.findViewById(R.id.activityProject_beneficiary);
            beneficiary.setText(project.buyer_translation_en.get(0));
        }

        //Completion (of payments)
        if(project.completionOfPayments != null){
            TextView completionOfPayments = (TextView) detailsView.findViewById(R.id.activityProject_completionOfPayments);
            completionOfPayments.setText(Integer.toString(project.completionOfPayments));
        }

        //Project Id
        if(project.projectId != null){
            TextView projectId = (TextView) detailsView.findViewById(R.id.activityProject_projectId);
            projectId.setText(project.projectId);
        }

        //Start date
        String startDate = project.getStartDate();
        if(startDate != null){
            TextView startDateTextView = (TextView) detailsView.findViewById(R.id.activityProject_startDate);
            startDateTextView.setText(startDate);
        }

        //End date
        String endDate = project.getEndDate();
        if(endDate != null){
            TextView endDateTextView = (TextView) detailsView.findViewById(R.id.activityProject_endDate);
            endDateTextView.setText(endDate);
        }

        //Budget
        if(project.hasBudgetAggregate_aggregatedAmount != null){
            TextView budget = (TextView) detailsView.findViewById(R.id.activityProject_budget);
            String budgetAsString = Util.convertToString(project.hasBudgetAggregate_aggregatedAmount);
            budget.setText(String.format("%s %s", budgetAsString, currency));
        }

        //Num of ratings
        if(project.num_ratings != null){
            TextView numOfRatings= (TextView) detailsView.findViewById(R.id.activityProject_numOfRatings);
            numOfRatings.setText(Integer.toString(project.num_ratings));
        }

        //Rating
        if(project.average_rating != null){
            RatingBar averageRatingBar = (RatingBar) detailsView.findViewById(R.id.activityProject_ratingBar);
            averageRatingBar.setRating(project.average_rating);

            TextView averageRating = (TextView) detailsView.findViewById(R.id.activityProject_rating);
            averageRating.setText(Util.convertToString(project.average_rating));
        }

        //'Show all comments' btn
        if(project.num_comments > 0)
            showLoadCommentsBtn();

    }

    //Load all comments
    private void loadComments(){

        if(project == null || commentListAdapter == null || footerView == null)
            return;

        showLoader();

        YDSApiClient client = new YDSApiClient();
        client.getProjectComments(project.id, new YDSApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {

                hideLoader();

                //Show all comments
                project.comments = (List<Comment>) object;
                commentListAdapter.refresh(project.comments );

                //Hide 'Load all comments' btn
                hideLoadCommentsBtn();

            }

            @Override
            public void onFailure(Message message) {
                Util.showToast(message);
            }
        });
    }

    //Attach map fragment
    private void attachMapFragment(){

        ProjectMapFragment projectMapFragment = ProjectMapFragment.newInstance(gson.toJson(project.getPoints()));
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activityProject_mapFragment, projectMapFragment);
        ft.commit();
    }

    //Show content
    private void showContent(){

        ListView details = (ListView) findViewById(R.id.activityProject_commentListView);
        details.setVisibility(View.VISIBLE);
    }

    //Hide content
    private void hideContent(){

        ListView details = (ListView) findViewById(R.id.activityProject_commentListView);
        details.setVisibility(View.INVISIBLE);
    }

    //Show loader
    private void showLoader(){

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activityProject_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    //Hide loader
    private void hideLoader(){

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activityProject_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    //Show 'Load all comments' btn
    private void showLoadCommentsBtn(){

        Button showAllBtn = (Button) footerView.findViewById(R.id.seeAllCommentsBtn);
        showAllBtn.setVisibility(View.VISIBLE);
    }

    //Hide 'Load all comments' btn
    private void hideLoadCommentsBtn(){

        Button showAllBtn = (Button) footerView.findViewById(R.id.seeAllCommentsBtn);
        showAllBtn.setVisibility(View.GONE);
    }
}
