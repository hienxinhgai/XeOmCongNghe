package com.example.btl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
 Button dangky, dangnhap;
 EditText SDT,mk;
 public static  List<User> list = new ArrayList<User>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dangky =   (Button) findViewById(R.id.btndk);
        dangnhap = (Button) findViewById(R.id.btndn );
        SDT = (EditText) findViewById(R.id.edtsdt) ;
        mk = (EditText) findViewById(R.id.edtmk);
        dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten = new Intent(MainActivity.this,dangky.class);
                startActivity(inten);
            }
        });
        dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tim(SDT.getText().toString(),mk.getText().toString())){
                    Toast.makeText(MainActivity.this,"Đăng nhập thành công",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this,"Tên đăng nhập hoặc mật khẩu không đúng",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    boolean tim(String sdt,String mk){
        for(int i=0; i<list.size(); i++){
            if(sdt.equals(list.get(i).SĐT)  && mk.equals(list.get(i).password))
                return true;
        }
        return  false;
    }
}
