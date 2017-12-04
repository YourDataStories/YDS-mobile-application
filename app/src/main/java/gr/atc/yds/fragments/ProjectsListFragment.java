package gr.atc.yds.fragments;

import android.content.Context;
import android.os.Bundle;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

public class ProjectsListFragment extends Fragment implements ProjectsFragment {

    public interface Listener {
        void onProjectItemClicked(Long projectID);
        void onProjectListScrolledToBottom();
    }

    private Listener listener;
    private static final String ARG_PARAM1 = "projects";
    private List<Project> projects;
    private ProjectListAdapter projectListAdapter;
    private ListView projectsListView;
    private View view;
    private boolean scrolledToBottom;

    public ProjectsListFragment() {

        view = null;
        projects = null;
        scrolledToBottom = false;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        projectsListView = (ListView) view.findViewById(R.id.fragmentProjectsList_projectsListView);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Set project list adapter
        setListAdapter();

        //Set project list listeners
        setListListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Sets event listeners to projects' ListView
     */
    private void setListListeners(){

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

        //List view scrolled
        projectsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (projectsListView.getAdapter() == null || projectsListView.getAdapter().getCount() == 0)
                    return ;

                int l = visibleItemCount + firstVisibleItem;
                if (l >= totalItemCount && !scrolledToBottom) {

                    //List view scrolled to bottom
                    scrolledToBottom = true;
                    listener.onProjectListScrolledToBottom();
                }
            }
        });
    }

    /**
     * Sets list adapter
     */
    private void setListAdapter(){

        if(projects == null)
            return;

        projectListAdapter = new ProjectListAdapter(App.getContext(), projects);
        projectsListView.setAdapter(projectListAdapter);
    }

    /**
     * Adds projects to ListView
     * @param newProjects list of new projects
     */
    @Override
    public void addProjects(List<Project> newProjects){

        if(projects == null || projectListAdapter == null)
            return;

        projects.addAll(newProjects);
        projectListAdapter.notifyDataSetChanged();
        scrolledToBottom = false;
    }

    /**
     * Removes all projects from ListView
     */
    @Override
    public void clearProjects(){

        if(projects == null || projectListAdapter == null)
            return;

        projects.clear();
        projectListAdapter.notifyDataSetChanged();
    }

    /**
     * Updates an already shown project
     * @param updatedProject updated project
     */
    public void updateProject(Project updatedProject){

        if(projects == null || projectListAdapter == null)
            return;

        //Find project (tha should be updated)
        int position = 0;
        for(Project project : projects) {

            if (project.projectId.equals(updatedProject.projectId))
                break;

            position++;
        }

        //Project (tha should be updated) found
        if(position < projects.size()){

            //Replace project with the updated version
            projects.remove(position);
            projects.add(position, updatedProject);
            projectListAdapter.notifyDataSetChanged();

        }

    }
}
