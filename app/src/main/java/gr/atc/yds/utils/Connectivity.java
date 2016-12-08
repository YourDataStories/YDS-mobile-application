package gr.atc.yds.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import java.net.InetAddress;

import gr.atc.yds.controllers.App;

/**
 * Created by ipapas on 1/19/16.
 */
public class Connectivity {

    //Enumerations
    public enum ConnectivityStatus {
        OK,
        NO_NETWORK_CONNECTION,
        NO_INTERNET_CONNECTION
    }


    //Return connectivity status
    public static ConnectivityStatus getConnectivityStatus(){

        //Check network connection
        if(!isNetworkConnected())
            return ConnectivityStatus.NO_NETWORK_CONNECTION;


        //Check internet connection
        if(!isInternetAvailable())
            return ConnectivityStatus.NO_INTERNET_CONNECTION;


        return ConnectivityStatus.OK;

    }

    //Check if the android device is connected to a network
    public static boolean isNetworkConnected() {

        Context context = App.getContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    //Check if the android device is connected to the internet
    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

}
