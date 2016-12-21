package gr.atc.yds.fragments;

import android.content.Context;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.adapters.ProjectListAdapter;
import gr.atc.yds.controllers.App;
import gr.atc.yds.models.Project;
import gr.atc.yds.utils.Util;

public class ProjectsListFragment extends Fragment {

    public interface Listener {
        void onProjectItemClicked(Long projectID);
    }

    private Listener listener;
    private static final String ARG_PARAM1 = "projects";
    private List<Project> projects;
    private View view;

    public ProjectsListFragment() {

        view = null;
        projects = null;
    }

    public static ProjectsListFragment newInstance(String param1) {

        ProjectsListFragment fragment = new ProjectsListFragment();

        //Set arguments
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Listener) {
            listener = (Listener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get arguments
        if (getArguments() != null) {

            String projectsString = getArguments().getString(ARG_PARAM1);

            if(projectsString != null)
                projects = new Gson().fromJson(projectsString, new TypeToken<List<Project>>(){}.getType());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.from(contextThemeWrapper);

        view = localInflater.inflate(R.layout.fragment_projects_list, container, false);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        //Show project list
        showProjectsOnList();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void showProjectsOnList(){

        if(projects != null && view != null){

            ListView projectsListView = (ListView) view.findViewById(R.id.fragmentProjectsList_projectsListView);
            ProjectListAdapter projectListAdapter = new ProjectListAdapter(App.getContext(), projects);
            projectsListView.setAdapter(projectListAdapter);

            //Project clicked
            projectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if(listener != null){
                        Project clickedProject = projects.get(i);
                        listener.onProjectItemClicked(clickedProject.projectId);
                    }

                }
            });
        }
    }
}
