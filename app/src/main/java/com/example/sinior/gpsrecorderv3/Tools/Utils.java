package com.example.sinior.gpsrecorderv3.Tools;

import android.content.Context;
import android.content.Intent;

import com.example.sinior.gpsrecorderv3.Beans.Point;

import java.util.List;

public class Utils {

    private final Context mContext;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    public void exitApplication(){
        System.exit(1);
    }

    public double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    public Point getNearestPoint(List<Point> points, Point currentPoint){
        Point nearestPoint = null;
        Double tmpDistance = null;
        for (Point p : points)
        {
            Double pDistance = this.meterDistanceBetweenPoints(Float.parseFloat(currentPoint.getAtitude()),Float.parseFloat(currentPoint.getLongtude()),Float.parseFloat(p.getAtitude()),Float.parseFloat(p.getLongtude()));
            if(tmpDistance == null){
                tmpDistance = pDistance;
                nearestPoint = p;
            }else if(tmpDistance > pDistance){
                tmpDistance = pDistance;
                nearestPoint = p;
            }
        }
        return nearestPoint;
    }
}
