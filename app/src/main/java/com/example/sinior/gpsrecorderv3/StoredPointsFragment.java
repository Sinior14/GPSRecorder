package com.example.sinior.gpsrecorderv3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sinior.gpsrecorderv3.Beans.Point;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoredPointsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoredPointsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoredPointsFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mDatabase.getReference();
    Button btnNewStore;

    private OnFragmentInteractionListener mListener;

    public StoredPointsFragment() {
        // Required empty public constructor
    }


    public static StoredPointsFragment newInstance(String param1, String param2) {
        StoredPointsFragment fragment = new StoredPointsFragment();
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
        View view =inflater.inflate(R.layout.fragment_stored_points, container, false);
        btnNewStore = (Button) view.findViewById(R.id.btnNewStore);

        btnNewStore.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNewStore:

                Point pt = new Point();
                pt.setLongtude("123");
                pt.setAtitude("456");
                //mDatabaseReference = mDatabase.getReference().child("bika");
                //mDatabaseReference.setValue("Donald Duck");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("bika");
                HashMap<String, Integer> pts = new HashMap<String, Integer>();
                //pts.
                //pts.put("alanisawesome", pt);

                mDatabaseReference.setValue(pt);

                myRef.setValue("Hello, World!");
                //mDatabaseReference = mDatabase.getReference().child("user");
                Point p = new Point();
                mDatabaseReference.setValue(p);
                break;
            case R.id.btnDeleteAllStore:
                new AlertDialog.Builder(getActivity())
                        .setMessage("هل أنت متأكد من مسح الكل ؟")
                        .setCancelable(false)
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /*final PointsBDD pointsBDD = new PointsBDD(getActivity());
                                pointsBDD.open();
                                pointsBDD.removeAllPoints();
                                pointsList = pointsBDD.getAllPoint();
                                adapter = new PointsAdapter(getActivity(), pointsList);
                                lvPoints.setAdapter(adapter);
                                pointsBDD.close();*/
                            }
                        })
                        .setNegativeButton("لا", null)
                        .show();
                break;

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
