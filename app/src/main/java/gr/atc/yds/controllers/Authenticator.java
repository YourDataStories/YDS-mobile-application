package gr.atc.yds.controllers;

import gr.atc.yds.clients.HeadsApiClient;
import gr.atc.yds.enums.Message;
import gr.atc.yds.models.Token;

/**
 * Created by ipapas on 07/12/16.
 */
public class Authenticator {

    StorageController storage;

    public Authenticator(){
        storage = new StorageController();
    }

    //Interfaces
    public interface ResponseListener {
        public void onSuccess();
        public void onFailure(Message message);
    }

    //Check if user has logged in
    public boolean isUserLoggedIn(){

        //Get potential saved token
        Token token = (Token) storage.loadData("token", Token.class);

        //Token does not exist
        if(token == null)
            return false;

        //Token is expired
        if(token.isExpired()){
            signOut();
            return false;
        }


        return true;
    }

    //Get logged-in user's username
    public String getUsername(){

        if(isUserLoggedIn()){

            Token token = (Token) storage.loadData("token", Token.class);
            return token.owner;
        }

        return null;

    }

    //Sign In
    public void signIn(String username, String password, final ResponseListener responseListener){

        HeadsApiClient client = new HeadsApiClient();
        client.signIn(username, password, new HeadsApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {

                //Save token
                Token token = (Token) object;
                StorageController storage = new StorageController();
                storage.saveData("token", token);

                responseListener.onSuccess();
            }

            @Override
            public void onFailure(Message message) {
                responseListener.onFailure(message);
            }
        });
    }

    //Sign Out
    public void signOut(){

        //Delete saved token
        StorageController storage = new StorageController();
        storage.deleteData("token");
    }
    public void signOut(final ResponseListener responseListener){

        signOut();
        responseListener.onSuccess();

    }

    //Sign Up
    public void signUp(final String username, final String password, final ResponseListener responseListener){

        HeadsApiClient client = new HeadsApiClient();
        client.signUp(username, password, new HeadsApiClient.ResponseListener() {
            @Override
            public void onSuccess(Object object) {

                //Sign In
                signIn(username, password, responseListener);
            }

            @Override
            public void onFailure(Message message) {
                responseListener.onFailure(message);
            }
        });

    }




}
