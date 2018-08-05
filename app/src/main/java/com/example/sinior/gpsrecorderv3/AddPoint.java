package com.example.sinior.gpsrecorderv3;

import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sinior.gpsrecorderv3.BDD.PointsBDD;
import com.example.sinior.gpsrecorderv3.Beans.Point;
import com.example.sinior.gpsrecorderv3.Tools.GpsTracker;
import com.example.sinior.gpsrecorderv3.Tools.MyLocation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddPoint.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddPoint#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPoint extends Fragment implements LocationListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GpsTracker gpsTracker;
    private EditText tvLatitude, tvLongitude;
    private ImageView imgSavePoint, imgResetPoint;
    private OnFragmentInteractionListener mListener;
    LocationManager locationManager;
    Point pt;

    public AddPoint() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPoint.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPoint newInstance(String param1, String param2) {
        AddPoint fragment = new AddPoint();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    public void getLocation(View view) {
        gpsTracker = new GpsTracker(MainActivity.getAppContext());
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            tvLatitude.setText(String.valueOf(latitude));
            tvLongitude.setText(String.valueOf(longitude));
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_point, container, false);
        tvLatitude = (EditText) view.findViewById(R.id.tbLatitude);
        tvLongitude = (EditText) view.findViewById(R.id.tbLongitude);
        imgSavePoint = (ImageView) view.findViewById(R.id.imgSavePoint);
        imgResetPoint = (ImageView) view.findViewById(R.id.imgResetPoint);
        pt = new Point();
        this.getLocation();
        imgSavePoint.setOnClickListener(this);
        imgResetPoint.setOnClickListener(this);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        tvLatitude.setText(String.valueOf(location.getLatitude()));
        tvLongitude.setText(String.valueOf(location.getLongitude()));
        pt.setAtitude(String.valueOf(location.getLatitude()));
        pt.setLongtude(String.valueOf(location.getLongitude()));
        try {
            Geocoder geocoder = new Geocoder(MainActivity.getAppContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            /*tvLatitude.setText(tvLatitude.getText() + "\n" + addresses.get(0).getAddressLine(0) + ", " +
                    addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2));*/
            tvLatitude.setText(String.valueOf(location.getLatitude()));
            tvLongitude.setText(String.valueOf(location.getLongitude()));
            pt.setAtitude(String.valueOf(location.getLatitude()));
            pt.setLongtude(String.valueOf(location.getLongitude()));
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
        Toast.makeText(MainActivity.getAppContext(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.getLocation) {
            Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgSavePoint:
                this.addPoint(pt);
                break;
            case R.id.imgResetPoint:

                break;

        }
    }

    public void addPoint(Point pt) {
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        String date = df.format(Calendar.getInstance().getTime());
        final PointsBDD pointsBDD = new PointsBDD(getActivity());
        pointsBDD.open();
        pt.setCreateDate(date);
        pt.setStat("true");
        if (pointsBDD.insertPoint(pt) > 0)
            System.out.println("Ajouter bien fait");
        pointsBDD.close();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
