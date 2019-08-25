package com.example.sinior.gpsrecorderv3;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sinior.gpsrecorderv3.Adapter.PointsAdapter;
import com.example.sinior.gpsrecorderv3.BDD.PointsBDD;
import com.example.sinior.gpsrecorderv3.Beans.Point;
import com.example.sinior.gpsrecorderv3.Tools.GpsTracker;
import com.example.sinior.gpsrecorderv3.Tools.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListPoints extends Fragment implements LocationListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView lvPoints;
    public ArrayList<Point> pointsList;
    PointsAdapter adapter;
    Button imgAddPoint, imgDeleteAll;
    TextView tvDistance;
    ImageView imgMultiRoutes;
    public Point currentLocation;
    private Utils utils;
    private Point nearsetPoint;
    private Double nearsetPointDistance;

    private OnFragmentInteractionListener mListener;

    FragmentTransaction frgmtTrans;

    public ListPoints() {
        // Required empty public constructor
    }

    public static ListPoints newInstance(String param1, String param2) {
        ListPoints fragment = new ListPoints();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_points, container, false);
        lvPoints = (ListView) view.findViewById(R.id.lvPoints);
        imgAddPoint = (Button) view.findViewById(R.id.btnAddPoint);
        imgDeleteAll = (Button) view.findViewById(R.id.btnDeleteAll);
        tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        imgMultiRoutes = (ImageView) view.findViewById(R.id.imgMultiRoute);
        imgAddPoint.setOnClickListener(this);
        imgDeleteAll.setOnClickListener(this);
        imgMultiRoutes.setOnClickListener(this);
        final PointsBDD pointsBDD = new PointsBDD(getActivity());
        pointsBDD.open();
        pointsList = pointsBDD.getAllPoint();
        adapter = new PointsAdapter(getActivity(), pointsList);
        lvPoints.setAdapter(adapter);
        pointsBDD.close();
        this.utils = new Utils(getActivity());
        currentLocation = new Point();
        GpsTracker tracker = new GpsTracker(getActivity());
        if (!tracker.canGetLocation()) {
            tracker.showSettingsAlert(null);
        } else {
            currentLocation.setAtitude(String.valueOf(tracker.getLatitude()));
            currentLocation.setLongtude(String.valueOf(tracker.getLongitude()));
            this.nearsetPoint = this.utils.getNearestPoint(pointsList, currentLocation);
            if (this.nearsetPoint == null) {
                tvDistance.setText("0");
            } else {
                this.nearsetPointDistance = this.utils.meterDistanceBetweenPoints(Float.parseFloat(currentLocation.getAtitude()), Float.parseFloat(currentLocation.getLongtude()), Float.parseFloat(nearsetPoint.getAtitude()), Float.parseFloat(nearsetPoint.getLongtude()));
                DecimalFormat df = new DecimalFormat("0.00");
                tvDistance.setText(String.valueOf(df.format(this.nearsetPointDistance)));
            }
        }


        return view;
    }

    public ArrayList<Point> sortByNearstPoint(){
        ArrayList<Point> sortedList = new ArrayList<Point>();
        sortedList.add(this.nearsetPoint);
        ArrayList<Point> tmpList = this.pointsList;
        Point tmpNearstPoint = nearsetPoint;
        tmpList.remove(tmpNearstPoint);
        while (sortedList.size() == pointsList.size()) {
            tmpNearstPoint = this.utils.getNearestPoint(tmpList, tmpNearstPoint);
            sortedList.add(tmpNearstPoint);
            tmpList.remove(tmpNearstPoint);
        }

        return sortedList;
    }

    @Override
    public void onClick(View view) {
        //creating fragment object
        android.app.Fragment fragment = null;
        switch (view.getId()) {
            case R.id.btnAddPoint:
                fragment = new AddPoint();
                break;
            case R.id.imgMultiRoute:
                fragment = new MapFragement();
                Bundle bundle = new Bundle();
                bundle.putBoolean("multiRoutes", true);
                fragment.setArguments(bundle);
                break;
            case R.id.btnDeleteAll:
                new AlertDialog.Builder(getActivity())
                        .setMessage("هل أنت متأكد من مسح الكل ؟")
                        .setCancelable(false)
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final PointsBDD pointsBDD = new PointsBDD(getActivity());
                                pointsBDD.open();
                                pointsBDD.removeAllPoints();
                                pointsList = pointsBDD.getAllPoint();
                                adapter = new PointsAdapter(getActivity(), pointsList);
                                lvPoints.setAdapter(adapter);
                                pointsBDD.close();
                            }
                        })
                        .setNegativeButton("لا", null)
                        .show();

                break;

        }
        //replacing the fragment
        if (fragment != null) {
            final FragmentManager fragmentManager = getFragmentManager();
            frgmtTrans = fragmentManager.beginTransaction();
            frgmtTrans.replace(R.id.content_frame, fragment);
            frgmtTrans.commit();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation.setAtitude(String.valueOf(location.getLatitude()));
        currentLocation.setLongtude(String.valueOf(location.getLongitude()));

        Double distance = this.utils.meterDistanceBetweenPoints(Float.parseFloat(currentLocation.getAtitude()), Float.parseFloat(currentLocation.getLongtude()), 0, 0);
        System.out.println("distance : " + distance);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
