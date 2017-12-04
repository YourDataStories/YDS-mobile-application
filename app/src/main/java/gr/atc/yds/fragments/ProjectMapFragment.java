package gr.atc.yds.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import gr.atc.yds.R;
import gr.atc.yds.controllers.App;
import gr.atc.yds.models.Project;
import gr.atc.yds.utils.Util;


public class ProjectMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "projectPoints";
    private List<LatLng> projectPoints;
    private GoogleMap map;

    public ProjectMapFragment() {
        // Required empty public constructor
    }

    public static ProjectMapFragment newInstance(String param1) {
        ProjectMapFragment fragment = new ProjectMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get arguments
        if (getArguments() != null) {

            String projectPointsString = getArguments().getString(ARG_PARAM1);

            if(projectPointsString != null)
                projectPoints = new Gson().fromJson(projectPointsString, new TypeToken<List<LatLng>>(){}.getType());
            else
                projectPoints = null;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_project_map, container, false);

        attachMap();

        return view;
    }

    private void attachMap(){

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragmentProjectMap_mapFragment, mapFragment);
        ft.commit();

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        initMap(googleMap);
        showProjectOnMap();
    }

    private void initMap(GoogleMap googleMap){

        if(projectPoints == null || projectPoints.size() <= 0)
            return;

        CameraUpdate center = CameraUpdateFactory.newLatLng(projectPoints.get(projectPoints.size()/2));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(getResources().getInteger(R.integer.PROJECT_MAP_ZOOM));

        map = googleMap;
        map.moveCamera(center);
        map.animateCamera(zoom);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(App.getContext(), R.raw.map_style));
    }

    //Draw project on map
    private void showProjectOnMap(){

        if(projectPoints == null || projectPoints.size() <= 0)
            return;

        //Point
        if(projectPoints.size() == 1){

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_project);
            LatLng position = projectPoints.get(0);
            map.addMarker(new MarkerOptions().position(position).icon(icon));

        }

        //LineString
        else{

            int color =  ContextCompat.getColor(App.getContext(), R.color.colorPrimary);
            PolylineOptions polylineOptions = new PolylineOptions().color(color)
                    .width(15)
                    .geodesic(false)
                    .addAll(projectPoints);
            map.addPolyline(polylineOptions);
        }
    }

}
