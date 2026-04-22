package com.sunilskyros.payanam;

import com.sunilskyros.payanam.features.signup.SignUpView;

import java.sql.Connection;

public class Main {

    public static final int VERSION_NO = 1;
    public static final String VERSION_NAME = "0.0.1";
    public static void main(String[] args) {
        new SignUpView().init();
    }
}
