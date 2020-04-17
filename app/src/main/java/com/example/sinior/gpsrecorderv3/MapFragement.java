package com.example.sinior.gpsrecorderv3;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.sinior.gpsrecorderv3.BDD.PointsBDD;
import com.example.sinior.gpsrecorderv3.Beans.Point;
import com.example.sinior.gpsrecorderv3.Tools.GpsTracker;
import com.example.sinior.gpsrecorderv3.Tools.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;


public class MapFragement extends Fragment implements LocationListener, SensorEventListener, OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {
    private GoogleMap mMap;
    private GpsTracker gpsTracker;
    public static Point point, targetPoint;
    public static Point currentLocation;
    public static boolean multiView;
    public static boolean onlyCurrentLocation;
    private ArrayList<Point> listPoints = new ArrayList<Point>();
    LocationManager locationManager;
    Marker currentLocationMarker;
    private boolean isMarkerRotating;
    private boolean enableAsynDraw, lineDrawed;
    private LatLng oldLocation, newLocaation;
    private Polyline currentLine;
    private Utils utils;
    public ArrayList<Point> pointsList;
    public ArrayList<Marker> markers;

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
        utils = new Utils(view.getContext());
        MapFragment fragment = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        }
        fragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Setting a click event handler for the map
        if (point != null) {
            this.listPoints.add(point);
        }
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
                listPoints = pointsBDD.getAllPoint();
                this.drawPolyline(listPoints);
                pointsBDD.close();
            } else if (point != null) {
                this.drawPolyline(listPoints);
            }
        } else if (itemId == R.id.optAddPoint) {
            this.addPoint();
        } else if (itemId == R.id.optGoTo) {

            this.moveToCurrentLocation(currentLocationMarker.getPosition());

        } else if (itemId == R.id.optSynOrNot) {
            if (!enableAsynDraw) {
                item.setIcon(getResources().getDrawable(R.drawable.attach_green));
                enableAsynDraw = true;
            } else {
                item.setIcon(getResources().getDrawable(R.drawable.attach_red));
                enableAsynDraw = false;
            }


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
        //mMap.setMyLocationEnabled(true);
        if (mMap != null) {
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        if (getArguments() != null && getArguments().getBoolean("multiRoutes")) {
            final PointsBDD pointsBDD = new PointsBDD(getActivity());
            pointsBDD.open();
            listPoints = pointsBDD.getAllPoint();
            this.showLocationsOnMap(listPoints);
            pointsBDD.close();
        } else if (point != null) {
            this.addLocationsOnMap(point, false);
            //this.drawPolyline(listPoints);
        }
        this.getLocation();
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 5, this);
        onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
            }
        });
        mMap.setOnInfoWindowClickListener(this);
    }

    public void getLocation() {
        if (getActivity().getApplicationContext() != null) {
            gpsTracker = new GpsTracker(getActivity());
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
        markers = new ArrayList<Marker>();

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
        LatLng latlang = new LatLng(latitude, longitude);

        if (currentLocation) {
            this.currentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latlang)
                    .title("موقعي")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        } else {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latlang)
                    .title(point.getCreateDate())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.anchor)));
            markers = new ArrayList<>();
            markers.add(marker);
        }


        this.moveToCurrentLocation(latlang);

    }

    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

    }

    public void onSensorChanged(SensorEvent event) {

        float degree = Math.round(event.values[0]);

        Log.d(null, "Degree ---------- " + degree);

        updateCamera(degree);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void moveToCurrentLocation(LatLng currentLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }

    private void drawPolyline(ArrayList<Point> listPoints) {
        PolylineOptions options = new PolylineOptions().width(8).color(Color.RED);
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
            LatLng latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());

            currentLocationMarker.setPosition(latLng);
            LatLng point = new LatLng(tracker.getLatitude(), tracker.getLongitude());
            options.add(point);
        }
        for (int z = 0; z < listPoints.size(); z++) {
            LatLng point = new LatLng(Float.parseFloat(listPoints.get(z).getAtitude()), Float.parseFloat(listPoints.get(z).getLongtude()));
            options.add(point);
            targetPoint = listPoints.get(z);
        }



        if (currentLine != null) {
            currentLine.remove();
        }
        currentLine = mMap.addPolyline(options);
        this.lineDrawed = true;
        this.currentLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.direction));


       /* line.setEndCap(
                new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow),
                        16));*/
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (currentLocation == null) {
                    currentLocation = new Point();
                    oldLocation = latLng;
                } else {
                    oldLocation = new LatLng(Float.parseFloat(currentLocation.getAtitude()), Float.parseFloat(currentLocation.getLongtude()));
                }
                currentLocation.setAtitude(String.valueOf(location.getLatitude()));
                currentLocation.setLongtude(String.valueOf(location.getLongitude()));

                newLocaation = latLng;
                if (currentLocationMarker == null) {
                    currentLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("موقعي")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                } else {
                    currentLocationMarker.setPosition(latLng);
                }
                if (this.listPoints.size() > 0) {
                    currentLocationMarker.setFlat(true);
                    float bearing = (float) bearingBetweenLocations(oldLocation, newLocaation);
                    rotateMarker(currentLocationMarker, bearing);
                    if (lineDrawed) {
                        if (enableAsynDraw) {
                            this.drawPolyline(this.listPoints);
                        }
                    }
                }

                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            if (getActivity() != null && lineDrawed && currentLocation != null && targetPoint != null) {
                double distance = this.utils.meterDistanceBetweenPoints(Float.parseFloat(targetPoint.getAtitude()), Float.parseFloat(targetPoint.getLongtude()), location.getLatitude(), location.getLongitude());
                DecimalFormat df = new DecimalFormat("0");
                System.out.println("distance : " + df.format(distance));
                utils.useArabicTTS("باقي ليك " +df.format(distance) + "متر");
                if (distance <= 10) {
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(5000);
                    }
                }

            }
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
                Point p = currentLocation;
                p.setLongtude(String.valueOf(longitude));
                p.setAtitude(String.valueOf(latitude));
                DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
                String date = df.format(Calendar.getInstance().getTime());
                final PointsBDD pointsBDD = new PointsBDD(getActivity());
                pointsBDD.open();
                p.setCreateDate(date);
                p.setStat("true");
                if (pointsBDD.insertPoint(p) > 0) {
                    Toast.makeText(getActivity(), "تم حفظ النقطة", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                pointsBDD.close();
                LatLng latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                markers.add(mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(p.getCreateDate())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.anchor))));
            } else {
                gpsTracker.showSettingsAlert(dialog);
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        if (!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 2000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    float bearing = -rot > 180 ? rot / 2 : rot;

                    marker.setRotation(bearing);

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Point pt = null;
        for (Marker mrk : markers) {
            if (mrk.getId().equals(marker.getId())) {
                pt = new Point(String.valueOf(marker.getPosition().latitude), String.valueOf(marker.getPosition().longitude));
                break;
            }
        }

        if (pt != null) {
            ArrayList<Point> mrkClickedList = new ArrayList<>();
            mrkClickedList.add(pt);
            this.drawPolyline(mrkClickedList);
        }
    }
}
