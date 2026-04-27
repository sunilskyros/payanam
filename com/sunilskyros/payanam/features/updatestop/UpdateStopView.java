package com.sunilskyros.payanam.features.updatestop;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class UpdateStopView {
    private final UpdateStopModel updateStopModel;
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private final Scanner scanner;


    public UpdateStopView(){
        this.updateStopModel=new UpdateStopModel(this);
        this.scanner= ConsoleInput.getScanner();
    }

    public void init(Bus bus) {
        updateCurrentStop(bus);
    }
    void updateCurrentStop(Bus bus){
        System.out.println("Update Current stop for the bus ");
        System.out.println("---------------------------------");
        List<Stop> stop= bus.getStops();
        Stop preStop=null;
        for(Stop current:stop){
            if(current.getCurrentStop()==null) {
                System.out.println("Current stop : " + current.getStopName());
                System.out.println("Do you want to update the stop now ? [Y/N]");
                String choice = scanner.next();
                if (choice.trim().equalsIgnoreCase("Y")) {
                    current.setCurrentStop(true);
                    current.setUpdatedTime(LocalTime.parse(LocalTime.now().format(FORMATTER)));
                }
                else {
                    System.out.println("Exited from the session");
                    break;
                }
            }
            if(preStop!=null) preStop.setCurrentStop(false);
            preStop=current;
        }
//        System.out.println("Updated last stop");
    }
}
