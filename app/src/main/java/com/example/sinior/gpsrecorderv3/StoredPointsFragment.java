package com.example.sinior.gpsrecorderv3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.sinior.gpsrecorderv3.Adapter.StoredDataAdapter;
import com.example.sinior.gpsrecorderv3.BDD.PointsBDD;
import com.example.sinior.gpsrecorderv3.Beans.Point;
import com.example.sinior.gpsrecorderv3.Tools.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import cn.pedant.SweetAlert.SweetAlertDialog;


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
    private String myStoredName;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mDatabase.getReference();
    Button btnNewStore, btnDeleteAllStore;
    ArrayList<HashMap<String, Point>> listStoredData;
    ArrayList<Map<String, ArrayList<Point>>> listOfCurrentDateItems;
    ArrayList<String> listOfDbKeys;
    ListView lvStoredData;
    StoredDataAdapter adapter;
    TextToSpeech textToSpeech;
    private Utils utils;
    HashMap<String, Point> pts;



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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stored_points, container, false);
        utils = new Utils(view.getContext());
        lvStoredData = (ListView) view.findViewById(R.id.lvStoredData);
        listStoredData = new ArrayList<>();
        listOfDbKeys = new ArrayList<>();
        adapter = new StoredDataAdapter(getActivity(), listStoredData, listOfDbKeys);
        lvStoredData.setAdapter(adapter);
        btnNewStore = (Button) view.findViewById(R.id.btnNewStore);
        btnNewStore.setOnClickListener(this);
        btnDeleteAllStore = (Button) view.findViewById(R.id.btnDeleteAllStore);
        btnDeleteAllStore.setOnClickListener(this);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("myStoredName", Context.MODE_PRIVATE);
        myStoredName = sharedPref.getString("myStoredName", null);

        mDatabaseReference = mDatabase.getReference().child(myStoredName);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listStoredData = new ArrayList<>();
                listOfDbKeys = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    pts = new HashMap<>();
                    HashMap<String, Point> ptsList = (HashMap<String, Point>) singleSnapshot.getValue();
                    String itemKey = ptsList.keySet().toArray()[0].toString();

                    Gson gson = new Gson();
                    String jsonString = gson.toJson(ptsList);
                    jsonString = String.join(":", Arrays.copyOfRange(jsonString.split(":"), 1, jsonString.split(":").length)) ;
                    jsonString = String.join("", jsonString.split("],\"id\"")[0]) ;
                    jsonString = jsonString + "]}";
                    jsonString = jsonString.replaceAll("\"\"", "\":\"");

                    Point pt = gson.fromJson(jsonString, Point.class);
                    if(!pt.getRemoved()){
                        listOfDbKeys.add(singleSnapshot.getKey());
                        pts.put(itemKey, pt);
                        listStoredData.add(pts);
                    }

                }
                adapter = new StoredDataAdapter(getActivity(), listStoredData, listOfDbKeys);
                lvStoredData.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

                System.out.println("Failed to read value." + error.toException());
            }
        });

        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        if (!utils.isInternetAvailable()) {

            //Error message
            SweetAlertDialog dialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("لست متصلا بالأنترنت !");
            dialog.setOnShowListener(new DialogInterface.OnShowListener()
            {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    utils.useTextToSpeech("makaynach internet");
                }

            });

            dialog.show();
        }

        return view;
    }


    public int getLengthOfCurrentDateItems(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            List<Object> temp = listStoredData.stream().filter(new Predicate<Map<String, Point>>() {
                @Override
                public boolean test(Map<String, Point> item) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    return item.entrySet().iterator().next().getKey().contains(dateFormat.format(date).replaceAll("\\.", "-"));
                }
            }).collect(Collectors.toList());

            listOfCurrentDateItems = new ArrayList(temp);
        }

        return listOfCurrentDateItems.size();
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
    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNewStore:
                Point pointsList = new Point();
                final PointsBDD pointsBDD = new PointsBDD(getActivity());
                pointsBDD.open();
                pointsList.setPtsList(pointsBDD.getAllPoint());
                pointsBDD.close();

                Map<String, Point> pts = new HashMap<String, Point>();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String itemKey = dateFormat.format(date).replaceAll("\\.", "-") + " (" + getLengthOfCurrentDateItems() + ")";
                pts.put(itemKey, pointsList);
                DatabaseReference ref = mDatabase.getReference().child(myStoredName);
                ref.push().setValue(pts)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                utils.useTextToSpeech("Safi tsajal");
                                // 1. Success message
                                new SweetAlertDialog(getActivity())
                                        .setTitleText("تم الحفظ بنجاح !")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                // ...
                            }
                        });
                ;


                break;
            case R.id.btnDeleteAllStore:
                new AlertDialog.Builder(getActivity())
                        .setMessage("هل أنت متأكد من مسح الكل ؟")
                        .setCancelable(false)
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                for(int i=0; i< listStoredData.size(); i++){
                                    String itemKey = listStoredData.get(i).keySet().toArray()[0].toString();
                                    mDatabaseReference = mDatabase.getReference().child(myStoredName);
                                    utils.removeListPointsDb(mDatabaseReference, itemKey, listOfDbKeys.get(i));
                                }
                            }
                        })
                        .setNegativeButton("لا", null)
                        .show();
                break;

        }

    }


    public void onCancelled(@NonNull DatabaseError databaseError) {

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
