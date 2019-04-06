package com.example.btl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.example.btl.MyFunction.myLocation;

public class AdapterYeuCauDatXe extends BaseAdapter {
    private ArrayList<DatXe> list;
    private Context context;
    private int layout;

    public AdapterYeuCauDatXe(Context context, int layout, ArrayList<DatXe> ycdx) {
        this.context = context;
        this.layout = layout;
        this.list = ycdx;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(layout,null);
        TextView tvYCDX = (TextView)convertView.findViewById(R.id.tvYCDX);
        LatLng l = new LatLng(list.get(position).lat,list.get(position).lng);
        tvYCDX.setText("vị trí cách bạn " + MyFunction.khoagCach(l,myLocation) + " km");

        return  convertView;
    }
}
