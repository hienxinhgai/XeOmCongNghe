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
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
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
    private ArrayList<DatXe> list;

    void Toasts(String s){
        Toast.makeText(YeuCauDatXeActivity.this,s,Toast.LENGTH_LONG).show();
    }

    void capNhatDanhSach(){
        database.child("yeuCauDatXe").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<DatXe>();
                for(DataSnapshot dts : dataSnapshot.getChildren()){
                    DatXe d = dts.getValue(DatXe.class);
                    list.add(d);
                    LatLng l = new LatLng(d.lat,d.lng);
                }

                AdapterYeuCauDatXe adapterYeuCauDatXe
                        = new AdapterYeuCauDatXe(YeuCauDatXeActivity.this, R.layout.item_yeucaudatxe ,list);
                lvYeuCauDatXe.setAdapter(adapterYeuCauDatXe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeu_cau_dat_xe);

        database = FirebaseDatabase.getInstance().getReference();
        lvYeuCauDatXe= (ListView)findViewById(R.id.lvYeuCauDatXe);

        database.child("yeuCauDatXe").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                capNhatDanhSach();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                capNhatDanhSach();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //click yeu cau dat xe
        lvYeuCauDatXe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final DatXe d=list.get(position);
                database.child("GPS_NguoiDung").child(d.SDT).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LatLng location = new LatLng(dataSnapshot.child("latitude").getValue(double.class),dataSnapshot.child("longitude").getValue(double.class));
                        String message= "Vị trí khách cách bạn: " + MyFunction.khoagCach(myLocation,location) + " km"
                                +"\nĐiểm đến: " + d.viTriDich
                                + "\nQuãng đường: " + d.khoangCach + " km"
                                + "\nTổng tiền: " + d.chiPhi + " đồng\n"
                                + "\nGhi chú: " + d.ghiChu + "\n";
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
                                        database.child("yeuCauDatXe").child(d.SDT).removeValue();
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
            }
        });
    }
}

