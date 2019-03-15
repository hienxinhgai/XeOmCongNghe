package com.example.btl;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
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

    private void Toasts(String s){
         Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
               database.child("users").child(edtSDT.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       User u = dataSnapshot.getValue(User.class);
                       if(u!=null && u.password!=null && u.password.equals(edtMK.getText().toString())){
                           Intent intent = new Intent(MainActivity.this,NguoiDungActivity.class);
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
