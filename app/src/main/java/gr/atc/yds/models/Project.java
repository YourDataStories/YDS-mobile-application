package gr.atc.yds.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gr.atc.yds.utils.Util;

/**
 * Created by ipapas on 08/12/16.
 */
public class Project {

    public String id;
    public Long projectId;
    public Float average_rating;
    public Integer num_ratings;
    public Integer num_comments;
    public String title_el;
    public String title_en;
    public Long hasBudgetAggregate_aggregatedAmount;
    public String hasRelatedFeature_hasGeometry_asWKT;

    public Project(){

    }

    //Get title
    public String getTitle(){

        if(title_en != null)
            return title_en;

        if(title_el != null)
            return title_el;

        return null;
    }

    //Get project's LatLng points
    public List<LatLng> getPoints(){
        return convertStringToPoints(hasRelatedFeature_hasGeometry_asWKT);
    }

    //Convert Line String to LatLng Points
    private List<LatLng> convertStringToPoints(String lineString){

        List<LatLng> latLngList = new ArrayList<LatLng>();

        if(lineString == null)
            return latLngList;

        lineString = lineString.replace("LINESTRING","");
        lineString = lineString.replace("POINT","");
        lineString = lineString.replace("(","");
        lineString = lineString.replace(")","");

        //Log.d(tag, "lineString: " + lineString);

        List<String> stringLatLngList = Arrays.asList(lineString.split(","));

        for(String stringLatLng : stringLatLngList) {

            String[] stringCoordinates = stringLatLng.split("\\s+");

            if(stringCoordinates.length == 2){

                try{

                    Double lng = Double.parseDouble(stringCoordinates[0]);
                    Double lat = Double.parseDouble(stringCoordinates[1]);
                    LatLng latLng = new LatLng(lat, lng);
                    latLngList.add(latLng);

                }catch(Exception e){
                    e.printStackTrace();
                    continue;
                }

            }

        }

        return latLngList;

    }

}
