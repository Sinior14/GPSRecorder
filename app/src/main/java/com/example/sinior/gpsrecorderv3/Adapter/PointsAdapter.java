package com.example.sinior.gpsrecorderv3.Adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class PointsAdapter extends ArrayAdapter {
    ArrayList<Point> Points;
    Context context;
    FragmentTransaction frgmtTrans;


    public PointsAdapter(Context context, ArrayList<Point> medicaments) {
        super(context, 0, medicaments);
        this.Points = medicaments;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.points_item, parent, false);
        }
            final Point Pt = Points.get(position);
            // Lookup view for data population
            TextView tvAtLg = (TextView) convertView.findViewById(R.id.tvAtLg);
            TextView tvCreateDate = (TextView) convertView.findViewById(R.id.tvCreateDate);
            ImageView tvLireAlso = (ImageView) convertView.findViewById(R.id.tvLireAlso);
            ImageButton btnDeleteItem = (ImageButton) convertView.findViewById(R.id.btnDeleteItem);
            CircleImageView cimag = (CircleImageView) convertView.findViewById(R.id.profile_image);
            tvLireAlso.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MapFragement mapFragement = new MapFragement();
                    final FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                    frgmtTrans = fragmentManager.beginTransaction();
                    mapFragement.point = Pt;
                    frgmtTrans.replace(R.id.content_frame, mapFragement);
                    frgmtTrans.commit();
                }
            });
            btnDeleteItem.setOnClickListener(new View.OnClickListener() {
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
                            final PointsBDD pointsBDD = new PointsBDD(context);
                            pointsBDD.open();
                            pointsBDD.removePointWithID(Pt.getId());
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
            // Populate the data into the template view using the data object
            tvAtLg.setText(Pt.getAtitude() + " - "+ Pt.getLongtude());
            tvCreateDate.setText(Pt.getCreateDate());

        return convertView;
    }
}