package gr.atc.yds.utils;

import android.widget.EditText;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;

/**
 * Created by ipapas on 11/30/15.
 */
public class Form {

    //Check if fields have been completed by the user
    public static boolean allFieldsAreCompleted(EditText ... editTexts){

        for(EditText editText : editTexts)
            if(editText.getText().toString().trim().equals("")) {
                editText.setError(App.getContext().getResources().getString(R.string.requiredFieldErrorMessage));
                return false;
            }
        return true;

    }
}
