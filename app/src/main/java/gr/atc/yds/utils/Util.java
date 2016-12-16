package gr.atc.yds.utils;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.enums.Message;

/**
 * Created by ipapas on 07/12/16.
 */
public class Util {

    //Show log message
    public static void log(String message){
        Log.i(App.logTag, message);
    }

    //Show toast message
    public static void showToast(String message){
        Toast.makeText(App.getContext(), message, Toast.LENGTH_LONG).show();
    }
    public static void showToast(Message message){

        String textMessage = null;

        switch (message){

            case INVALID_CREDENTIALS:
                textMessage = App.getContext().getString(R.string.invalidCredentials);
                break;

            default:
                textMessage = App.getContext().getString(R.string.somethingWentWrong);
        }

        showToast(textMessage);
    }

    //Hide soft keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    //Beautify date
    public static String beautifyDate(String date){

        if(date == null)
            return null;

        String beautifiedDate = null;
        String[] splittedDate = date.split("T")[0].split("-");

        if(splittedDate.length == 3){
            beautifiedDate = String.format("%s/%s/%s", splittedDate[2], splittedDate[1], splittedDate[0]);
        }

        return beautifiedDate;
    }

    //Convert Long to String
    public static String convertToString(Long number){

        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        String numberAsString = decimalFormat.format(number);

        return numberAsString;
    }


}
