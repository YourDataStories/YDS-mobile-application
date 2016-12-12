package gr.atc.yds.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.models.Project;
import gr.atc.yds.utils.Util;


public class ProjectsMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public interface Listener {
        void onProjectMarkerClicked(String projectID);
    }

    private Listener listener;
    private static final String ARG_PARAM1 = "projects";
    private List<Project> projects;
    private Map<Marker,Project> projectMarkers;
    private View view;
    private GoogleMap map;

    public static ProjectsMapFragment newInstance(String param1) {

        ProjectsMapFragment fragment = new ProjectsMapFragment();

        //Set arguments
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);

        return fragment;
    }

    public ProjectsMapFragment() {

        projects = null;
        view = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Util.log("onAttach ProjectsMapFragment");

        //Set listener
        if (context instanceof Listener) {
            listener = (Listener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Util.log("onCreateView ProjectsMapFragment");

        //Init view
        view = inflater.inflate(R.layout.fragment_projects_map, container, false);

        projectMarkers = new HashMap<>();
        attachMap();

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Util.log("onMapReady");

        initMap(googleMap);
        showProjectsOnMap();

        //Show the current location icon
        if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            map.setMyLocationEnabled(true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //Get clicked project
        Project clickedProject = projectMarkers.get(marker);

        if(listener != null)
            listener.onProjectMarkerClicked(clickedProject.id);

        return false;
    }

    private void attachMap(){

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragmentProjectsMap_mapFragment, mapFragment);
        ft.commit();

        mapFragment.getMapAsync(this);
    }

    private void initMap(GoogleMap googleMap){

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(37.983810, 23.727539));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(6);

        map = googleMap;
        map.moveCamera(center);
        map.animateCamera(zoom);
        map.setOnMarkerClickListener(this);

    }

    private void showProjectsOnMap(){

        Util.log("showProjectsOnMap");

        if(projects != null && map != null)
            for(Project project : projects)
                showProjectOnMap(project);


    }

    //Draw project on map
    private void showProjectOnMap(Project project){

        Util.log("showProjectOnMap");

        List<LatLng> points = project.getPoints();

        if(points.size() <= 0)
            return;

        int color =  ContextCompat.getColor(App.getContext(), R.color.colorPrimary);
        PolylineOptions polylineOptions = new PolylineOptions().color(color)
                .width(25)
                .geodesic(false)
                .addAll(points);

        //Draw polyline
        map.addPolyline(polylineOptions);

        //Add a marker in the middle of it
        Marker marker = addProjectMarkerOnMap(points);
        projectMarkers.put(marker, project);

    }

    //Add marker in the middle of each project's polyline
    private Marker addProjectMarkerOnMap(List<LatLng> points){

        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        LatLng position = points.get((Integer) (points.size() / 2));

        Marker marker = map.addMarker(new MarkerOptions().position(position).icon(icon));
        return marker;

    }

}
