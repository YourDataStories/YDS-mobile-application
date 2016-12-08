package gr.atc.yds.controllers;

import android.app.Application;
import android.content.Context;

import gr.atc.yds.R;

/**
 * Created by ipapas on 07/12/16.
 */
public class App extends Application {

    //Config variables
    public static String logTag;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        initConfigVars();
    }

    //Get context
    public static Context getContext(){
        return context;
    }

    //Initialize config variables
    private void initConfigVars(){

        logTag = getString(R.string.LOG_TAG);

    }

}
