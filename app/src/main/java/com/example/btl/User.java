package com.example.btl;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    //public String username;
    public String password;
    public String Hoten;
    public String SDT;
    public boolean LaiXe;

    public User(){

    }
    public User(String sdt,String pass,String hoten, boolean laixe){
        password=pass;
        Hoten=hoten;
        SDT=sdt;
        LaiXe = laixe;
    }
}

