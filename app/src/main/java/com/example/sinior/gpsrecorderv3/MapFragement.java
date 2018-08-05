package com.example.sinior.gpsrecorderv3;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sinior.gpsrecorderv3.Beans.Point;
import com.example.sinior.gpsrecorderv3.Tools.GpsTracker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapFragement  extends Fragment implements LocationListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private GpsTracker gpsTracker;
    public static Point point;
    public static Point currentLocation;
    private ArrayList<Point> listPoints = new ArrayList<Point>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_fragement, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        }
        fragment.getMapAsync(this);
        // Setting a click event handler for the map
        this.listPoints.add(point);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng marker = new LatLng(-33.867, 151.206);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 13));

        googleMap.addMarker(new MarkerOptions().title("Hello Google Maps!").position(marker));

        if(mMap != null){
            mMap.getUiSettings().setMapToolbarEnabled(true);
        }
        if(point != null){
            this.addLocationsOnMap(point);
            this.drawPolyline(listPoints);
        }
        this.getLocation();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Clears the previously touched position
                mMap.clear();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);
            }
        });
    }

    public void getLocation() {
        gpsTracker = new GpsTracker(MainActivity.getAppContext());
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            System.out.println(latitude + " "+ longitude);
        } else {
            gpsTracker.showSettingsAlert();
        }
    }
    private void showLocationsOnMap(ArrayList<Point> listPoints){

        float latitude;
        float longitude;
        Marker marker;

        //Add markers for all locations
        for (Point point : listPoints) {

            latitude = Float.parseFloat(point.getAtitude());
            longitude = Float.parseFloat(point.getLongtude());
            LatLng latlang = new LatLng(latitude, longitude);

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latlang)
                    .title(point.getCreateDate()));
        }
    }
    private void addLocationsOnMap(Point point){

        float latitude;
        float longitude;
        Marker marker;

        //Add markers for all locations


            latitude = Float.parseFloat(point.getAtitude());
            longitude = Float.parseFloat(point.getLongtude());
            LatLng latlang = new LatLng(latitude, longitude);

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latlang)
                    .title(point.getCreateDate()));

            this.moveToCurrentLocation(latlang);

    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }

    private void drawPolyline(ArrayList<Point> listPoints){
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < listPoints.size(); z++) {
            LatLng point = new LatLng(Float.parseFloat(listPoints.get(z).getAtitude()), Float.parseFloat(listPoints.get(z).getLongtude()));
            options.add(point);
        }
        LatLng point = new LatLng(30.395657, -9.605479);
        options.add(point);
        Polyline line = mMap.addPolyline(options);
    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }


    @Override
    public void onLocationChanged(Location location) {

        currentLocation.setAtitude(String.valueOf(location.getLatitude()));
        currentLocation.setLongtude(String.valueOf(location.getLongitude()));
        try {
            Geocoder geocoder = new Geocoder(MainActivity.getAppContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            /*tvLatitude.setText(tvLatitude.getText() + "\n" + addresses.get(0).getAddressLine(0) + ", " +
                    addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2));*/
            currentLocation.setAtitude(String.valueOf(location.getLatitude()));
            currentLocation.setLongtude(String.valueOf(location.getLongitude()));
        } catch (Exception e) {

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
