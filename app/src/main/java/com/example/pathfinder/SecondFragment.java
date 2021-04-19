package com.example.pathfinder;

import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SecondFragment extends Fragment implements OnMapReadyCallback  {

    private GoogleMap gMap;
    private MapView mapView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<AutocompletePrediction> predictionList;
    private View gMapView;
    public String city;
    private Location mLastKnownLocation,loc1,loc2;
    private LocationCallback locationCallback;
    public SearchView searchView;
    private SupportMapFragment gmapFragment;
    private Polyline polyline = null;
    private List<LatLng> latLngList = new ArrayList<>();
    private final float DEFAULT_ZOOM = 15;
    private Button start_btn;
    private   float distanceInKm;
    LinearLayout direction_layout,distance_layout,distance_and_time;
    CardView walking,cycle,bike,car;
    TextView distance,time,walk_time,cycle_time,bike_time,car_time,desc_text,below_desc;
    private int time_walk,time_cycle,time_bike,time_car;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
       View myView =  inflater.inflate(R.layout.fragment_second, container, false);
        direction_layout = myView. findViewById(R.id.get_direction);
        distance_layout = myView.findViewById(R.id.distance_layout);
        walking= myView.findViewById(R.id.walking);
        cycle= myView.findViewById(R.id.cycle);
        bike= myView.findViewById(R.id.bike);
        car= myView.findViewById(R.id.car);
        walk_time= myView.findViewById(R.id.walking_time);
        cycle_time= myView.findViewById(R.id.cycle_time);
        bike_time= myView.findViewById(R.id.bike_time);
        car_time= myView.findViewById(R.id.car_time);
        distance= myView.findViewById(R.id.distance);
        time= myView.findViewById(R.id.time);
        start_btn = myView.findViewById(R.id.start_journey_btn);
        distance_and_time = myView.findViewById(R.id.distance_and_time);
        desc_text = myView.findViewById(R.id.desc_text);
        below_desc = myView.findViewById(R.id.below_desc);
        searchView = ((MapActivity2)getActivity()).searchView;
        city = ((MapActivity2)getActivity()).city;

       return myView;
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set the direction and distance  layouts invisible
        direction_layout.setVisibility(View.INVISIBLE);
        distance_layout.setVisibility(View.INVISIBLE);
        cycle_time.setVisibility(View.INVISIBLE);
        walk_time.setVisibility(View.INVISIBLE);
        car_time.setVisibility(View.INVISIBLE);
        below_desc.setVisibility(View.INVISIBLE);

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);

        if(gMapView != null && gMapView.findViewById(Integer.parseInt("1"))!=null)
        {
            View locationButtton = ((View) gMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButtton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
            layoutParams.setMargins(0,0,40,170);
        }

        // check if the gps location of the user's device is on or not and then request to enable it

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder lBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(lBuilder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getMyLocation();
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if( e instanceof ResolvableApiException){
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(getActivity(),18);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String loc = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(loc!= null && !loc.equals("")){
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        addressList = geocoder.getFromLocationName(loc,1);
                        while (addressList.size()==0) {
                            addressList = geocoder.getFromLocationName(loc, 1);
                        }
                        if (addressList.size()>0) {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());

                            //set loc1 variable to destination location
                            loc1 = new Location("");
                            loc1.setLatitude(address.getLatitude());
                            loc1.setLongitude(address.getLongitude());

                            latLngList.add(latLng);
                            get_place(latLng,loc);
                            direction_layout.setVisibility(View.VISIBLE);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                city=s;
                Log.d("king",s);
             if(s.length()==0){
                 direction_layout.setVisibility(View.INVISIBLE);
                 distance_layout.setVisibility(View.INVISIBLE);
             }
                ((MapActivity2)getActivity()).add_suggestion_fragment();


                return false;
            }
        });

        // set the visibility of distance_layout visible and measure the time
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawPolyLineOnMap(latLngList,loc1,loc2);
                String dest = String.format("%.01f",distanceInKm);
                desc_text.setVisibility(View.INVISIBLE);
                //show the time taken by bike byDefault
                time_bike = Math.round(distanceInKm);
                String hrs  = String.valueOf(time_bike/50);
                String min = String.valueOf(time_bike%50);

                if(time_bike/50>0){
                    bike_time.setText(hrs+" hrs "+min+" mins");
                    time.setText("( "+hrs+" hrs "+min+" mins"+" )");
                }
                else{
                    bike_time.setText(min+" mins");
                    time.setText("( "+min+" mins"+" )");
                }
                distance_and_time.setVisibility(View.VISIBLE);
                distance.setText(dest+" km");
                distance_layout.setVisibility(View.VISIBLE);
                start_btn.setVisibility(View.INVISIBLE);
                below_desc.setVisibility(View.VISIBLE);
            }
        });

        //Calculate time taken by various vehicles
        walking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_walk = Math.round(distanceInKm);
                String hrs  = String.valueOf(time_walk/8);
                String min = String.valueOf(time_walk%8);
                walk_time.setVisibility(View.VISIBLE);
                if(time_walk/8>0){
                    walk_time.setText(hrs+" hrs "+min+" mins");
                    time.setText("( "+hrs+" hrs "+min+" mins"+" )");
                }
                else{
                    car_time.setText(min+" mins");
                    time.setText("( "+min+" mins"+" )");
                }

                car_time.setVisibility(View.INVISIBLE);
                cycle_time.setVisibility(View.INVISIBLE);
                bike_time.setVisibility(View.INVISIBLE);
            }
        });
        cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_cycle = Math.round(distanceInKm);
                String hrs  = String.valueOf(time_cycle/20);
                String min = String.valueOf(time_cycle%20);
                cycle_time.setVisibility(View.VISIBLE);
                if(time_cycle/20>0){
                    cycle_time.setText(hrs+" hrs "+min+" mins");
                    time.setText("( "+hrs+" hrs "+min+" mins"+" )");
                }
                else{
                    car_time.setText(min+" mins");
                    time.setText("( "+min+" mins"+" )");
                }

                walk_time.setVisibility(View.INVISIBLE);
                car_time.setVisibility(View.INVISIBLE);
                bike_time.setVisibility(View.INVISIBLE);
            }
        });
        bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_bike= Math.round(distanceInKm);
                String hrs  = String.valueOf(time_bike/50);
                String min = String.valueOf(time_bike%50);
                bike_time.setVisibility(View.VISIBLE);
                if(time_bike/50>0){
                    bike_time.setText(hrs+" hrs "+min+" mins");
                    time.setText("( "+hrs+" hrs "+min+" mins"+" )");
                }
                else{
                    bike_time.setText(min+" mins");
                    time.setText("( "+min+" mins"+" )");
                }


                walk_time.setVisibility(View.INVISIBLE);
                cycle_time.setVisibility(View.INVISIBLE);
                car_time.setVisibility(View.INVISIBLE);
            }
        });
        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_car = Math.round(distanceInKm);
                String hrs  = String.valueOf(time_car/60);
                String min = String.valueOf(time_car%60);
                car_time.setVisibility(View.VISIBLE);
                if(time_car/60>0){
                    car_time.setText(hrs+" hrs "+min+" mins");
                    time.setText("( "+hrs+" hrs "+min+" mins"+" )");
                }
                else{
                    car_time.setText(min+" mins");
                    time.setText("( "+min+" mins"+" )");
                }

                walk_time.setVisibility(View.INVISIBLE);
                cycle_time.setVisibility(View.INVISIBLE);
                bike_time.setVisibility(View.INVISIBLE);
            }
        });

    }


    @SuppressLint("MissingPermission")
    private void getMyLocation(){
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()){
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation !=null){
                                LatLng latLng1 = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                                //set loc2 variable to d source location
                                loc2 = new Location("");
                                loc2.setLatitude(mLastKnownLocation.getLatitude());
                                loc2.setLongitude(mLastKnownLocation.getLongitude());

                                latLngList.add(latLng1);
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,DEFAULT_ZOOM));
                            }else{
                                LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                                locationCallback = new LocationCallback(){
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if(locationResult==null){
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        LatLng lattlng2 = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                                        //set loc2 variable to d source location
                                        loc2 = new Location("");
                                        loc2.setLatitude(mLastKnownLocation.getLatitude());
                                        loc2.setLongitude(mLastKnownLocation.getLongitude());

                                        latLngList.add(lattlng2);
                                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lattlng2,DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };

                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);

                            }
                        }else{
                            Toast.makeText(getActivity(), "unable to get last location....", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void get_place(LatLng latLng,String loc){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(loc);
        gMap.clear();
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        gMap.addMarker(markerOptions);
        //gMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLatitude())).title(loc));
        // gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));


    }
    public void drawPolyLineOnMap(List<LatLng> list,Location l1,Location l2) {
        //  Draw polyline on map
        if(polyline != null ) polyline.remove();

        //Create polyline on Map;
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(list)
                .width(15).color(Color.RED)
                .geodesic(true);

        polyline = gMap.addPolyline(polylineOptions);
        float  distanceInMeter = l1.distanceTo(l2);
        distanceInKm = distanceInMeter/1000;
        latLngList.clear();
        getMyLocation();


    }
}