package com.opsys.proj2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Project2 {

	public static void main(String[] args) {
		
		// Handle invalid arguments
        if(args.length != 1) {
            System.err.println("ERROR: Invalid arguments");
            System.err.println("USAGE: ./a.out <input-file>");
            System.exit(1);
        }
        
        // Create the memory management algorithms
        Algorithm bf = new BestFitAlgorithm( );
        Algorithm wf = new WorstFitAlgorithm( );
        Algorithm nf = new NextFitAlgorithm( );
        Algorithm nc = new NonContiguousAlgorithm( );
        
        // Run each simulation
    	try {
    		nf.simulate( parseInputFile(args[0]) );
    		System.out.println();
			bf.simulate( parseInputFile(args[0]) );
    		System.out.println();
			wf.simulate( parseInputFile(args[0]) );
			System.out.println();
			nc.simulate( parseInputFile(args[0]) );
		} catch (IOException e) {
			e.printStackTrace();
		}      
        
	}
	
	public static Process.Blueprint[] parseInputFile(String filePath) throws IOException {
		
		// Create array list of process'
        ArrayList<Process.Blueprint> blueprints = new ArrayList<>();
        
        // Open the file
        File f = new File(filePath);
        
        // Read through the file
        try(BufferedReader reader = new BufferedReader(new FileReader(f))) {
        	
        	// Read the first line - Number of processes
            String line;
            line = reader.readLine();
            
            // Go through the all lines
            while((line = reader.readLine()) != null) {
            	
            	// Skip empty lines and lines that start with '#'
                if(line.trim().startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }
                
                // Split line by spaces to get attributes
                String[] attr = line.split("\\s+");
                
                // Create arraylist to hold arrival/runtimes
                ArrayList<Process.Time>  times = new ArrayList<>();
                for(int i = 2; i < attr.length; ++i) {
                	
                	// Split the sting on '/' and create new time obj and add to list
                	String[] arrivalAndRunTimes = attr[i].split("/");
                	int newArrival = Integer.parseInt(arrivalAndRunTimes[0]);
                	int newRun = Integer.parseInt(arrivalAndRunTimes[1]);
                	Process.Time newTime = new Process.Time(newArrival, newRun);
                	times.add(newTime);
                	
                }
                
                // Create a new process obj. and add process to the list
                Process.Blueprint blueprint = new Process.Blueprint(attr[0], Integer.parseInt(attr[1]), times);
                blueprints.add(blueprint);
            }
        }
        
        // Return the array of process blueprints
        return blueprints.toArray(new Process.Blueprint[blueprints.size()]);
    }

}
