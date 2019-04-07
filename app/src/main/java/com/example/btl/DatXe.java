package com.example.btl;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DatXe {
    public String SDT;
    public double khoangCach;
    public int chiPhi;
    public double lat;
    public double lng;
    public String viTriKhach;
    public String viTriDich;
    public String ghiChu;

    public DatXe(){

    }
    public DatXe(String s,double k,int c, boolean ck, double la,double lo, String v, String vd, String g){
        SDT=s;
        khoangCach=k;
        chiPhi=c;
        lat=la;
        lng=lo;
        viTriKhach=v;
        viTriDich=vd;
        ghiChu=g;
    }
}

