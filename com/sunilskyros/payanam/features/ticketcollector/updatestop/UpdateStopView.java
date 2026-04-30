package com.sunilskyros.payanam.features.updatestop;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.util.Scanner;

public class UpdateStopView {
    private final UpdateStopModel updateStopModel;

    private final Scanner scanner;


    public UpdateStopView(){
        this.updateStopModel=new UpdateStopModel(this);
        this.scanner= ConsoleInput.getScanner();
    }

    public void init(Bus bus) {
        updateStop(bus);
    }
    void updateStop(Bus bus){
        System.out.println("\nUpdate Current stop for the bus ");
        System.out.println("---------------------------------");
        updateStopModel.updateCurrentStop(bus);
    }
    void showMessage(String message){
        System.out.println(message);
    }
    String getInput(String message){
        System.out.println(message);
        return scanner.next();
    }
}
