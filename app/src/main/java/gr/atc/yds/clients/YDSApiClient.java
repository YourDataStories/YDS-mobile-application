package gr.atc.yds.clients;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

import gr.atc.yds.enums.Message;
import gr.atc.yds.models.Project;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

/**
 * Created by ipapas on 08/12/16.
 */
public class YDSApiClient {

    private WebService service;
    private String YDSBaseUrl;
    private Gson gson;

    //Representation of YDS remote web services
    public interface WebService {

        //Get projects
        @GET("584a818a1000009e0efb0181")
        Call<ResponseBody> getProjects();

        //Get project details
        @GET("584152c81000004901bb4c1c")
        Call<ResponseBody> getProjectDetails();

        //Get project comments
        @GET("58415657100000ae01bb4c23")
        Call<ResponseBody> getProjectComments();

        //Get comment details
        @GET("584156c0100000df01bb4c25")
        Call<ResponseBody> getCommentDetails();


    }

    //Client Callback
    public interface ResponseListener {
        public void onSuccess(Object object);
        public void onFailure(Message message);
    }

    public YDSApiClient(){

        YDSBaseUrl = "http://www.mocky.io/v2/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YDSBaseUrl)
                .build();
        service = retrofit.create(WebService.class);

        gson = new Gson();
    }

    //Get projects
    public void getProjects(final ResponseListener responseListener){

        Call<ResponseBody> call = service.getProjects();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    //Http == 200
                    if (response.isSuccessful()) {

                        JSONObject responseBodyJSONObject = new JSONObject(response.body().string());

                        //Get projects
                        String projectsString = responseBodyJSONObject.getJSONObject("response").getString("docs");
                        List<Project> projects = gson.fromJson(projectsString, new TypeToken<List<Project>>(){}.getType());

                        responseListener.onSuccess(projects);

                    }

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
                responseListener.onFailure(Message.SOMETHING_WENT_WRONG);
            }
        });
    }
}
