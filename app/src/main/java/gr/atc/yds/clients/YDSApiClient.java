package gr.atc.yds.clients;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.enums.Message;
import gr.atc.yds.models.Comment;
import gr.atc.yds.models.Project;
import gr.atc.yds.models.ProjectDetails;
import gr.atc.yds.utils.Util;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by ipapas on 08/12/16.
 */
public class YDSApiClient extends Client{

    private static final String YDSBaseUrl = App.getContext().getString(R.string.YDS_BASE_URL);
    private WebService service;
    private Gson gson;

    //Representation of YDS remote web services
    public interface WebService {

        //Get projects
        @GET("projects/getProjects")
        Call<ResponseBody> getProjects();

        //Get project details
        @GET("projects/getProjectDetails")
        Call<ResponseBody> getProjectDetails(@Query("projectId") Long projectId,
                                             @Query("username") String username);

        //Get project comments
        @GET("5853ccae0f00001c0dc731e2")
        Call<ResponseBody> getProjectComments();

        //Rate project
        @POST("584ff7282a0000dd20e8f571")
        Call<ResponseBody> rateProject();

        //Comment project
        @POST("585158ec0f00002f0a046cc4")
        Call<ResponseBody> commentProject();

        //Like comment
        @POST("585158ec0f00002f0a046cc4")
        Call<ResponseBody> likeComment();

        //Dislike comment
        @POST("585158ec0f00002f0a046cc4")
        Call<ResponseBody> dislikeComment();


    }

    public YDSApiClient(){

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

                        //Get projects
                        JSONObject responseBodyJSONObject = new JSONObject(response.body().string());
                        String projectsString = responseBodyJSONObject.getString("docs");
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
                handleFailure(t, responseListener);
            }
        });
    }

    //Get project details
    public void getProjectDetails(Long projectId, String username, final ResponseListener responseListener){

        Call<ResponseBody> call = service.getProjectDetails(projectId, "test");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    //Http == 200
                    if (response.isSuccessful()) {

                        //Get project details
                        String responseBodyString = response.body().string();
                        ProjectDetails project = gson.fromJson(responseBodyString, ProjectDetails.class);

                        responseListener.onSuccess(project);

                    }

                    //Http != 200
                    else
                        responseListener.onFailure(Message.SOMETHING_WENT_WRONG);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleFailure(t, responseListener);
            }
        });
    }

    //Get project comments
    public void getProjectComments(Long projectId, final ResponseListener responseListener){

        Call<ResponseBody> call = service.getProjectComments();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    //Http == 200
                    if (response.isSuccessful()) {

                        JSONObject responseBodyJSONObject = new JSONObject(response.body().string());

                        //Get comments
                        String commentsString = responseBodyJSONObject.getJSONObject("response").getString("comments");
                        List<Comment> comments = gson.fromJson(commentsString, new TypeToken<List<Comment>>(){}.getType());

                        responseListener.onSuccess(comments);

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
                handleFailure(t, responseListener);
            }
        });
    }

    //Rate project
    public void rateProject(String projectId, float rating, String username, final ResponseListener responseListener){

        Call<ResponseBody> call = service.rateProject();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Http == 200
                if (response.isSuccessful())
                    responseListener.onSuccess(null);

                //Http != 200
                else
                    responseListener.onFailure(Message.SOMETHING_WENT_WRONG);

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleFailure(t, responseListener);
            }
        });
    }

    //Comment project
    public void commentProject(String projectId, Comment comment, String username, final ResponseListener responseListener){

        Call<ResponseBody> call = service.commentProject();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Http == 200
                if (response.isSuccessful())
                    responseListener.onSuccess(null);

                //Http != 200
                else
                    responseListener.onFailure(Message.SOMETHING_WENT_WRONG);

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleFailure(t, responseListener);
            }
        });
    }

    //Like comment
    public void likeComment(int commentId, String username, final ResponseListener responseListener){

        Call<ResponseBody> call = service.likeComment();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Http == 200
                if (response.isSuccessful())
                    responseListener.onSuccess(null);

                    //Http != 200
                else
                    responseListener.onFailure(Message.SOMETHING_WENT_WRONG);

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleFailure(t, responseListener);
            }
        });
    }

    //Dislike comment
    public void dislikeComment(int commentId, String username, final ResponseListener responseListener){

        Call<ResponseBody> call = service.dislikeComment();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Http == 200
                if (response.isSuccessful())
                    responseListener.onSuccess(null);

                    //Http != 200
                else
                    responseListener.onFailure(Message.SOMETHING_WENT_WRONG);

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleFailure(t, responseListener);
            }
        });
    }
}
