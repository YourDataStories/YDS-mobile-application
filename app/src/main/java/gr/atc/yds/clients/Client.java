package gr.atc.yds.clients;

import java.io.IOException;
import java.net.SocketTimeoutException;

import gr.atc.yds.enums.Message;

/**
 * Created by ipapas on 20/12/16.
 */

public class Client {

    //Client Callback
    public interface ResponseListener {
        public void onSuccess(Object object);
        public void onFailure(Message message);
    }

    protected void handleFailure(Throwable t, ResponseListener responseListener){

        if (t instanceof SocketTimeoutException)
            responseListener.onFailure(Message.TIMEOUT);

        else if (t instanceof IOException)
            responseListener.onFailure(Message.NO_INTERNET_CONNECTION);

        else
            responseListener.onFailure(Message.SOMETHING_WENT_WRONG);

    }
}
