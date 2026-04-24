package com.sunilskyros.payanam.util;

import java.util.Scanner;

public class ConsoleInput {
    private static final Scanner scanner=new Scanner(System.in);

    private ConsoleInput(){}
    public static Scanner getScanner(){
        return scanner;
    }
}
