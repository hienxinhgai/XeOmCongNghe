package com.example.btl;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NguoiDungActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    private String SDT;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng GPS;
    private DatabaseReference database;
    private Button btnCallDriver;
    private LatLng DiemDen;
    private EditText edtDiemDen;
    private DBHelper sqlite;

    private void Toasts(String s) {
        Toast.makeText(NguoiDungActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nguoi_dung);
        GPS=MyFunction.myLocation;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NguoiDungActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }

        btnCallDriver = (Button) findViewById(R.id.btnCallDriver);
        edtDiemDen = (EditText) findViewById(R.id.edtDiemDen);
        database = FirebaseDatabase.getInstance().getReference();
        SDT = getIntent().getStringExtra("SDT");
        sqlite = new DBHelper(NguoiDungActivity.this,"database",null,1);

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
                GPS=new LatLng(location.getLatitude(),location.getLongitude());
//                Toasts(GPS.getLatitude() + " " + GPS.getLongitude());
                database.child("GPS_NguoiDung").child(SDT).setValue(GPS);
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
            ActivityCompat.requestPermissions(NguoiDungActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        //sau 1 giay update vi tri mot lan
        //goi den ham onLocationChanged o tren
        locationManager.requestLocationUpdates("gps", 1000, 1, locationListener);

        //click nut dat xe
        btnCallDriver.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(btnCallDriver.getText().toString().equals("HỦY CHUYẾN")){
                database.child("yeuCauDatXe").child(SDT).removeEventListener(childListener);
                database.child("yeuCauDatXe").child(SDT).removeValue();
                btnCallDriver.setText("ĐẶT XE");
                Toasts("Hủy chuyến thành công");
                return;
            }

            if(DiemDen==null){
                Toasts("click vào bản đồ đề chọn điểm cần đến");
                return;
            }


            final DatXe datxe = new DatXe();
            datxe.SDT = SDT;
            datxe.khoangCach = MyFunction.khoagCach(GPS,DiemDen);
            datxe.chiPhi = MyFunction.chiPhi(GPS,DiemDen);
            datxe.lat = GPS.latitude;
            datxe.lng = GPS.longitude;
            datxe.viTriDich = edtDiemDen.getText().toString();

            //class CustonDialog
            CustomDialog dialog = new CustomDialog(NguoiDungActivity.this, datxe, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datxe.ghiChu = CustomDialog.edtGhiChu.getText().toString();
                    database.child("yeuCauDatXe").child(SDT).setValue(datxe);
                    Toasts("Đã đặt xe, dang tìm tài xế");
                    database.child("yeuCauDatXe").child(SDT).addChildEventListener(childListener);
                    btnCallDriver.setText("HỦY CHUYẾN");
                }
            });
            dialog.show();
//            String message="Điểm đến: " + edtDiemDen.getText().toString()
//                    +"\nKhoảng cách: " + datxe.khoangCach + " km"
//                    +"\nGiá cả: 10 000đ/1km đầu"
//                    + "\nChi phí: " + datxe.chiPhi + " đồng";
//            new AlertDialog.Builder(NguoiDungActivity.this)
//                    .setTitle("Thông tin đặt xe")
//                    .setMessage(message)
//                    .setPositiveButton("Đặt", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            database.child("yeuCauDatXe").child(SDT).setValue(datxe);
//                            Toasts("Đã đặt xe, dang tìm tài xế");
//                            database.child("yeuCauDatXe").child(SDT).addChildEventListener(childListener);
//                            btnCallDriver.setText("HỦY CHUYẾN");
//                        }
//                    })
//                    .setNegativeButton("Hủy",null)
//                    .show();
             }
    });

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    //nhan thong bao dat xe thanh cong
    ChildEventListener childListener =  new ChildEventListener() {


        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Log.e("key",dataSnapshot.getKey().toString());
            Log.e("value",dataSnapshot.getValue().toString());
            if(!dataSnapshot.getValue().toString().equals(SDT))
                return;
            btnCallDriver.setText("ĐẶT XE");

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(500); // for 500 ms
            }

            new AlertDialog.Builder(NguoiDungActivity.this)
                    .setTitle("Đặt xe thành công")
                    .setMessage("Vui lòng chờ trong giây lát\nTài xế sẽ liên lạc lại với bạn")
                    .setNegativeButton("OK",null)
                    .show();
            database.child("yeuCauDatXe").child(SDT).removeEventListener(this);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NguoiDungActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        mMap.setMyLocationEnabled(true);

        //di chuyen camera den vi tri hien tai
        if(GPS!=null){
            database.child("GPS_NguoiDung").child(SDT).setValue(GPS);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GPS,17));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                DiemDen=latLng;
                mMap.addMarker(new MarkerOptions().title("Điểm đến")
                .position(DiemDen));

                Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    ArrayList<String> diadiem = MyFunction.getAdress(NguoiDungActivity.this,DiemDen);
                    if(diadiem.size()>0)
                        edtDiemDen.setText(diadiem.get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Toasts("Đăng xuất thành công");
        locationManager.removeUpdates(locationListener);
        database.child("yeuCauDatXe").child(SDT).removeEventListener(childListener);
        locationManager=null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nguoi_dung,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.itemDangXuat:
                sqlite.truyVan("delete from user");
                finish();
                return true;
            case R.id.itemDoiMatKhau:
                Intent inten1 = new Intent(NguoiDungActivity.this,DoiMatKhauActivity.class);
                inten1.putExtra("SDT",SDT);
                startActivity(inten1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
