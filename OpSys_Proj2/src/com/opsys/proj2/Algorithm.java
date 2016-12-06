package com.opsys.proj2;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Algorithm {
	
	int time;
	protected final int framesPerLine = 32;
	protected final int frames = 256;
	ArrayList<Partition> memory;
	int freeMemory;
	
	
	//main simulation method
    public abstract void simulate(Process.Blueprint[] blueprints);
    
    public abstract String getName();

    // Function to print the memory to the screen
    public void printMemory( ArrayList<Partition> memory ) {
    	    	
    	// Sort the partitions
    	Collections.sort(memory);
    	
    	// Print out the top row
    	for(int j = 0; j < framesPerLine; ++j)
    		System.out.print("=");
    	System.out.println();
    	
    	// Print out the data in memory
    	int n = 0;
    	for ( Partition p : memory ) {
    		for ( int i = 0; i < p.size; ++i ) {
    			System.out.print(p.partitionId);
    			++n;
    			
    			if ( n >= framesPerLine ) {
    				System.out.println();
    				n = 0;
    			}
    		}   		
    	}
    	
    	// Print out the bottom row
    	for(int j = 0; j < framesPerLine; ++j)
    		System.out.print("=");
    	System.out.println();
    }

    
    
    
    /**
     * Generic print statement for an event
     * @param time
     * @param message
     */
    public void printMessage(int time, String message) {
        System.out.printf("time %dms: %s\n", time, message );
    }

    /**
     * Print statement for the start of the simulation
     * @param time
     */
    public void printSimulationStart( int time ) {
        printMessage(time, String.format("Simulator started (%s)", getName()));
    }
    
    /**
     * Print statement for the start of the simulation
     * @param time
     */
    public void printSimulationEnd( int time ) {
        printMessage(time, String.format("Simulator ended (%s)", getName()));
    }
    
    /**
     * Print statement for a process arriving
     * @param time
     * @param process id 
     * @param number of frames for process
     */
    public void printProcessArrived( int time, String id, int numFrames ) {
        printMessage(time, String.format("Process %s arrived (requires %d frames)", id, numFrames));
    }
    
    /**
     * Print statement for a process being placed
     * @param time
     * @param process id
     */
    public void printProcessPlaced( int time, String id ) {
        printMessage(time, String.format("Placed process %s:", id));
    }

    /**
     * Print statement for a process being removed
     * @param time
     * @param process id
     */
    public void printProcessRemoved( int time, String id ) {
        printMessage(time, String.format("Process %s removed:", id));
    }
    
    /**
     * Print statement for a process being skipped
     * @param time
     * @param process id
     */
    public void printProcessSkipped( int time, String id ) {
        printMessage(time, String.format("Cannot place process %s -- skipped!", id));
    }
    
}
