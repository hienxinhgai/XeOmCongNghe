package com.example.btl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class dangky extends AppCompatActivity {
    Button dangky ;
    EditText hoten, sdt,mk,nlmk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangky);
        dangky = (Button) findViewById(R.id.button);
        hoten = (EditText)findViewById(R.id.edtHoTen);
        sdt = (EditText)findViewById(R.id.edtSDT);
        mk = (EditText)findViewById(R.id.edtMK);
        nlmk = (EditText) findViewById(R.id.edtNLMK);
        dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(  mk.getText().toString().equals(nlmk.getText().toString())){
                    User u= new User();
                    u.Hoten = hoten.getText().toString();
                    u.SĐT = sdt.getText().toString();
                    u.password = mk.getText().toString();
                    MainActivity.list.add(u);
                    Toast.makeText(dangky.this,"Đăng ký thành công",Toast.LENGTH_LONG).show();
                   Intent intent = new Intent(dangky.this,MainActivity.class);
                   startActivity(intent);

                }
                else {
                    Toast.makeText(dangky.this,"Mật khẩu không hớp nhau",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
