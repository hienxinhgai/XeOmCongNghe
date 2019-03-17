package com.example.btl;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class YeuCauDatXeActivity extends AppCompatActivity {
    private ListView lvYeuCauDatXe;
    private DatabaseReference database;
    private List<String> list;
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
                list = new ArrayList<>();
                for(DataSnapshot dts : dataSnapshot.getChildren()){
                    if(dts.getValue().toString().equals("1"))
                        list.add(dts.getKey().toString());
                }

                ArrayAdapter<String> arrayAdapter
                        = new ArrayAdapter<String>(YeuCauDatXeActivity.this, android.R.layout.simple_list_item_1 , list);
                lvYeuCauDatXe.setAdapter(arrayAdapter);
//                ArrayAdapter<String> arrayAdapter
//                        = new ArrayAdapter<String>(YeuCauDatXeActivity.this, android.R.layout.simple_list_item_1 , td);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lvYeuCauDatXe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toasts("đặt xe từ " + list.get(position));
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK,intent);
                intent.putExtra("SDTKhach",list.get(position));
                database.child("yeuCauDatXe").child(list.get(position)).setValue(0);
//                startActivity(intent);
                finish();
            }
        });
    }
}
