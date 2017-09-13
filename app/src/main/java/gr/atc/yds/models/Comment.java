package gr.atc.yds.models;

import java.sql.Timestamp;

import gr.atc.yds.utils.Util;

/**
 * Created by ipapas on 12/12/16.
 */

public class Comment {

    public enum Type {
        YDS,
        RSS,
        NEWS,
        BLOG,
        Tweet
    }

    public enum Reaction {
        like,
        dislike
    }

    public Integer id;
    public Type type;
    public String created_by;
    public String text;
    public String created;
    public Integer likes;
    public Integer dislikes;
    public Reaction reaction;

    public Comment(){

    }

    public Comment(Type type, String created_by, String text){

        this.id = null;
        this.type = type;
        this.created_by = created_by;
        this.text = text;

        this.likes = 0;
        this.dislikes = 0;
        this.reaction = null;

        this.created();
    }

    private void created(){

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.created = Util.convertTimestampToDate(timestamp);
    }

    //Like comment
    public void like(){

        if(reaction != null)
            return;

        reaction = Reaction.like;
        likes++;
    }

    //Dislike comment
    public void dislike(){

        if(reaction != null)
            return;

        reaction = Reaction.dislike;
        dislikes++;
    }

    //Get time-ago format of timestamp
    public String getTimeago(){
        return Util.getTimeago(created);
    }
}
