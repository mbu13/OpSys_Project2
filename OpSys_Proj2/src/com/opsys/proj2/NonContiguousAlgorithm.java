package com.opsys.proj2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import com.opsys.proj2.Process.Blueprint;
import com.opsys.proj2.Process.Time;


public class NonContiguousAlgorithm extends Algorithm{
	
	PriorityQueue<Event> eventQueue;
	ArrayList<RunningProcess> processes;
	
	public NonContiguousAlgorithm() {
		// Initialize the time
		time = 0;
		
		// Initialize memory
		memory = new ArrayList<>();
		freeMemory = frames; 
		for ( int i = 0; i < frames; ++i ) {
			memory.add( new Partition( ".", i, 1 ) );
		}
	}
	
	@Override
	public void simulate(Blueprint[] blueprints) {
	
	    // Create the ready queue for events as a priority queue and set the priority
	    eventQueue = new PriorityQueue<Event>( blueprints.length,
	            new Comparator<Event>(){
	                public int compare(Event a, Event b){
	                    if ( a.time < b.time ) { return -1; }
	                    else if ( b.time < a.time ) { return 1; }
	                    else {
	                    	if ( a.type == EventType.REMOVE && b.type == EventType.ARRIVED ) {
	                    		return -1;
	                    	} else if ( a.type == EventType.ARRIVED && b.type == EventType.REMOVE ) {
	                    		return 1;
	                    	} else {                        	
	                    		return a.processId.compareTo(b.processId);
	                    	}
	                    }
	                }
	            });
	    
	    // Initialize the array list of processes
	    processes = new ArrayList<RunningProcess>();
	    
	    // Convert all initial processes to events
	    for ( Process.Blueprint blueprint : blueprints ) {
	    	for ( Time t : blueprint.processTimes ) {
	    
	    		// Create the events for the process
	    		Event eArrive = new Event( blueprint.procId, t.arrival, EventType.ARRIVED, blueprint.memFrames );
	    		Event eRemove = new Event( blueprint.procId, t.arrival+t.run, EventType.REMOVE, blueprint.memFrames );
	    		
	    		// Add the events to the queue
	    		addEvent( eventQueue, eArrive );
	    		addEvent( eventQueue, eRemove );
	    	}
	    }
		
	    // Initialize time and display message
	    time = 0;
	    printSimulationStart( time );
	    
	    // Loop through all events
	    while(!eventQueue.isEmpty()) {
	    	
	    	// Get the next event
	        Event e = eventQueue.poll();
	        
	        // Set the current time
	        time = e.time;
	        
	        // Take care of event
	        if ( e.type == EventType.ARRIVED ) {
	        	arrivedEvent( e );
	        } else if ( e.type == EventType.REMOVE ) {
	        	removeEvent( e );
	        }
	        
	    }
	    
	    // Print end of simulation
	    printSimulationEnd( time );
		
	}
	
	private void arrivedEvent( Event e ) {
		
		// Print Message
		printProcessArrived( time, e.processId, e.space );
		
		// Make sure there is enough free space available
		if ( freeMemory < e.space ) {
			printProcessSkipped( time, e.processId );
			printMemory( memory );
			return;
		}
		
		// Sort the partitions
		Collections.sort(memory);
		
		// Add data to memory, keeping track of the locations
		RunningProcess newProcess = new RunningProcess( e.processId );
		int count = 0;
		for ( int i = 0; i < memory.size(); ++i ) {
			if ( memory.get(i).partitionId.equals(".") ) {
				memory.get(i).partitionId = e.processId;
				count++;
				newProcess.frames.add(i);
				if ( count >= e.space ) {
					break;
				}
			}
		}
		
		// Adjust the amount of free memory
		freeMemory -= e.space;
		
		// Add the running process to the list
		processes.add( newProcess );
		
		// Print the message
		printProcessPlaced( time, e.processId );
		printMemory( memory );
	
	}
	
	private void removeEvent( Event e ) {
		
		// If ID is not in memory, return
		int loc = -1;
		for ( int i = 0; i < processes.size(); ++i ) {
			if ( processes.get(i).processId.equals( e.processId ) ) {
				loc = i;
				break;
			}
		}
		
		if ( loc == -1 ) { return; }
		
		// Sort the partitions
		Collections.sort( memory );
		
		// Go through all of locations in memory and remove data
		for ( Integer i : processes.get(loc).frames ) {
			memory.get(i).partitionId = ".";
		}
		
		// Add free memory
		freeMemory += e.space;
		
		// Remove the running process
		processes.remove(loc);
		
		// Sort the partitions
		Collections.sort( memory );
			
		// Print message
		printProcessRemoved( time, e.processId );
		printMemory( memory );
		
	}
		
	private class RunningProcess {
		// Create member variables
		public String processId;
		public ArrayList<Integer> frames;
		
		// Constructor
		private RunningProcess( String pid ) {
			processId = pid;
			frames = new ArrayList<Integer>();
		}
	}
	
	private class Event {		
		// Create member variables
	    public final EventType type;
	    public final String processId;
	    public int time;
	    public final int space;
	    
	    //Create the constructor
	    private Event( String id, int time, EventType type, int s ) {
	        this.type = type;
	        this.processId = id;
	        this.time = time;
	        this.space = s;
	    }
	}
	
	private void addEvent( PriorityQueue<Event> pq, Event e ) {
		addToQueueNoDup( pq, e );
	}
	
	private <S> void addToQueueNoDup(Queue<S> queue, S value) {
	    if(!queue.contains(value)) {
	        queue.add(value);
	    }
	}
	
	private enum EventType {
	    ARRIVED, REMOVE
	}
	
	@Override
	public String getName() {
		return "Non-contiguous";
	}

}