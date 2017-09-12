package gr.atc.yds.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import gr.atc.yds.controllers.Authenticator;
import gr.atc.yds.enums.Message;
import gr.atc.yds.services.CloseProjectService;
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
        Authenticator auth = Authenticator.getInstance();
        if(!auth.isUserLoggedIn())
            finish();
    }

    //Logout
    protected void logout(){

        //Logout
        Authenticator auth = Authenticator.getInstance();
        auth.signOut(new Authenticator.ResponseListener() {
            @Override
            public void onSuccess() {

                //Start SignInActivity
                Intent i = new Intent(PrivateActivity.this, SignInActivity.class);
                startActivity(i);
            }

            @Override
            public void onFailure(Message message) {
                Util.showToast(message);
            }
        });

        //Stop CloseProjectService
        CloseProjectService.stop(this);
    }
}
