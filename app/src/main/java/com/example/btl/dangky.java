package com.example.btl;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;


public class dangky extends AppCompatActivity {
    Button btnDangKy, btnSentCode;
    EditText edtHoTen, edtSDT,edtMK,edtNLMK,edtAuthCode;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String authCode;
    PhoneAuthProvider.ForceResendingToken token ;
    FirebaseAuth mAuth;

    DatabaseReference database;

    private void Toasts(String s){
        Toast.makeText(dangky.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangky);
        btnDangKy = (Button) findViewById(R.id.button);
        edtHoTen = (EditText)findViewById(R.id.edtHoTen);
        edtSDT = (EditText)findViewById(R.id.edtSDT);
        edtMK = (EditText)findViewById(R.id.edtMK);
        edtNLMK = (EditText) findViewById(R.id.edtNLMK);
        btnSentCode = (Button)findViewById(R.id.btnSentCode) ;
        edtAuthCode = (EditText)findViewById(R.id.edtAuthCode);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(dangky.this,"Có lỗi khi gửi mã xác nhận",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(dangky.this,"Đã gửi mã xác nhận",Toast.LENGTH_LONG).show();
                authCode =s;
                token = forceResendingToken;
            }
        };

        btnSentCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hàm dùng để gửi mã xác nhận vào điện thoại
                //mCallbacks dùng để xác nhận xem mã đã được gửi đi chưa, mã nhập vào đã đúng chưa
                 PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+840" + edtSDT.getText().toString().trim(),        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        dangky.this,               // Activity (for callback binding)
                        mCallbacks
                );
            }
        });


        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(  edtMK.getText().toString().equals(edtNLMK.getText().toString())){
                    if(authCode==null){
                        Toasts("Chưa gửi mã xác nhận");
                        return;
                    }
//                    Toast.makeText(dangky.this,"code:" + edtAuthCode.getText().toString()+"   "+authCode,Toast.LENGTH_LONG).show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(authCode, edtAuthCode.getText().toString().trim());
                    signInWithPhoneAuthCredential(credential);

                }
                else {
                    Toasts("Mật khẩu không khớp nhau");
//                    Toast.makeText(dangky.this,"Mật khẩu không khớp nhau",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = task.getResult().getUser();
                            User u = new User();
                            u.Hoten = edtHoTen.getText().toString();
                            u.SDT = edtSDT.getText().toString();
                            u.password = edtMK.getText().toString();
                            DangKyTaiKhoan(u);
//                            Toast.makeText(dangky.this,"Đăng ký thành công",Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(dangky.this,"Mã xác thực không đúng",Toast.LENGTH_LONG).show();
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void DangKyTaiKhoan(User user){
        database.child("users").child(user.SDT).setValue(user);
        Toasts("Đăng ký thành công");
        super.onBackPressed(); // quay lại activity trước
    }

}
