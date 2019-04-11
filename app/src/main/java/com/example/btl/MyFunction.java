package com.example.btl;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyFunction {

    public static LatLng myLocation;
    public static double khoagCach(LatLng start, LatLng end){
        double la1=start.latitude, lo1=start.longitude;
        double la2=end.latitude, lo2=end.longitude;

        double dLat = (la2 - la1) * (Math.PI*1.0 / 180);
        double dLon = (lo2 - lo1) * (Math.PI*1.0 / 180);
        double la1ToRad = la1 * (Math.PI*1.0 / 180);
        double la2ToRad = la2 * (Math.PI *1.0/ 180);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(la1ToRad)
                * Math.cos(la2ToRad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6366 * c;
        return Math.round(d*10000)*1.0/10000;
//        Location loc1 = new Location("");
//        loc1.setLatitude(start.latitude);
//        loc1.setLongitude(start.longitude);
//
//        Location loc2 = new Location("");
//        loc2.setLatitude(end.latitude);
//        loc2.setLongitude(end.longitude);
//
//        return loc1.distanceTo(loc2)*1.0/1000;
    }

    public static int chiPhi(LatLng start, LatLng end){
        int gia = (int) Math.round(khoagCach(start,end)*10000);
        if(gia<10000)
            gia=10000;
        return gia;
    }

    public static ArrayList<String> getAdress(Context context, LatLng location) throws IOException {
        ArrayList<String> a= new ArrayList<String>();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> listAdress = geocoder.getFromLocation(location.latitude, location.longitude, 1);
        for(int i=0; i<listAdress.size(); i++){
            String s = "";
            if (listAdress.get(i).getThoroughfare() != null)
                s += listAdress.get(i).getThoroughfare() + " - ";
//                            if(listAdress.get(0).getFeatureName()!=null)
//                                s+=listAdress.get(0).getFeatureName() + " ";
            if (listAdress.get(i).getSubAdminArea() != null)
                s += listAdress.get(i).getSubAdminArea() + " - ";
            if (listAdress.get(i).getLocality() != null)
                s += listAdress.get(i).getLocality() + " ";
//                            if(listAdress.get(0).getSubLocality()!=null)
//                                s+=listAdress.get(0).getSubLocality() + " ";
            a.add(s);
        }
        return a;
    }
}

