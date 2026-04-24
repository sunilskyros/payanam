package com.sunilskyros.payanam.features.searchbus;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.util.Scanner;

public class SearchBusView {
    private final SearchBusModel searchBusModel;
    private final Scanner scanner;


    SearchBusView(){
        this.searchBusModel=new SearchBusModel(this);
        this.scanner= ConsoleInput.getScanner();
    }

}
