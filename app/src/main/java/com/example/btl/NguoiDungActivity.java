package com.example.btl;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
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

public class NguoiDungActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    private String SDT;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng GPS;
    private DatabaseReference database;
    private Button btnCallDriver;
    private LatLng DiemDen;

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
        database = FirebaseDatabase.getInstance().getReference();
        SDT = getIntent().getStringExtra("SDT");

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
                database.child("GPS_NguoiDung").child(SDT).setValue(new LatLng(GPS.latitude,GPS.longitude));
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
            if(DiemDen==null){
                Toasts("click vào bản đồ đề chọn điểm cần đến");
                return;
            }
            if(GPS==null){
                Toasts("Chưa lấy được vị trí, xin vui lòng thử lại");
                return;
            }

            final DatXe datxe = new DatXe();
            datxe.SDT = SDT;
            datxe.khoangCach = MyFunction.khoagCach(GPS,DiemDen);
            datxe.chiPhi = MyFunction.chiPhi(GPS,DiemDen);
            datxe.lat = GPS.latitude;
            datxe.lng = GPS.longitude;
            datxe.check=true;

            String message="Khoảng cách: " + datxe.khoangCach + " km"
                    +"\nGiá cả: 10 000đ/1km đầu"
                    + "\nChi phí: " + datxe.chiPhi + " đồng";
            new AlertDialog.Builder(NguoiDungActivity.this)
                    .setTitle("Thông tin đặt xe")
                    .setMessage(message)
                    .setPositiveButton("Đặt", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.child("yeuCauDatXe").child(SDT).setValue(datxe);
                            Toasts("Đã đặt xe, dang tìm tài xế");
                            database.child("yeuCauDatXe").child(datxe.SDT).child("check").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue().toString().equals("false"))
                                            Toasts("Đặt xe thành công");
                                    }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    })
                    .setNegativeButton("Hủy",null)
                    .show();


        }
    });

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

}


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NguoiDungActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        mMap.setMyLocationEnabled(true);
        LatLng home = new LatLng(21.231693, 105.791249);
        googleMap.addMarker(new MarkerOptions()
                .position(home)
                .title("Nhà tín"));

//        LatLng locationcurent = new LatLng(GPS.getLatitude(),GPS.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home,17));

        //di chuyen camera den vi tri hien tai
        if(GPS!=null){
            database.child("GPS_NguoiDung").child(SDT).setValue(new LatLng(GPS.latitude,GPS.longitude));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPS.latitude,GPS.longitude),17));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                DiemDen=latLng;
                mMap.addMarker(new MarkerOptions().title("Điểm đến")
                .position(DiemDen));
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //neu activity dong no goi den ham destroy
        // la sao
        //mở activity nó chạy hàm oncreat, đóng activity nó chạy hàm ondestroy
        //tat update vi tri
        Toasts("Đăng xuất thành công");
        locationManager.removeUpdates(locationListener);
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
                //ham finish de dong activity
                // the h xuống đây goi hàm kia la dk á
                // nó gọi tự động
                //muốn đóng mình chỉ cần finish là được rồi
                // k đúng nó phải ra cái khac chư
                //vi đóng activityDoiMK c khong finish mà gọi intent
                //nên khi đóng nó lại quay lại
                finish();
                return true;
                //hieu roi
                //switch case khong break no chay tiep cai duoi ok
            case R.id.itemDoiMatKhau:
                Intent inten1 = new Intent(NguoiDungActivity.this,DoiMatKhauActivity.class);
                inten1.putExtra("SDT",SDT); // roi sao nua
                //putExtra de gui du lieu qua activity khac
                startActivity(inten1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
