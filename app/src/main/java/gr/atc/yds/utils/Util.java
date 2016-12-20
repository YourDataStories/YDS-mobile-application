package gr.atc.yds.utils;

import android.app.Activity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.kinnonii.timeago.TimeAgo;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.enums.Message;

/**
 * Created by ipapas on 07/12/16.
 */
public class Util {

    private final static String DATE_TIME_FORMAT = App.getContext().getString(R.string.DATE_TIME_FORMAT);

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

            case NO_INTERNET_CONNECTION:
                textMessage = App.getContext().getString(R.string.noInternetConnection);
                break;

            case TIMEOUT:
                textMessage = App.getContext().getString(R.string.timeOut);
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

    //Convert timeStamp to dateTime
    public static String convertTimestampToDate(Timestamp timestamp){

        Date date = new Date(timestamp.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

        return simpleDateFormat.format(date);
    }

    //Compute timeAgo of dateTime
    public static String getTimeago(String dateAsString){

        Util.log("input: " + dateAsString);
        Util.log("format: " + DATE_TIME_FORMAT);

        try {

            DateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date = format.parse(dateAsString);

            TimeAgo time = new TimeAgo("en");
            String timeAgo = time.timeAgo(date);

            Util.log("output: " + timeAgo);

            return timeAgo;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


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

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        String numberAsString = decimalFormat.format(number);

        return numberAsString;
    }

    //Convert Float to String
    public static String convertToString(Float number){

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        String numberAsString = decimalFormat.format(number);

        return numberAsString;
    }


}
