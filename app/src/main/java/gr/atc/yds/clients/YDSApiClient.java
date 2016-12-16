package gr.atc.yds.clients;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

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
        @GET("584e80f7120000270b3949fb")
        Call<ResponseBody> getProjects();

        //Get project details
        @GET("5853cbec0f00000e0dc731de")
        Call<ResponseBody> getProjectDetails();

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

    //Get project details
    public void getProjectDetails(String projectId, final ResponseListener responseListener){

        Call<ResponseBody> call = service.getProjectDetails();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    //Http == 200
                    if (response.isSuccessful()) {

                        //JSONObject responseBodyJSONObject = new JSONObject(response.body().string());
                        String responseBodyString = response.body().string();

                        //Get project details
                        ProjectDetails project = gson.fromJson(responseBodyString, ProjectDetails.class);

                        responseListener.onSuccess(project);

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

    //Get project comments
    public void getProjectComments(String projectId, final ResponseListener responseListener){

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
                responseListener.onFailure(Message.SOMETHING_WENT_WRONG);
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
                responseListener.onFailure(Message.SOMETHING_WENT_WRONG);
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
                responseListener.onFailure(Message.SOMETHING_WENT_WRONG);
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
                responseListener.onFailure(Message.SOMETHING_WENT_WRONG);
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
                responseListener.onFailure(Message.SOMETHING_WENT_WRONG);
            }
        });
    }
}
