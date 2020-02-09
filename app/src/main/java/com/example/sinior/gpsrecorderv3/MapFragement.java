package com.example.sinior.gpsrecorderv3;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sinior.gpsrecorderv3.BDD.PointsBDD;
import com.example.sinior.gpsrecorderv3.Beans.Point;
import com.example.sinior.gpsrecorderv3.Tools.GpsTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;


public class MapFragement extends Fragment implements LocationListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private GpsTracker gpsTracker;
    public static Point point;
    public static Point currentLocation;
    public static boolean multiView;
    private ArrayList<Point> listPoints = new ArrayList<Point>();
    LocationManager locationManager;
    Marker currentLocationMarker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_map_fragement, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        }
        fragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Setting a click event handler for the map
        this.listPoints.add(point);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.point_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.drawPolyline) {

            if (getArguments() != null && getArguments().getBoolean("multiRoutes")) {
                final PointsBDD pointsBDD = new PointsBDD(getActivity());
                pointsBDD.open();
                this.drawPolyline(pointsBDD.getAllPoint());
                pointsBDD.close();
            } else if (point != null) {
                this.drawPolyline(listPoints);
            }
        } else if (itemId == R.id.optAddPoint) {
            this.addPoint();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.drawPolyline);
        if ((item != null && getArguments() != null && !getArguments().getBoolean("multiRoutes")) || point == null) {
            item.setVisible(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        /*
        LatLng marker = new LatLng(-33.867, 151.206);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 13));

        googleMap.addMarker(new MarkerOptions().title("Hello Google Maps!").position(marker));
*/
        if (mMap != null) {
            mMap.getUiSettings().setMapToolbarEnabled(true);
        }
        mMap.setMyLocationEnabled(true);
        if (getArguments() != null && getArguments().getBoolean("multiRoutes") == true) {
            final PointsBDD pointsBDD = new PointsBDD(getActivity());
            pointsBDD.open();
            this.showLocationsOnMap(pointsBDD.getAllPoint());
            pointsBDD.close();
        } else if (point != null) {
            this.addLocationsOnMap(point, false);
            //this.drawPolyline(listPoints);
        }
        this.getLocation();
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
        onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
/*
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
                */
            }
        });
    }

    public void getLocation() {
        if (getActivity().getApplicationContext() != null) {
            gpsTracker = new GpsTracker(getActivity().getApplicationContext());
            if (gpsTracker.canGetLocation()) {
                Point p = new Point();
                p.setAtitude(String.valueOf(gpsTracker.getLatitude()));
                p.setLongtude(String.valueOf(gpsTracker.getLongitude()));
                this.addLocationsOnMap(p, true);
            } else {
                gpsTracker.showSettingsAlert(null);
            }
        }
    }

    private void showLocationsOnMap(ArrayList<Point> listPoints) {
        mMap.clear();
        float latitude;
        float longitude;
        Marker marker;
        ArrayList<Marker> markers = new ArrayList<Marker>();

        //Add markers for all locations
        for (Point point : listPoints) {

            latitude = Float.parseFloat(point.getAtitude());
            longitude = Float.parseFloat(point.getLongtude());
            LatLng latlang = new LatLng(latitude, longitude);

            markers.add(mMap.addMarker(new MarkerOptions()
                    .position(latlang)
                    .title(point.getCreateDate())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.anchor))));
        }
    }

    private void addLocationsOnMap(Point point, boolean currentLocation) {

        float latitude;
        float longitude;
        Marker marker;

        //Add markers for all locations


        latitude = Float.parseFloat(point.getAtitude());
        longitude = Float.parseFloat(point.getLongtude());
        LatLng latlang = new LatLng(latitude, longitude + 0.05000);

        if (currentLocation) {
            this.currentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latlang)
                    .title("موقعي")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        } else {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latlang)
                    .title(point.getCreateDate())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.target)));
        }


        this.moveToCurrentLocation(latlang);

    }

    private void moveToCurrentLocation(LatLng currentLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }

    private void drawPolyline(ArrayList<Point> listPoints) {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < listPoints.size(); z++) {
            LatLng point = new LatLng(Float.parseFloat(listPoints.get(z).getAtitude()), Float.parseFloat(listPoints.get(z).getLongtude()));
            options.add(point);
        }

        // add current location
        GpsTracker tracker = new GpsTracker(getActivity());
        if (!tracker.canGetLocation()) {
            tracker.showSettingsAlert(null);
        } else {
            if (currentLocation == null) {
                currentLocation = new Point();
            }
            currentLocation.setAtitude(String.valueOf(tracker.getLatitude()));
            currentLocation.setLongtude(String.valueOf(tracker.getLongitude()));
            LatLng point = new LatLng(tracker.getLatitude(), tracker.getLongitude());
            options.add(point);
        }

        Polyline line = mMap.addPolyline(options);
        this.currentLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.arrow));
        line.setEndCap(
                new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow),
                        16));
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getActivity(), "tbadlt", Toast.LENGTH_SHORT).show();

        System.out.println(currentLocation);
        if (currentLocation == null) {
            currentLocation = new Point();
        }
        currentLocation.setAtitude(String.valueOf(location.getLatitude()));
        currentLocation.setLongtude(String.valueOf(location.getLongitude()));
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(currentLocationMarker == null){
            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("موقعي")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }else{
            currentLocationMarker.setPosition(latLng);
        }

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

    public void addPoint() {
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "جاري العثور على مكانك", "المرجوا الإنتظار...", true);
        try {
            gpsTracker = new GpsTracker(getActivity());
            if (gpsTracker.canGetLocation()) {
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                Point p = new Point();
                p.setLongtude(String.valueOf(longitude));
                p.setAtitude(String.valueOf(latitude));
                DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
                String date = df.format(Calendar.getInstance().getTime());
                final PointsBDD pointsBDD = new PointsBDD(getActivity());
                pointsBDD.open();
                p.setCreateDate(date);
                p.setStat("true");
                if (pointsBDD.insertPoint(p) > 0) {
                    System.out.println("Ajouter bien fait");
                    Toast.makeText(getActivity(), "تم حفظ النقطة", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                pointsBDD.close();
            } else {
                gpsTracker.showSettingsAlert(dialog);
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }


}
