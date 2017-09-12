package gr.atc.yds.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import gr.atc.yds.R;
import gr.atc.yds.controllers.Authenticator;
import gr.atc.yds.services.CloseProjectService;

public class LauncherActivity extends AppCompatActivity {

    private Authenticator auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        auth = Authenticator.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //User has logged in
        if(auth.isUserLoggedIn()){

            //Redirect him to HomeActivity
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        }

        //User has NOT logged in
        else{

            //Redirect him to SignInActivity
            Intent i = new Intent(this, SignInActivity.class);
            startActivity(i);
        }
    }
}
