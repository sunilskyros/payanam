package com.sunilskyros.payanam;

import com.sunilskyros.payanam.features.signup.SignUpView;

public class Payanam {

    public static final int VERSION_NO = 1;
    public static final String APP_VERSION = "0.0.1";
    public static final String APP_NAME = "Payanam";

    public static void main(String[] args) {
        System.out.println("\n\tWelcome to " + APP_NAME + " - Version: " + APP_VERSION);
        System.out.println("--------------------------------------------");
        new SignUpView().init();
    }
}
