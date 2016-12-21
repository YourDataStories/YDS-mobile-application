package gr.atc.yds.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.models.Comment;
import gr.atc.yds.utils.Util;

/**
 * Created by ipapas on 14/12/16.
 */

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private final static String ALREADY_REACTED_MESSAGE = App.getContext().getResources().getString(R.string.alreadyReacted);

    private OnCommentReactionListener onCommentReactionListener;

    public CommentListAdapter(Context context, List<Comment> comments) {

        super(context, R.layout.item_comment, comments);

        if(context instanceof OnCommentReactionListener)
            this.onCommentReactionListener = (OnCommentReactionListener) context;
        else
            this.onCommentReactionListener = null;
    }

    //Refresh list of comments
    public void refresh(List<Comment> comments){

        clear();
        addAll(comments);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            //Create view
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.item_comment, parent, false);
        }

        //Get displayed comment
        Comment comment = getItem(position);

        Util.log(comment.text + " " + comment.reaction);

        if(comment != null){

            //Comment type
            if(comment.type != null){

                Drawable drawable;

                if(comment.type == Comment.Type.Twitter)
                    drawable = ContextCompat.getDrawable(App.getContext(), R.drawable.img_twitter);
                else if(comment.type == Comment.Type.RSS)
                    drawable = ContextCompat.getDrawable(App.getContext(), R.drawable.img_rss);
                else
                    drawable = ContextCompat.getDrawable(App.getContext(), R.drawable.img_yds);

                ImageView commentType = (ImageView) convertView.findViewById(R.id.commentType);
                commentType.setImageDrawable(drawable);

            }

            //Commenter name
            if(comment.created_by != null){
                TextView commenterNameTextView = (TextView) convertView.findViewById(R.id.commenterName);
                commenterNameTextView.setText(comment.created_by);
            }

            //Comment
            if(comment.text != null){
                TextView commentTextView = (TextView) convertView.findViewById(R.id.comment);
                commentTextView.setText(comment.text);
            }

            //Time-ago
            String timeAgo = comment.getTimeago();
            if(timeAgo != null){
                TextView timeAgoTextView = (TextView) convertView.findViewById(R.id.timeago);
                timeAgoTextView.setText(timeAgo);
            }

            //Thumbs up
            TextView thumbsUpNumber = (TextView) convertView.findViewById(R.id.thumbsUpNumber);
            ImageView thumbsUpBtn = (ImageView) convertView.findViewById(R.id.thumbsUpBtn);
            if(comment.reaction == Comment.Reaction.like)
                markThumbsUpAsClicked(thumbsUpBtn, thumbsUpNumber);
            else
                markThumbsUpAsUnclicked(thumbsUpBtn, thumbsUpNumber);
            if(comment.likes != null)
                thumbsUpNumber.setText(Integer.toString(comment.likes));

            //Thumbs down
            TextView thumbsDownNumber = (TextView) convertView.findViewById(R.id.thumbsDownNumber);
            ImageView thumbsDownBtn = (ImageView) convertView.findViewById(R.id.thumbsDownBtn);
            if(comment.reaction == Comment.Reaction.dislike)
                markThumbsDownAsClicked(thumbsDownBtn, thumbsDownNumber);
            else
                markThumbsDownAsUnclicked(thumbsDownBtn, thumbsDownNumber);
            if(comment.dislikes != null)
                thumbsDownNumber.setText(Integer.toString(comment.dislikes));

            //Set UI event listeners
            setEventListeners(position, convertView, parent);

        }

        return convertView;
    }

    //Set UI event listeners (for the specific position)
    private void setEventListeners(final int position, final View convertView, final ViewGroup parent){

        //Get displayed comment
        final Comment comment = getItem(position);

        if(comment == null)
            return;

        //Thumbs-up btn clicked
        final ImageView thumbsUpBtn = (ImageView) convertView.findViewById(R.id.thumbsUpBtn);
        thumbsUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //User has already reacted to this comment
                if(comment.reaction != null){

                    Util.showToast(ALREADY_REACTED_MESSAGE);
                    return;
                }

                //User reacts for first time
                comment.like();
                getView(position, convertView, parent);

                if(onCommentReactionListener != null)
                    onCommentReactionListener.onCommentLike(comment);

            }
        });

        //Thumbs-down btn clicked
        final ImageView thumbsDownBtn = (ImageView) convertView.findViewById(R.id.thumbsDownBtn);
        thumbsDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //User has already reacted to this comment
                if(comment.reaction != null){

                    Util.showToast(ALREADY_REACTED_MESSAGE);
                    return;
                }

                //User reacts for first time
                comment.dislike();
                getView(position, convertView, parent);

                if(onCommentReactionListener != null)
                    onCommentReactionListener.onCommentDislike(comment);

            }
        });
    }

    //Mark thumbs-up btn as clicked
    private void markThumbsUpAsClicked(ImageView thumbsUpBtn, TextView thumbsUpNumber){
        thumbsUpBtn.setImageDrawable(ContextCompat.getDrawable(App.getContext(), R.drawable.ic_thumbs_up_clicked));
        thumbsUpNumber.setTextColor(ContextCompat.getColor(App.getContext(),R.color.colorPrimary));
    }

    //Mark thumbs-up btn as unclicked
    private void markThumbsUpAsUnclicked(ImageView thumbsUpBtn, TextView thumbsUpNumber){
        thumbsUpBtn.setImageDrawable(ContextCompat.getDrawable(App.getContext(), R.drawable.ic_thumbs_up));
        thumbsUpNumber.setTextColor(ContextCompat.getColor(App.getContext(),R.color.textLightColor));
    }

    //Mark thumbs-down btn as clicked
    private void markThumbsDownAsClicked(ImageView thumbsDownBtn, TextView thumbsDownNumber){
        thumbsDownBtn.setImageDrawable(ContextCompat.getDrawable(App.getContext(), R.drawable.ic_thumbs_down_clicked));
        thumbsDownNumber.setTextColor(ContextCompat.getColor(App.getContext(),R.color.colorPrimary));
    }

    //Mark thumbs-down btn as unclicked
    private void markThumbsDownAsUnclicked(ImageView thumbsDownBtn, TextView thumbsDownNumber){
        thumbsDownBtn.setImageDrawable(ContextCompat.getDrawable(App.getContext(), R.drawable.ic_thumbs_down));
        thumbsDownNumber.setTextColor(ContextCompat.getColor(App.getContext(),R.color.textLightColor));
    }

    public interface OnCommentReactionListener {
        public void onCommentLike(Comment comment);
        public void onCommentDislike(Comment comment);
    }


}
