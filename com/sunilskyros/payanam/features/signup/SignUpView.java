package com.sunilskyros.payanam.features.signup;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.features.signin.SignInView;

import java.util.Scanner;

public class SignUpView {
    private SignUpModel signupmodel;

    public SignUpView(){
        this.signupmodel = new SignUpModel(this);
    }
    public void init(){
        Scanner scan=new Scanner(System.in);
        System.out.print("Do you want to resgiter ? [y/n] : ");
        String option=scan.next();
        if(option.equalsIgnoreCase("Y")){

        }

    }
    public void userCreated(String name) {
        System.out.println("Account Created for : "+name);
    }
}