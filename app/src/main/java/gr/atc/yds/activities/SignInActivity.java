package gr.atc.yds.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.controllers.Authenticator;
import gr.atc.yds.enums.Message;
import gr.atc.yds.utils.Form;
import gr.atc.yds.utils.Util;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setUIEventListeners();


    }

    //Set UI event listeners
    private void setUIEventListeners (){

        //SignIn button clicked
        Button signInBtn = (Button) findViewById(R.id.activitySignIn_signInBtn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = null;
                String password = null;

                //Read username
                EditText signInUsername = (EditText) findViewById(R.id.activitySignIn_username);
                username = signInUsername.getText().toString();

                //Read password
                EditText signInPassword = (EditText) findViewById(R.id.activitySignIn_password);
                password = signInPassword.getText().toString();

                //Check if the required fields have been completed by the user
                if (Form.allFieldsAreCompleted(signInUsername, signInPassword)) {

                    //Sign in
                    showLoader();
                    Authenticator auth = new Authenticator();
                    auth.signIn(username, password, new Authenticator.ResponseListener() {
                        @Override
                        public void onSuccess() {

                            hideLoader();

                            //Start HomeActivity
                            Intent i = new Intent(SignInActivity.this, HomeActivity.class);
                            startActivity(i);

                        }

                        @Override
                        public void onFailure(Message message) {
                            hideLoader();
                            Util.showToast(message);
                        }
                    });

                }

            }
        });

        //SignUp link clicked
        TextView signUpBtn = (TextView) findViewById(R.id.activitySignIn_signUpLink);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Start SignUpActivity
                Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(i);

            }
        });
    }

    //Show loader
    private void showLoader(){
        Util.hideSoftKeyboard(this);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activitySignIn_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    //Hide loader
    private void hideLoader(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activitySignIn_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
