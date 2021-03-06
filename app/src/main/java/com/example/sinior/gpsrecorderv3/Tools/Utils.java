package com.example.sinior.gpsrecorderv3.Tools;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.example.sinior.gpsrecorderv3.Beans.Point;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.app.Fragment;

public class Utils {

    private final Context mContext;
    TextToSpeech textToSpeech;
    private ArabicTTS tts;

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
        // Creating a new object of the ArabicTTS librrary
        tts = new ArabicTTS();
        // Preparing the language
        tts.prepare(mContext);
    }

    public void exitApplication() {
        System.exit(1);
    }

    public double meterDistanceBetweenPoints(float lat_a, float lng_a, double lat_b, double lng_b) {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

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
            final String command = "ping -c 1 google.com";
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void useTextToSpeech(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void useArabicTTS(String message) {
        tts.talk(message);
    }

    public static ArrayList<Point> cloneList(ArrayList<Point> dogList) {
        ArrayList<Point> clonedList = new ArrayList<Point>(dogList.size());
        for (Point dog : dogList) {
            clonedList.add(new Point(dog));
        }
        return clonedList;
    }

    public void destroyFragment(Fragment fragment){
        if(fragment != null){
            //getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void removeListPointsDb(DatabaseReference db, String key, String id) {
        db.child(id).child(key).child("removed").setValue(true);
        /*point.setRemoved(true);
        HashMap<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, point);
        db.updateChildren(childUpdates);*/
    }
}
