package com.example.sinior.gpsrecorderv3.Tools;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import com.example.sinior.gpsrecorderv3.Beans.Point;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Utils {

    private final Context mContext;
    TextToSpeech textToSpeech;

    public Utils(Context mContext) {
        this.mContext = mContext;
        textToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    public void exitApplication() {
        System.exit(1);
    }

    public double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f / Math.PI);

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

    public Point getNearestPoint(List<Point> points, Point currentPoint) {
        Point nearestPoint = null;
        Double tmpDistance = null;
        for (Point p : points) {
            Double pDistance = this.meterDistanceBetweenPoints(Float.parseFloat(currentPoint.getAtitude()), Float.parseFloat(currentPoint.getLongtude()), Float.parseFloat(p.getAtitude()), Float.parseFloat(p.getLongtude()));
            if (tmpDistance == null) {
                tmpDistance = pDistance;
                nearestPoint = p;
            } else if (tmpDistance > pDistance) {
                tmpDistance = pDistance;
                nearestPoint = p;
            }
        }
        return nearestPoint;
    }

    public ArrayList<Point> sortByNearstPoint(Point nearsetPoint, ArrayList pointsList) {
        ArrayList<Point> sortedList = new ArrayList<Point>();
        sortedList.add(nearsetPoint);
        ArrayList<Point> tmpList = pointsList;
        Point tmpNearstPoint = nearsetPoint;
        tmpList.remove(tmpNearstPoint);
        while (sortedList.size() == pointsList.size()) {
            tmpNearstPoint = this.getNearestPoint(tmpList, tmpNearstPoint);
            sortedList.add(tmpNearstPoint);
            tmpList.remove(tmpNearstPoint);
        }

        return sortedList;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public void useTextToSpeech(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    }
}
