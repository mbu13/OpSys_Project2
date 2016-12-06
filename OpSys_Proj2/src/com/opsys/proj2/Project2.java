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
	}
	
	public static Process.Blueprint[] parseInputFile(String filePath) throws IOException {
        ArrayList<Process.Blueprint> blueprints = new ArrayList<>();
        File f = new File(filePath);
        try(BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while((line = reader.readLine()) != null) {
                if(line.trim().startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }
                
                // Split line by spaces to get attributes
                String[] attr = line.split("\\s+");
                
                // Get arrival/runtimes
                ArrayList<Process.Time>  times = new ArrayList<>();
                for(int i = 2; i < attr.length; ++i) {
                	String[] arrivalAndRunTimes = attr[i].split("/");
                	int newArrival = Integer.parseInt(arrivalAndRunTimes[0]);
                	int newRun = Integer.parseInt(arrivalAndRunTimes[1]);
                	Process.Time newTime = new Process.Time(newArrival, newRun);
                	
                	times.add(newTime);
                }
                
                Process.Blueprint blueprint = new Process.Blueprint(attr[0], Integer.parseInt(attr[1]), times);
                blueprints.add(blueprint);
            }
        }

        return blueprints.toArray(new Process.Blueprint[blueprints.size()]);
    }

}
