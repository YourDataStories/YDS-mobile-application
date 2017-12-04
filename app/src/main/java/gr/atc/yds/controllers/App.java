package gr.atc.yds.controllers;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import gr.atc.yds.R;
import gr.atc.yds.activities.HomeActivity;
import gr.atc.yds.activities.SignInActivity;
import gr.atc.yds.enums.Message;
import gr.atc.yds.utils.Util;

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
        initImageLoader();
    }

    //Get context
    public static Context getContext(){
        return context;
    }

    //Initialize config variables
    private void initConfigVars(){

        logTag = getString(R.string.LOG_TAG);

    }

    //Initialize ImageLoader
    private void initImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }
}
