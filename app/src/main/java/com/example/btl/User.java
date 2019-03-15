package com.example.btl;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    //public String username;
    public String password;
    public String Hoten;
    public String SDT;

    public User(){

    }
    public User(String sdt,String pass,String hoten){
        password=pass;
        Hoten=hoten;
        SDT=sdt;
    }
}

