package com.example.btl;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DatXe {
    public String SDT;
    public double khoangCach;
    public int chiPhi;
    public boolean check;
    public double lat;
    public double lng;

    public DatXe(){

    }
    public DatXe(String s,double k,int c, boolean ck, double la,double lo){
        SDT=s;
        khoangCach=k;
        chiPhi=c;
        check=ck;
        lat=la;
        lng=lo;
    }
}

