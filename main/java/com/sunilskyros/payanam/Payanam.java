package com.sunilskyros.payanam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Payanam {

    public static final int VERSION_NUMBER = 2;
    public static final String APP_VERSION = "2.0.1";
    public static final String APP_NAME = "Payanam";

    /**
     * The main entry point for the Payanam Bus Tracking application.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Payanam.class, args);
    }
}
