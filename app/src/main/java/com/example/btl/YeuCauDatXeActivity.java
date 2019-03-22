package com.example.btl;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class YeuCauDatXeActivity extends AppCompatActivity {
    private ListView lvYeuCauDatXe;
    private DatabaseReference database;
    private List<DatXe> list;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng myLocation;

    void Toasts(String s){
        Toast.makeText(YeuCauDatXeActivity.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeu_cau_dat_xe);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //kiem tra bat gps chua
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toasts("chưa bật gps");
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

//        Toasts("Khoi tao" + GPS.getLatitude() + " " + GPS.getLongitude());

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //luu vi tri hien tai vao GPS
                //luu vi tri vao firebase database
                myLocation=new LatLng(location.getLatitude(),location.getLongitude());
//                Toasts(GPS.getLatitude() + " " + GPS.getLongitude());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {


            }

            @Override
            public void onProviderDisabled(String provider) {
                // Call your Alert message
                Toasts("chưa bật gps");
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(YeuCauDatXeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);

            return;
        }

        //goi den ham onLocationChanged o tren
        locationManager.requestLocationUpdates("gps", 1000, 1, locationListener);
        database = FirebaseDatabase.getInstance().getReference();
        lvYeuCauDatXe= (ListView)findViewById(R.id.lvYeuCauDatXe);

        database.child("yeuCauDatXe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<DatXe>();
                for(DataSnapshot dts : dataSnapshot.getChildren()){
                    DatXe d = dts.getValue(DatXe.class);
                    if(d.check==true)
                        list.add(d);
//                    if(dts.getValue().toString().equals("1"))
//                        list.add(dts.getKey().toString());
                }

                ArrayAdapter<DatXe> arrayAdapter
                        = new ArrayAdapter<DatXe>(YeuCauDatXeActivity.this, android.R.layout.simple_list_item_1 , list);
                lvYeuCauDatXe.setAdapter(arrayAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

  

        lvYeuCauDatXe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final DatXe d=list.get(position);
                database.child("GPS_NguoiDung").child(d.SDT).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LatLng location = new LatLng(dataSnapshot.child("latitude").getValue(double.class),dataSnapshot.child("longitude").getValue(double.class));
                        String message= "SĐT khách: " + d.SDT + "\nVi trí khách cách bạn: " ;
                        if(myLocation!=null){
                            message += MyFunction.khoagCach(myLocation,location) + " km";
                        }
                        message += "\nQuãng đường: " + d.khoangCach;
                        message += "\nChi phí: " + d.chiPhi + "\n";


                        new AlertDialog.Builder(YeuCauDatXeActivity.this)
                                .setTitle("Thông tin chuyến")
                                .setMessage(message)
                                .setPositiveButton("Nhận", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.putExtra("SDTKhach",d.SDT);
                                        setResult(Activity.RESULT_OK,intent);
                                        Toasts("nhận chuyến từ " + d.SDT);
                                        database.child("yeuCauDatXe").child(d.SDT).child("check").setValue(false);
                                        finish();
                                    }
                                })
                                .setNegativeButton("Quay lại", null)
                                .show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





//                startActivity(intent);

//                finish();
            }
        });
    }
}

