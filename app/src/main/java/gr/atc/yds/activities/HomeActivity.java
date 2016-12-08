package gr.atc.yds.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import gr.atc.yds.R;
import gr.atc.yds.controllers.Authenticator;
import gr.atc.yds.enums.Message;
import gr.atc.yds.utils.Util;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Set toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.logout:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Logout
    private void logout(){

        Authenticator auth = new Authenticator();
        auth.signOut(new Authenticator.ResponseListener() {
            @Override
            public void onSuccess() {

                //Start SignInActivity
                Intent i = new Intent(HomeActivity.this, SignInActivity.class);
                startActivity(i);
            }

            @Override
            public void onFailure(Message message) {
                Util.showToast(message);
            }
        });
    }
}
