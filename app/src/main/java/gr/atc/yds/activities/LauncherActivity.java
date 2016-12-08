package gr.atc.yds.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import gr.atc.yds.R;
import gr.atc.yds.controllers.Authenticator;

public class LauncherActivity extends AppCompatActivity {

    private Authenticator auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        auth = new Authenticator();
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
