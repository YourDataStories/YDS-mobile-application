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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import gr.atc.yds.R;
import gr.atc.yds.clients.YDSApiClient;
import gr.atc.yds.controllers.Authenticator;
import gr.atc.yds.enums.Message;
import gr.atc.yds.models.Comment;
import gr.atc.yds.utils.Form;
import gr.atc.yds.utils.Util;

public class CommentActivity extends PrivateActivity {

    private Long projectId;
    private Authenticator auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //Get arguments
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            projectId = getIntent().getExtras().getLong("projectId");
        else
            projectId = null;

        //Init
        initUI();
        setUIEventListeners();
        auth = Authenticator.getInstance();

    }

    //Initialize UI
    public void initUI(){

        //Set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Add back btn to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Set title
        setTitle(getString(R.string.activityCommentTitle));
    }

    //Set UI event listeners
    private void setUIEventListeners (){

        //'Add comment' btn clicked
        Button submitBtn = (Button) findViewById(R.id.activityComment_submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                //Back btn clicked
                finish();

        }

        return super.onOptionsItemSelected(item);
    }

    //Add comment
    private void addComment(){

        if(projectId == null || !auth.isUserLoggedIn()){
            logout();
            return;
        }

        //Validate form
        EditText commentEditText = (EditText) findViewById(R.id.activityComment_commentEditText);
        if(!Form.allFieldsAreCompleted(commentEditText))
            return;

        String username = auth.getUsername();

        String text = commentEditText.getText().toString();
        final Comment comment = new Comment(Comment.Type.YDS, username, text);

        //Submit comment
        showLoader();
        YDSApiClient client = YDSApiClient.getInstance();
        client.commentProject(projectId, comment, new YDSApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {

                hideLoader();

                //Send result to ProjectActivity
                Intent data = new Intent();
                data.putExtra("comment", new Gson().toJson(comment));

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

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activityComment_progressBar);
        progressBar.setVisibility(View.VISIBLE);

    }

    //Hide loader
    private void hideLoader(){

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activityComment_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

    }
}
