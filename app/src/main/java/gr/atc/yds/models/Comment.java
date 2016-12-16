package gr.atc.yds.models;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by ipapas on 12/12/16.
 */

public class Comment {

    public enum Type {
        YDS,
        RSS,
        Twitter
    }

    public enum Reaction {
        LIKE,
        DISLIKE
    }

    public int id;
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

        this.type = type;
        this.created_by = created_by;
        this.text = text;

        this.likes = 0;
        this.dislikes = 0;
        this.reaction = null;

        this.created = new Date(System.currentTimeMillis()).toString();
    }

    //Like comment
    public void like(){

        if(reaction != null)
            return;

        reaction = Reaction.LIKE;
        likes++;
    }

    //Dislike comment
    public void dislike(){

        if(reaction != null)
            return;

        reaction = Reaction.DISLIKE;
        dislikes++;
    }

    //Get time-ago format of timestamp
    public String getTimeago(){
        return "3 min ago";
    }
}
