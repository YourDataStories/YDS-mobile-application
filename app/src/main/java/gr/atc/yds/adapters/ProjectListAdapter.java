package gr.atc.yds.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.models.Project;
import gr.atc.yds.utils.Util;

/**
 * Created by ipapas on 09/12/16.
 */

public class ProjectListAdapter extends ArrayAdapter<Project> {

    private Context context;
    private String currency;

    public ProjectListAdapter(Context context, List<Project> projects) {
        super(context, R.layout.item_project, projects);

        this.context = context;

        //Get currency
        currency = App.getContext().getString(R.string.CURRENCY);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            //Create view
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.item_project, parent, false);
        }

        //Get current project
        Project project = getItem(position);

        if(project != null){

            //Title
            if(project.title_en != null){
                TextView titleTextView = (TextView) convertView.findViewById(R.id.listItemProject_title);
                titleTextView.setText(project.title_en);
            }

            //Budget
            if(project.hasBudgetAggregate_aggregatedAmount != null){
                TextView budgetTextView = (TextView) convertView.findViewById(R.id.listItemProject_budget);
                String budget = Util.convertToString(project.hasBudgetAggregate_aggregatedAmount);
                budgetTextView.setText(String.format("%s %s",budget, currency));
            }

            //Num of comments
            if(project.num_comments != null){
                TextView numOfCommentsTextView = (TextView) convertView.findViewById(R.id.listItemProject_numOfComments);
                numOfCommentsTextView.setText(Integer.toString(project.num_comments));
            }

            //Num of ratings
            if(project.num_ratings != null){
                TextView numOfRatingsTextView = (TextView) convertView.findViewById(R.id.listItemProject_numOfRatings);
                numOfRatingsTextView.setText(Integer.toString(project.num_ratings));
            }

            //Rating
            if(project.average_rating != null){
                RatingBar averageRatingBar = (RatingBar) convertView.findViewById(R.id.listItemProject_ratingBar);
                averageRatingBar.setRating(project.average_rating);

                TextView averageRatingTextView = (TextView) convertView.findViewById(R.id.listItemProject_rating);
                averageRatingTextView.setText(Float.toString(project.average_rating));
            }

        }

        return convertView;

    }

}
