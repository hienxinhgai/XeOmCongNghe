package com.example.btl;

import com.google.android.gms.maps.model.LatLng;

public class MyFunction {

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
    }

    public static int chiPhi(LatLng start, LatLng end){
        int gia = (int) Math.round(khoagCach(start,end)*10000);
        if(gia<10000)
            gia=10000;
        return gia;
    }

}

