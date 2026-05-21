package com.sunilskyros.payanam.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class InputAndValidation {
    private static final Scanner scanner=new Scanner(System.in);
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 50;
    public static Map<String, String> options = new HashMap<>();

    static {
        options.put("admin_options",
                "Admin Menu\n---------------\n1.Bus List\n2.Add Bus\n3.Add Stops\n4.Add Ticket Collector\n5.Delete Bus\n6.Delete Stops\n7.Delete Passenger / Ticket collector \n8.Sign Out\n");
        options.put("passenger_options",
                "Passenger Menu\n---------------------\n1.Search Bus by Number\n2.Search Bus by Stop\n3.List all buses\n4.Book ticket\n5.View Tickets\n6.Profile\n7.Sign Out\n");
        options.put("ticketCollector_options",
                "Ticket Collector Menu\n------------------------\n1.Bus List\n2.Select Bus\n3.Validate Ticket\n4.Sign Out\n");

    }

    private InputAndValidation() {}

    public static String getStringInput(String s) {
        System.out.print("\n"+s);
        return scanner.nextLine().trim();
    }

    public static String getPassWord(String s) {
        String passWord = getStringInput(s).trim();
        if (!passWord.isEmpty()) {
            return passWord;
        }
        return null;
    }

    public static String validatePassWord(String passWord) {
        if (!PASSWORD_PATTERN.matcher(passWord).matches()) {
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
        if (!MOBILE_PATTERN.matcher(phoneNumber.trim()).matches()) {
            return "Enter a valid 10 digit mobile number";
        }
        return null;
    }
}
