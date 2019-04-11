package com.example.btl;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.database.sqlite.*;

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
    EditText edtSDT, edtMK;
    DatabaseReference database;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DBHelper SQLite;

    private void Toasts(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                if(MyFunction.myLocation==null)
//                    Toasts("Lấy vị trí thành công");
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

        //sau 0.1 giay update vi tri mot lan
        //goi den ham onLocationChanged o tren
        locationManager.requestLocationUpdates("gps", 100, 1, locationListener);

        //lay vi tri cuoi cung
        Location location = locationManager.getLastKnownLocation("gps");
        if(location!=null){
            MyFunction.myLocation = new LatLng(location.getLatitude(),location.getLongitude());
            Toasts("Lấy vị trí thành công");
        }

        SQLite = new DBHelper(MainActivity.this,"database",null,1);
//        SQLite.truyVan("drop table if exists user");
        SQLite.truyVan("CREATE TABLE IF NOT EXISTS user (SDT varchar(10), pass varchar(30), laixe varchar(4))");


        btnDangKy =   (Button) findViewById(R.id.btndk);
        btnDangNhap = (Button) findViewById(R.id.btndn );
        edtSDT = (EditText) findViewById(R.id.edtsdt) ;
        edtMK = (EditText) findViewById(R.id.edtmk);

        database = FirebaseDatabase.getInstance().getReference();
        Cursor c =  SQLite.select("Select * from user");
        if( c.moveToNext()){
                User u = new User();
                u.SDT = c.getString(0);
              //  u.password = c.getString(1);
                u.LaiXe = Boolean.parseBoolean(c.getString(2));
                if(MyFunction.myLocation==null){
                    Toasts("Đang lấy vị trí");
                }
                else if(u.LaiXe){
                    Intent intent = new Intent(MainActivity.this,LaiXeActivity.class);
                    intent.putExtra("SDT",u.SDT);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(MainActivity.this,NguoiDungActivity.class);
                    intent.putExtra("SDT",u.SDT);
                    startActivity(intent);
                }

        }

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
//                           SQLite.truyVan("Insert into user values ('"+u.SDT+"','"+u.password+"')");
                           SQLite.truyVan("delete from user;");
                           SQLite.truyVan(String.format("insert into user values('%s','%s','%s');",u.SDT,u.password,u.LaiXe));
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

}
