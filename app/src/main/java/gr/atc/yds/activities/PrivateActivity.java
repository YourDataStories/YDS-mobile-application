package gr.atc.yds.activities;

import android.support.v7.app.AppCompatActivity;

import gr.atc.yds.controllers.Authenticator;
import gr.atc.yds.utils.Util;

/**
 * Created by ipapas on 20/12/16.
 */

//Accessible only by logged in users
public class PrivateActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();

        //Allow access only to logged-in users
        Authenticator auth = new Authenticator();
        if(!auth.isUserLoggedIn())
            finish();

    }
}
