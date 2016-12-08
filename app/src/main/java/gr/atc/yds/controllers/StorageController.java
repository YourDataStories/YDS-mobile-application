package gr.atc.yds.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import gr.atc.yds.models.Token;

/**
 * Created by ipapas on 12/8/15.
 */
public class StorageController {

    private SharedPreferences prefs;
    private Gson gson;

    //Constructor
    public StorageController(){
        Context context = App.getContext();
        prefs = context.getSharedPreferences("gr.atc.yds", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    //Save data
    public void saveData(String key, Object data){

        //Serialize
        String dataString = gson.toJson(data);

        //Save
        prefs.edit().remove(key).putString(key, dataString).apply();

    }

    //Load data
    public Object loadData(String key, Class<?> type){

        //Load
        String dataString = prefs.getString(key, "");

        //Not found
        if(dataString == null)
            return null;

        //Deserialize
        return gson.fromJson(dataString, type);

    }

    //Delete data
    public void deleteData(String key){

        prefs.edit().remove(key).apply();

    }
}
