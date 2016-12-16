package gr.atc.yds.models;

import java.util.List;

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
    public List<String> buyer_translation_en;
    public List<Comment> comments;
    public Boolean rated;

    public ProjectDetails(){

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



}
