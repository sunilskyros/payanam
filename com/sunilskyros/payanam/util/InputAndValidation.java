package com.sunilskyros.payanam.util;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleInput {
    private static final Scanner scanner=new Scanner(System.in);
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d {9}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d). {8, }$");

    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 50;

    private ConsoleInput() {}
    public static Scanner getScanner() {
        return scanner;
    }

    public static String getStringInput(String s) {
        System.out.println("\n"+s);
        return scanner.nextLine().trim();
    }

    public static String getPassWord(String s) {
        String passWord=getStringInput(s);
        if(!PASSWORD_PATTERN.matcher(passWord).matches()){
            return "Password must be at least 8 characters and contain letters and numbers";
        }
        return null;
    }

    public static String validateName(String name) {
        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            return "Name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters";
        }
        return null;
    }
    public  static String validatePhoneNumber(String phoneNumber){

    }
}
