package gr.atc.yds.models;

import java.util.List;

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
    public String buyer_translation_en;
    public List<Comment> lastComments;

    public ProjectDetails(){

    }

}
