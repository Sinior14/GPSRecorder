package com.example.sinior.gpsrecorderv3.Adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sinior.gpsrecorderv3.BDD.PointsBDD;
import com.example.sinior.gpsrecorderv3.Beans.Point;
import com.example.sinior.gpsrecorderv3.ListPoints;
import com.example.sinior.gpsrecorderv3.MapFragement;
import com.example.sinior.gpsrecorderv3.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoredDataAdapter extends ArrayAdapter {
    ArrayList<Map<String, Point>> storedData;
    Context context;
    FragmentTransaction frgmtTrans;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mDatabase.getReference();

    public StoredDataAdapter(Context context, ArrayList<Map<String, Point>> storedData) {
        super(context, 0, storedData);
        this.storedData = storedData;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stored_item, parent, false);
            final Map<String, Point> dataItem = storedData.get(position);
            // Lookup view for data population
            TextView tvStoredDate = (TextView) convertView.findViewById(R.id.tvStoreDate);
            TextView tvNbrItem = (TextView) convertView.findViewById(R.id.tvNbrItem);
            ImageView tvRestore = (ImageView) convertView.findViewById(R.id.tvRestore);
            ImageButton btnDeleteItem = (ImageButton) convertView.findViewById(R.id.btnDeleteItem);
            CircleImageView cimag = (CircleImageView) convertView.findViewById(R.id.profile_image);
            tvRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* MapFragement mapFragement = new MapFragement();
                    final FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                    frgmtTrans = fragmentManager.beginTransaction();
                    mapFragement.point = Pt;
                    // set Fragmentclass Arguments
                    // mapFragement.setArguments(bundle);
                    frgmtTrans.replace(R.id.content_frame, mapFragement);
                    frgmtTrans.commit();*/
                }
            });
            btnDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDatabaseReference = mDatabase.getReference().child("bika");

                    Map<String, Object> updateItem = new HashMap<>();
                    dataItem.entrySet().iterator().next().getValue().setRemoved(true);
                    updateItem.put(dataItem.entrySet().iterator().next().getKey(), dataItem.entrySet().iterator().next().getValue());
                    mDatabaseReference.setValue(dataItem.entrySet().iterator().next().getKey(), dataItem.entrySet().iterator().next().getValue());
                    System.out.println();
                    /*final PointsBDD pointsBDD = new PointsBDD(context);
                    pointsBDD.open();
                    pointsBDD.removePointWithID(Pt.getId());
                    pointsBDD.close();

                    final FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                    frgmtTrans = fragmentManager.beginTransaction();
                    frgmtTrans.replace(R.id.content_frame, new ListPoints());
                    frgmtTrans.commit();*/
                }
            });
            // Populate the data into the template view using the data object
            tvStoredDate.setText(dataItem.entrySet().iterator().next().getKey());
            tvNbrItem.setText(dataItem.entrySet().iterator().next().getKey());
        } else {
            final Map<String, Point> dataItem = storedData.get(position);
            // Lookup view for data population
            TextView tvStoredDate = (TextView) convertView.findViewById(R.id.tvStoreDate);
            TextView tvNbrItem = (TextView) convertView.findViewById(R.id.tvNbrItem);
            ImageView tvRestore = (ImageView) convertView.findViewById(R.id.tvRestore);
            ImageButton btnDeleteItem = (ImageButton) convertView.findViewById(R.id.btnDeleteItem);
            CircleImageView cimag = (CircleImageView) convertView.findViewById(R.id.profile_image);

            tvRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*MapFragement mapFragement = new MapFragement();
                    final FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                    frgmtTrans = fragmentManager.beginTransaction();
                    mapFragement.point = Pt;
                    // set Fragmentclass Arguments
                    // mapFragement.setArguments(bundle);
                    frgmtTrans.replace(R.id.content_frame, mapFragement);
                    frgmtTrans.commit();*/
                }
            });
            btnDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDatabaseReference = mDatabase.getReference().child("bika");

                    Map<String, Object> updateItem = new HashMap<>();
                    ArrayList<Point> values = new ArrayList<>(dataItem.values());
                    System.out.println( values);
                    /*Point pt =  values.get(0);
                    pt.setRemoved(true);
                    updateItem.put(dataItem.entrySet().iterator().next().getKey(), pt);
                    mDatabaseReference.setValue(dataItem.entrySet().iterator().next().getKey(), dataItem.entrySet().iterator().next().getValue());
                    /*final PointsBDD pointsBDD = new PointsBDD(context);
                    pointsBDD.open();
                    pointsBDD.removePointWithID(Pt.getId());
                    pointsBDD.close();

                    final FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                    frgmtTrans = fragmentManager.beginTransaction();
                    frgmtTrans.replace(R.id.content_frame, new ListPoints());
                    frgmtTrans.commit();*/
                }
            });
            // Populate the data into the template view using the data object
            tvStoredDate.setText(dataItem.entrySet().iterator().next().getKey());
            tvNbrItem.setText(dataItem.entrySet().iterator().next().getKey());
        }

        return convertView;
    }
}
