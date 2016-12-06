package com.opsys.proj2;

import java.util.ArrayList;

public class Process {

    final Blueprint blueprint;

    public Process(Blueprint processBlueprint) {
        blueprint = processBlueprint;
    }


    public static class Blueprint {
        // procN pN_mem pN_arr_time_1/pN_run_time_1 ... p1_arr_time_z/p1_run_time_z
        final String procId;
        final int memFrames;
        ArrayList<Time> processTimes;

        public Blueprint(String procId, int memFrames, ArrayList<Time> processTimes) {
            this.procId = procId;
            this.memFrames = memFrames;
            this.processTimes = processTimes;
        }
    }
    
    public static class Time {
    	final int arrival;
    	final int run;
    	
    	public Time(int arr, int run) {
    		this.arrival = arr;
    		this.run = run;
    	}
    }
}
