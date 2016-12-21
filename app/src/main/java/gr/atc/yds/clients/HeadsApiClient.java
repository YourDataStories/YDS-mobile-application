package gr.atc.yds.clients;

import org.json.JSONObject;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.enums.Message;
import gr.atc.yds.models.Token;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by ipapas on 08/12/16.
 */

public class HeadsApiClient extends Client{

    private static final String HeadsBaseUrl = App.getContext().getString(R.string.HEADS_BASE_URL);
    private WebService service;

    //Representation of Heads remote web services
    public interface WebService {

        //SignIn
        @FormUrlEncoded
        @POST("token")
        Call<ResponseBody> signIn(@Field("username") String username,
                                  @Field("password") String password,
                                  @Field("confirmPassword") String confirmPassword,
                                  @Field("grant_type") String grantType);


        //SignUp
        @FormUrlEncoded
        @POST("api/Account/Register")
        Call<ResponseBody> signUp(@Field("username") String username,
                                  @Field("password") String password,
                                  @Field("confirmPassword") String confirmPassword);

    }

    public HeadsApiClient(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HeadsBaseUrl)
                .build();

        service = retrofit.create(WebService.class);
    }

    //Sign In
    public void signIn(final String username, String password, final ResponseListener responseListener) {

        Call<ResponseBody> call = service.signIn(username, password, password, "password");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    //Http == 200
                    if (response.isSuccessful()) {

                        JSONObject responseBodyJSONObject = new JSONObject(response.body().string());

                        //Get token
                        String accessToken = responseBodyJSONObject.getString("access_token");
                        Long expiresIn = responseBodyJSONObject.getLong("expires_in");
                        Token token = new Token(username, accessToken, expiresIn);

                        responseListener.onSuccess(token);

                    }

                    //Http != 200
                    else {

                        JSONObject responseBodyJSONObject = new JSONObject(response.errorBody().string());
                        String error = responseBodyJSONObject.getString("error");

                        if(error.equalsIgnoreCase("invalid_grant") )
                            responseListener.onFailure(Message.INVALID_CREDENTIALS);
                        else
                            responseListener.onFailure(Message.SOMETHING_WENT_WRONG);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    responseListener.onFailure(Message.SOMETHING_WENT_WRONG);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleFailure(t, responseListener);
            }
        });

    }

    //Sign Up
    public void signUp(String username, String password, final ResponseListener responseListener) {

        Call<ResponseBody> call = service.signUp(username, password, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    //Http == 200
                    if (response.isSuccessful())
                        responseListener.onSuccess(null);


                    //Http != 200
                    else
                        responseListener.onFailure(Message.SOMETHING_WENT_WRONG);



                } catch (Exception e) {
                    e.printStackTrace();
                    responseListener.onFailure(Message.SOMETHING_WENT_WRONG);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleFailure(t, responseListener);
            }
        });

    }
}
