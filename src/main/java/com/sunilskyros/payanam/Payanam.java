package com.sunilskyros.payanam;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Payanam {

    public static final int VERSION_NUMBER = 2;
    public static final String APP_VERSION = "2.3.1";
    public static final String APP_NAME = "Payanam";

    @PostConstruct
    public void init() {
        // Set default JVM timezone to Indian Standard Time (GMT+5:30)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    /**
     * The main entry point for the Payanam Bus Tracking application.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        // Set timezone property for early initialization before Spring contexts load
        System.setProperty("user.timezone", "Asia/Kolkata");
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(Payanam.class, args);
    }
}

