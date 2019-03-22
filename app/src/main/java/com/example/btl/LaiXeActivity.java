package com.example.btl;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LaiXeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference database;
    private Button btnGetLocationCustom;
    private String SDTKhach;

    private void Toasts(String s) {
        Toast.makeText(LaiXeActivity.this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lai_xe);

        btnGetLocationCustom = (Button) findViewById(R.id.btnGetLocationCustom);

        database = FirebaseDatabase.getInstance().getReference();

        database.child("yeuCauDatXe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String s= dataSnapshot.getValue().toString();
//                if(s.substring(s.length()-5,s.length()-1).equals("true")){
//                    Toasts(s + "vừa đặt xe");
//                }
//                Toasts(s + "vừa đặt xe");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDriver);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LaiXeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);

            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //click lay vi tri khach
        btnGetLocationCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(,15));
                if(SDTKhach==null){
                    Toasts("Chưa nhận chuyến");
                    return;
                }
                database.child("GPS_NguoiDung").child(SDTKhach).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mMap.clear();
                        LatLng location = new LatLng(dataSnapshot.child("latitude").getValue(double.class),dataSnapshot.child("longitude").getValue(double.class));
                        mMap.addMarker(new MarkerOptions().position(location).title("khách"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                Toasts(SDTKhach);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lai_xe,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemYeuCauDatXe:
                Intent intent = new Intent(LaiXeActivity.this,YeuCauDatXeActivity.class);
                startActivityForResult(intent,1);
                return true;
            case R.id.itemLogout:
                onDestroy();
                return true;
            case R.id.itemCallClient:
                if (ContextCompat.checkSelfPermission(LaiXeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LaiXeActivity.this, new String[]{Manifest.permission.CALL_PHONE},2);
                }
                else if(SDTKhach==null){
                    Toasts("Chưa nhận chuyến");
                }
                else
                {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + SDTKhach));
                    startActivity(intentCall);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1){
            if(resultCode== Activity.RESULT_OK){
                SDTKhach=data.getStringExtra("SDTKhach");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Toasts("Đăng xuất thành công");
    }
}
