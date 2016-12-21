package gr.atc.yds.models;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.utils.Util;

/**
 * Created by ipapas on 12/12/16.
 */

public class ProjectDetails extends Project {

    public String description_el;
    public String description_en;
    public String startDate;
    public String endDate;
    public Integer completionOfPayments;
    public String buyer_name;
    public List<String> buyer_translation_en;
    public List<Comment> comments;
    public Float user_rating;

    public ProjectDetails(){

    }

    //Get description
    public String getDescription(){

        if(description_en != null)
            return description_en;

        if(description_el != null)
            return description_el;

        return null;
    }

    //Get collapsed description
    public String getCollapsedDescription(){

        return collapseDescription(getDescription());

    }

    //Get buyer
    public String getBuyer(){

        if(buyer_translation_en != null && buyer_translation_en.size() > 0)
            return buyer_translation_en.get(0);

        if(buyer_name != null)
            return buyer_name;

        return null;
    }

    //Add comment
    public void addComment(Comment comment){

        comments.add(0, comment);
    }

    //Get beautified start data
    public String getStartDate(){
        return Util.beautifyDate(startDate);
    }

    //Get beautified end data
    public String getEndDate(){
        return Util.beautifyDate(endDate);
    }

    //Collapse description
    private String collapseDescription(String description){

        int descriptionCollapsedLength = App.getContext().getResources().getInteger(R.integer.COLLAPSED_DESCRIPTION_LENGTH);
        String collapsedDescription = description;

        if(description != null && description.length() > descriptionCollapsedLength){
            collapsedDescription = description.substring(0, descriptionCollapsedLength);
            collapsedDescription += "...";
        }

        return collapsedDescription;

    }



}
