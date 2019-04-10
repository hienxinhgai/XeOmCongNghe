package com.example.btl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LaiXeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference database;
    private Button btnGetLocationCustom;
    private String SDTKhach;
    private String SDT;
    private DBHelper sqlite;
    private ArrayList markerPoints;
    private LatLng GPSCustom;

    private void Toasts(String s) {
        Toast.makeText(LaiXeActivity.this, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lai_xe);

        markerPoints = new ArrayList();
        btnGetLocationCustom = (Button) findViewById(R.id.btnGetLocationCustom);
        sqlite = new DBHelper(LaiXeActivity.this,"database",null,1);

        database = FirebaseDatabase.getInstance().getReference();
        SDT = getIntent().getStringExtra("SDT");

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

       database.child("yeuCauDatXe").addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               Toasts("Vừa có người đặt xe");
               Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
               if (vibrator.hasVibrator()) {
                   vibrator.vibrate(500); // for 500 ms
               }

           }

           @Override
           public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyFunction.myLocation,17));
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
                        GPSCustom = new LatLng(dataSnapshot.child("latitude").getValue(double.class),dataSnapshot.child("longitude").getValue(double.class));
                        mMap.addMarker(new MarkerOptions().position(GPSCustom).title("khách"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GPSCustom,15));

                        // Already two locations
                        markerPoints.clear();
                        mMap.clear();

                        if(MyFunction.myLocation==null){
                            Toasts("Chưa lấy được vị trí");
                            return;
                        }
                        // Adding new item to the ArrayList
                        markerPoints.add(MyFunction.myLocation);
                        markerPoints.add(GPSCustom);
                        // Creating MarkerOptions
                        MarkerOptions options = new MarkerOptions();

                        // Setting the position of the marker
                        options.position(GPSCustom);
                        // Add new marker to the Google Map Android API V2
                        mMap.addMarker(options);

                        LatLng origin = (LatLng) markerPoints.get(0);
                        LatLng dest = (LatLng) markerPoints.get(1);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);
                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                Toasts(SDTKhach);
            }
        });
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String key = "key=AIzaSyAqv8zngFpnl_wiSuT5gmUVAQ6PrVICzIQ";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("URL",url);
        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            if(result.size()==0){
                Toasts("result line 340 size= 0");
                return;
            }

            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j <path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.BLUE);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lai_xe,menu);
        return super.onCreateOptionsMenu(menu);
    }
//NullPointerException là có 1 biến bị null nhưng vẫn dùng
    //nó bảo trong cái chide null
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemYeuCauDatXe:
                Intent intent = new Intent(LaiXeActivity.this,YeuCauDatXeActivity.class);
                startActivityForResult(intent,1);
                return true;
            case R.id.itemLogout:
                sqlite.truyVan("delete from user");
               finish();
                return true;
            case R.id.itemDoiMatKhau:
                Intent inten = new Intent(LaiXeActivity.this,DoiMatKhauActivity.class);
                inten.putExtra("SDT",SDT);
                //put null vao nay
                // ma sao lại là null nhỉ
                startActivity(inten);
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
