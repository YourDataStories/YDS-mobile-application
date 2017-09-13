package gr.atc.yds.fragments;

import java.util.List;

import gr.atc.yds.models.Project;

/**
 * Created by ipapas on 13/09/17.
 */

public interface ProjectsFragment {

    void addProjects(List<Project> projects);
    void clearProjects();
}
