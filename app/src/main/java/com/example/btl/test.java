package com.example.btl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class test extends AppCompatActivity {
    Button btnSent;
    EditText edtPhonenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btnSent = (Button)findViewById(R.id.btnsent);
        edtPhonenumber = (EditText)findViewById(R.id.phonenumber);

        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PhoneAuthProvider.ForceResendingToken mResendToken;
                PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        Toast.makeText(test.this,"onVerificationCompleted",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(test.this,"loi khi gui",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        Toast.makeText(test.this,"onCodeSent",Toast.LENGTH_LONG).show();
                    }
                };

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+840" + edtPhonenumber.getText().toString().trim(),        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        test.this,               // Activity (for callback binding)
                        mCallbacks
                );


//                Toast.makeText(test.this,s,Toast.LENGTH_LONG).show();
            }



        });

    }
}
