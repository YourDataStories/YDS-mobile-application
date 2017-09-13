package gr.atc.yds.clients;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.enums.Message;
import gr.atc.yds.models.Comment;
import gr.atc.yds.models.Project;
import gr.atc.yds.models.ProjectDetails;
import gr.atc.yds.utils.Log;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by ipapas on 08/12/16.
 */
public class YDSApiClient extends Client{

    private static YDSApiClient ydsApiClient = null;
    private static final String YDSBaseUrl = App.getContext().getString(R.string.YDS_BASE_URL);
    private WebService service;
    private Gson gson;

    //Representation of YDS remote web services
    public interface WebService {

        //Get projects
        @GET("getProjects")
        Call<ResponseBody> getProjects(@Query("pt1") String southWest, @Query("pt2") String northEast, @Query("start") int offset);

        //Get close project
        @GET("getCloseProject")
        Call<ResponseBody> getCloseProject(@Query("pt") String latLng);

        //Get project details
        @GET("getProjectDetails")
        Call<ResponseBody> getProjectDetails(@Query("projectId") Long projectId, @Query("username") String username);

        //Get project comments
        @GET("getProjectComments")
        Call<ResponseBody> getProjectComments(@Query("projectId") Long projectId, @Query("username") String username);

        //Rate project
        @POST("addProjectRate")
        Call<ResponseBody> rateProject(@Body RequestBody body);

        //Comment project
        @POST("addProjectComments")
        Call<ResponseBody> commentProject(@Body RequestBody body);

        //React to comment
        @POST("addCommentReaction")
        Call<ResponseBody> reactToComment(@Body RequestBody body);

    }

    //Request bodies
    public static class CommentProjectRequestBody{

        public Long project_id;
        public List<Comment> comments;

        public CommentProjectRequestBody(){}

        public CommentProjectRequestBody(Long project_id, Comment newComment){

            this.project_id = project_id;
            this.comments = new ArrayList<>();
            this.comments.add(newComment);
        }
    }
    public static class RateProjectRequestBody{

        public Long project_id;
        public Integer rate;
        public String username;

        public RateProjectRequestBody(){}

        public RateProjectRequestBody(Long project_id, Integer rate, String username){

            this.project_id = project_id;
            this.rate = rate;
            this.username = username;
        }
    }
    public static class ReactToCommentRequestBody{

        public Integer comment_id;
        public Comment.Reaction reaction;
        public String username;

        public ReactToCommentRequestBody(){}

        public ReactToCommentRequestBody(Integer comment_id, Comment.Reaction reaction, String username){

            this.comment_id = comment_id;
            this.reaction = reaction;
            this.username = username;
        }
    }

    private YDSApiClient(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YDSBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(WebService.class);

        gson = new Gson();
    }

    public static YDSApiClient getInstance(){
        if(ydsApiClient == null)
            ydsApiClient = new YDSApiClient();

        return ydsApiClient;
    }

    //Get projects
    public void getProjects(LatLng southWest, LatLng northEast, int offset, final ResponseListener responseListener){

        String pt1 = toString(southWest);
        String pt2 = toString(northEast);

        Log.i("YDS", "getProjects: " + pt1 + ", " + pt2);

        Call<ResponseBody> call = service.getProjects(pt1, pt2, offset);
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

    //Get close project
    public void getCloseProject(LatLng center, final ResponseListener responseListener){

        String pt = toString(center);

        Call<ResponseBody> call = service.getCloseProject(pt);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    //Http == 200
                    if (response.isSuccessful()) {

                        //TODO Get project's ID and name
                        JSONObject responseBodyJSONObject = new JSONObject(response.body().string());
                        String projectString = responseBodyJSONObject.getString("docs");
                        Project project = gson.fromJson(projectString, Project.class);

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
                handleFailure(t, responseListener);
            }
        });
    }

    //Get project details
    public void getProjectDetails(Long projectId, String username, final ResponseListener responseListener){

        Call<ResponseBody> call = service.getProjectDetails(projectId, username);
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
    public void getProjectComments(Long projectId, String username, final ResponseListener responseListener){

        Call<ResponseBody> call = service.getProjectComments(projectId, username);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    //Http == 200
                    if (response.isSuccessful()) {

                        JSONObject responseBodyJSONObject = new JSONObject(response.body().string());

                        //Get comments
                        String commentsString = responseBodyJSONObject.getString("comments");
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
    public void rateProject(Long projectId, Integer rate, String username, final ResponseListener responseListener){

        //Compose request body
        RateProjectRequestBody rateProjectRequestBody = new RateProjectRequestBody(projectId, rate, username);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(rateProjectRequestBody));

        Call<ResponseBody> call = service.rateProject(requestBody);
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
    public void commentProject(Long projectId, Comment comment, final ResponseListener responseListener){

        //Compose request body
        CommentProjectRequestBody commentProjectRequestBody = new CommentProjectRequestBody(projectId, comment);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(commentProjectRequestBody));

        Call<ResponseBody> call = service.commentProject(requestBody);
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

        //Compose request body
        ReactToCommentRequestBody reactToCommentRequestBody = new ReactToCommentRequestBody(commentId, Comment.Reaction.like, username);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(reactToCommentRequestBody));

        Call<ResponseBody> call = service.reactToComment(requestBody);
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

        //Compose request body
        ReactToCommentRequestBody reactToCommentRequestBody = new ReactToCommentRequestBody(commentId, Comment.Reaction.dislike, username);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(reactToCommentRequestBody));

        Call<ResponseBody> call = service.reactToComment(requestBody);
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

    /**
     * Converts LatLng point to string
     * @param point LatLng point
     * @return string format
     */
    private String toString(LatLng point){

        return String.format("%f %f", point.longitude, point.latitude);

    }
}
