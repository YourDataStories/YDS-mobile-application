package gr.atc.yds.models;

/**
 * Created by ipapas on 08/12/16.
 */
public class Token {

    public String accessToken;
    public Long expireTimestamp; //Seconds

    public Token(){

    }
    public Token(String accessToken, Long expiresInSeconds){

        //Current timestamp
        Long currentTimestamp = System.currentTimeMillis()/1000;

        this.accessToken = accessToken;
        this.expireTimestamp = currentTimestamp + expiresInSeconds;
    }

    //Check if Token expired
    public boolean isExpired(){

        //Current timestamp
        Long currentTimestamp = System.currentTimeMillis()/1000;

        if(currentTimestamp < expireTimestamp)
            return false;

        return true;
    }
}
