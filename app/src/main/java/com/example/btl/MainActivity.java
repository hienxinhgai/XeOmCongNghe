package com.example.btl;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Button btnDangKy, btnDangNhap;
    EditText edtSDT,edtMK;
    DatabaseReference database;
    private LocationManager locationManager;
    private LocationListener locationListener;


    private void Toasts(String s){
         Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
     }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private  boolean isGPSEnable(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        if(!isNetworkConnected()){
            Toasts("Chưa kết nối internet");
        }
        if(!isGPSEnable()){
            Toasts("Chưa bật gps");
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //kiem tra bat gps chua
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toasts("chưa bật gps");
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(MyFunction.myLocation==null)
                    Toasts("Lấy vị trí thành công");
                MyFunction.myLocation=new LatLng(location.getLatitude(),location.getLongitude());
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

        //sau 1 giay update vi tri mot lan
        //goi den ham onLocationChanged o tren
        locationManager.requestLocationUpdates("gps", 1000, 1, locationListener);


        btnDangKy =   (Button) findViewById(R.id.btndk);
        btnDangNhap = (Button) findViewById(R.id.btndn );
        edtSDT = (EditText) findViewById(R.id.edtsdt) ;
        edtMK = (EditText) findViewById(R.id.edtmk);

        database = FirebaseDatabase.getInstance().getReference();

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten = new Intent(MainActivity.this,dangky.class);
                startActivity(inten);
            }
        });

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyFunction.myLocation==null){
                    Toasts("Đang lấy vị trí, vui lòng thử lại");
                    return;
                }
               database.child("users").child(edtSDT.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       User u = dataSnapshot.getValue(User.class);
                       if(u!=null && u.password!=null && u.password.equals(edtMK.getText().toString())){
                           Intent intent;
                           if(u.LaiXe==true){
                               intent = new Intent(MainActivity.this,LaiXeActivity.class);
                           }
                           else{
                               intent = new Intent(MainActivity.this,NguoiDungActivity.class);
                           }
                           intent.putExtra("SDT",u.SDT);
                           startActivity(intent);
                       }
                       else{
                           Toasts("Sai mật khẩu");
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });

            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Toasts("Đăng xuất thành công");
    }
}
