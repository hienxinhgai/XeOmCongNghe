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

import static com.example.btl.MyFunction.myLocation;

public class YeuCauDatXeActivity extends AppCompatActivity {
    private ListView lvYeuCauDatXe;
    private DatabaseReference database;
    private List<DatXe> list;
    private List<String>listView;

    void Toasts(String s){
        Toast.makeText(YeuCauDatXeActivity.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeu_cau_dat_xe);

        database = FirebaseDatabase.getInstance().getReference();
        lvYeuCauDatXe= (ListView)findViewById(R.id.lvYeuCauDatXe);

        database.child("yeuCauDatXe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<DatXe>();
                listView = new ArrayList<String>();
                for(DataSnapshot dts : dataSnapshot.getChildren()){
                    DatXe d = dts.getValue(DatXe.class);
                    if(d.check==true){
                        list.add(d);
                        LatLng l = new LatLng(d.lat,d.lng);
                        listView.add("vị trí cách bạn " + MyFunction.khoagCach(l,myLocation) + " km");
                    }
                }

                ArrayAdapter<String> arrayAdapter
                        = new ArrayAdapter<String>(YeuCauDatXeActivity.this, android.R.layout.simple_list_item_1 ,listView);
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
                        String message= "SĐT khách: " + d.SDT + "\nVi trí khách cách bạn: "
                                + MyFunction.khoagCach(myLocation,location) + " km"
                                + "\nQuãng đường: " + d.khoangCach
                                + "\nTổng tiền: " + d.chiPhi + "\n";


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

