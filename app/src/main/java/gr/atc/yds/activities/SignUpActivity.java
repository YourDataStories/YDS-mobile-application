package gr.atc.yds.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setUIEventListeners();
    }

    //Set UI event listeners
    private void setUIEventListeners (){

        //SignUp button clicked
        Button signUpBtn = (Button) findViewById(R.id.activitySignUp_signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = null;
                String password = null;
                String confirmPassword = null;

                //Read username
                EditText signUpUsername = (EditText) findViewById(R.id.activitySignUp_username);
                username = signUpUsername.getText().toString();

                //Read password
                EditText signUpPassword = (EditText) findViewById(R.id.activitySignUp_password);
                password = signUpPassword.getText().toString();

                //Read confirm password
                EditText signUpConfirmPassword = (EditText) findViewById(R.id.activitySignUp_repeatPassword);
                confirmPassword = signUpConfirmPassword.getText().toString();

                //Check if the required fields have been completed by the user
                if (Form.allFieldsAreCompleted(signUpUsername, signUpPassword, signUpConfirmPassword)) {

                    //Check if password and confirm are the same
                    if(password.equals(confirmPassword)){

                        //Sign up
                        showLoader();
                        Authenticator auth = new Authenticator();
                        auth.signUp(username, password, new Authenticator.ResponseListener() {
                            @Override
                            public void onSuccess() {

                                hideLoader();

                                //Start HomeActivity
                                Intent i = new Intent(SignUpActivity.this, HomeActivity.class);
                                startActivity(i);

                            }

                            @Override
                            public void onFailure(Message message) {
                                hideLoader();
                                Util.showToast(message);
                            }
                        });
                    }
                    else
                        Util.showToast(App.getContext().getString(R.string.passwordAndConfirmDontMatch));

                }

            }
        });

        //SignIn link clicked
        TextView signInBtn = (TextView) findViewById(R.id.activitySignUp_signInLink);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Start SignInActivity
                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(i);

            }
        });
    }

    //Show loader
    private void showLoader(){
        Util.hideSoftKeyboard(this);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activitySignUp_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    //Hide loader
    private void hideLoader(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activitySignUp_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
