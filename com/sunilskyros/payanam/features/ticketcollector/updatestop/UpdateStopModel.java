package com.sunilskyros.payanam.features.updatestop;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Stop;

import java.time.LocalTime;
import java.util.List;

public class UpdateStopModel {
    private final UpdateStopView updateStopView;

    UpdateStopModel(UpdateStopView updateStopView){
        this.updateStopView=updateStopView;
    }
    void updateCurrentStop(Bus bus){
        List<Stop> stop= bus.getStops();
        Stop preStop=null;
        int i=0;
        for(;i< stop.size();i++){
            Stop current=stop.get(i);
            if(current.getCurrentStop()==null) {
                updateStopView.showMessage("Current stop : " + current.getStopName());
                String choice=updateStopView.getInput("Do you want to update the stop now ? [Y/N]");
                if (choice.trim().equalsIgnoreCase("Y")) {
                    current.setCurrentStop(true);
                    current.setUpdatedTime(LocalTime.now());
                }
                else {
                    updateStopView.showMessage("Exited from the session");
                    break;
                }
            }
            if(preStop!=null) preStop.setCurrentStop(false);
            preStop=current;
            int count=1;
            for(int j=i;j<stop.size();j++){
                Stop nonVisitedStops=stop.get(j);
                nonVisitedStops.setUpdatedTime(LocalTime.now().plusMinutes(15L * count++));
            }
        }
        if(i==stop.size()-1){
            
        }
    }
}
