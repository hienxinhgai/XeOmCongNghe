package com.example.btl;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DoiMatKhauActivity extends AppCompatActivity {
    Button btnDoiMatKhau;
    EditText edtMatKhau,edtMatKhauMoi,edtNhapLaiMatKhau;
    private DatabaseReference database;
    private String SDT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau2);

        SDT = getIntent().getStringExtra("SDT");
        btnDoiMatKhau = (Button) findViewById(R.id.btnDoiMatKhau);
        edtMatKhau = (EditText) findViewById(R.id.edtMatKhau);
        edtMatKhauMoi = (EditText) findViewById(R.id.edtMatKhauMoi);
        edtNhapLaiMatKhau = (EditText) findViewById(R.id.edtNhapLaiMatKhau);
        database = FirebaseDatabase.getInstance().getReference();
        btnDoiMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child("users").child(SDT).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//bao lay edtitext la phai .getText.tostring mà ok rồi đấy
                        User u = dataSnapshot.getValue(User.class);
                        if(u.password.equals(edtMatKhau.getText().toString()) && edtMatKhauMoi.getText().toString().equals(edtNhapLaiMatKhau.getText().toString())) {
                            // cái ben ham nguoi dùng no k chuyen ve main

                                database.child("users").child(u.SDT).child("password").setValue(edtMatKhauMoi.getText().toString());
                                Toast.makeText(DoiMatKhauActivity.this,"Đổi mật khẩu thành công",Toast.LENGTH_LONG).show();
                                finish();

                        }

                        else {
                            Toast.makeText(DoiMatKhauActivity.this,"mat khau k dung",Toast.LENGTH_LONG).show();

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
