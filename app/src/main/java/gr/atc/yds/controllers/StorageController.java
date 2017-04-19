package gr.atc.yds.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by ipapas on 12/8/15.
 */
public class StorageController {

    private static StorageController storageController = null;
    private SharedPreferences prefs;
    private Gson gson;

    //Constructor
    private StorageController(){
        Context context = App.getContext();
        prefs = context.getSharedPreferences("gr.atc.yds", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    //Get instance (singleton pattern)
    public static StorageController getInstance(){
        if(storageController == null)
            storageController = new StorageController();

        return storageController;
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
