package com.example.sinior.gpsrecorderv3.Adapter;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
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
import com.example.sinior.gpsrecorderv3.R;
import com.example.sinior.gpsrecorderv3.Tools.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class StoredDataAdapter extends ArrayAdapter {
    private ArrayList<String> listOfDbKey;
    ArrayList<HashMap<String, Point>> storedData;
    Context context;
    FragmentTransaction frgmtTrans;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mDatabase.getReference();
    private Utils utils;

    public StoredDataAdapter(Context context, ArrayList<HashMap<String, Point>> storedData, ArrayList<String> listOfDbKey) {
        super(context, 0, storedData);
        this.storedData = storedData;
        this.context = context;
        this.listOfDbKey = listOfDbKey;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stored_item, parent, false);
        }
            final int index = position;
            final HashMap<String, Point> dataItem = storedData.get(position);
            final String itemKey = dataItem.keySet().toArray()[0].toString();
            // Lookup view for data population
            TextView tvStoredDate = (TextView) convertView.findViewById(R.id.tvStoreDate);
            TextView tvNbrItem = (TextView) convertView.findViewById(R.id.tvNbrItem);
            ImageView tvRestore = (ImageView) convertView.findViewById(R.id.tvRestore);
            ImageButton btnDeleteItem = (ImageButton) convertView.findViewById(R.id.btnDeleteItem);
            CircleImageView cimag = (CircleImageView) convertView.findViewById(R.id.profile_image);
            utils = new Utils(convertView.getContext());
            tvRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    // Setting Dialog Title
                    alertDialog.setTitle("عملية الإستعادة");
                    // Setting Dialog Message
                    alertDialog.setMessage("هل أنت متأكد من ذلك ؟ " );
                    // On pressing Settings button

                    alertDialog.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            final PointsBDD pointsBDD = new PointsBDD(context);
                            pointsBDD.open();
                            for (Map.Entry<String, Point> entry : dataItem.entrySet()) {

                                for(Point item : entry.getValue().getPtsList()){
                                    pointsBDD.insertPoint(item);
                                }
                            }

                            pointsBDD.close();
                            final FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                            frgmtTrans = fragmentManager.beginTransaction();
                            frgmtTrans.replace(R.id.content_frame, new ListPoints());
                            frgmtTrans.commit();
                        }
                    });
                    // on pressing cancel button
                    alertDialog.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();

                }
            });
            btnDeleteItem.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    // Setting Dialog Title
                    alertDialog.setTitle("عملية المسح");
                    // Setting Dialog Message
                    alertDialog.setMessage("هل أنت متأكد من ذلك ؟ " );
                    // On pressing Settings button

                    alertDialog.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            mDatabaseReference = mDatabase.getReference().child("bika");
                            utils.removeListPointsDb(mDatabaseReference, itemKey, listOfDbKey.get(index));
                        }
                    });
                    // on pressing cancel button
                    alertDialog.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                                dialog.dismiss();
                        }
                    });
                    alertDialog.show();

                }
            });
            // Populate the data into the template view using the data object
            tvStoredDate.setText(itemKey);
            tvNbrItem.setText(itemKey);

        return convertView;
    }
}
