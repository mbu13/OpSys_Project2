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
    
    // Function to get the name of the algorithm
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

    // Function to defrag the memory
    public int defrag() {
    	
    	// Make sure the memory is sorted
    	Collections.sort(memory);
    	
    	// Initialize the time delay to zero
    	int timeDelay = 0;
    	
    	// Remove all of the empty partitions from the memory
    	for ( int i = memory.size() - 1; i >= 0; --i ) {
    		if ( memory.get(i).partitionId.equals(".") ) {
    			memory.remove(i);
    		}
    	}
    	
    	// Make sure the memory is sorted
    	Collections.sort(memory); 
    	
    	// Shift the starting locations of all of the partitions
    	int currentStartLoc = 0;
    	for ( Partition p : memory ) {
    		p.startLocation = currentStartLoc;
    		currentStartLoc += p.size;    	
    		timeDelay += p.size;
    	}
    	
    	// Create the new empty partition
    	Partition p = new Partition( ".", currentStartLoc, freeMemory );
    	memory.add(p);
    	
    	// Sort the memory
    	Collections.sort( memory );
    
    	// Return the time delay cause by defragmentation
    	return timeDelay;
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
     * Print statement for the end of the simulation
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
    
    /**
     * Print statement for a process starting defrag.
     * @param time
     * @param process id
     */
    public void printProcessStartDefrag( int time, String id ) {
        printMessage(time, String.format("Cannot place process %s -- starting defragmentation", id));
    }
    
    /**
     * Print statement for a process ending defrag.
     * @param time
     * @param time delay from defrag
     */
    public void printProcessEndDefrag( int time, int timeDelay ) {
    	String m = "";
    	for ( int i = 0; i < memory.size() - 2; ++i ) {
    		if ( memory.get(i).partitionId.equals(".") ){ continue; }
    		m += memory.get(i).partitionId + ", ";
    	}
    	m += memory.get(memory.size()-2).partitionId;
    	
        printMessage(time, String.format("Defragmentation complete (moved %d frames: %s)", timeDelay, m));
    }
}
