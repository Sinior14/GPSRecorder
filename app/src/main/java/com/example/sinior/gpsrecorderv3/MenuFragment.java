package com.example.sinior.gpsrecorderv3;

import android.app.FragmentManager;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sinior.gpsrecorderv3.Tools.Utils;
import com.google.android.gms.maps.MapFragment;

import androidx.annotation.RequiresApi;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment implements View.OnClickListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Utils utils;

    ImageView imgCurrentPlace;
    ImageView imgSavePoint;
    ImageView imgListPoints;
    ImageView imgExit;

    TextView tvCurrentPlace;
    TextView tvSaveImport;
    TextView tvListPoints;
    TextView tvExit;

    FragmentTransaction frgmtTrans;

    private OnFragmentInteractionListener mListener;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_menu, container, false);
        tvExit= (TextView) view.findViewById(R.id.tvExit);
        tvCurrentPlace= (TextView) view.findViewById(R.id.tvCurrentPlace);
        tvListPoints = (TextView) view.findViewById(R.id.tvListPointsMenu);
        tvSaveImport = (TextView) view.findViewById(R.id.tvSaveImport);

        imgExit = (ImageView) view.findViewById(R.id.imgExit);
        imgListPoints = (ImageView) view.findViewById(R.id.imgListPointsMenu);
        imgCurrentPlace = (ImageView) view.findViewById(R.id.imgCurrentPlace);
        imgSavePoint = (ImageView) view.findViewById(R.id.imgSavePoints);

        tvCurrentPlace.setOnClickListener(this);
        tvListPoints.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        tvSaveImport.setOnClickListener(this);
        imgExit.setOnClickListener(this);
        imgCurrentPlace.setOnClickListener(this);
        imgListPoints.setOnClickListener(this);
        imgSavePoint.setOnClickListener(this);

        return view;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View view) {
        //creating fragment object
        System.out.println("view.getId()");
        System.out.println(view.getId());
        android.app.Fragment fragment = null;
        switch (view.getId()){
            case R.id.tvCurrentPlace:
                fragment = new MapFragement();
                break;
            case R.id.imgCurrentPlace:
                fragment = new MapFragement();
                break;
            case R.id.tvListPointsMenu:
                fragment = new ListPoints();
                break;
            case R.id.imgListPointsMenu:
                fragment = new ListPoints();
                break;
            case R.id.tvExit:
                this.utils = new Utils(getActivity().getApplicationContext());
                this.utils.exitApplication();
                break;
            case R.id.imgExit:
                this.utils = new Utils(getActivity().getApplicationContext());
                this.utils.exitApplication();
                break;
            case R.id.tvSaveImport:
                fragment = new StoredPointsFragment();
                break;
            case R.id.imgSavePoints:
                fragment = new StoredPointsFragment();
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
