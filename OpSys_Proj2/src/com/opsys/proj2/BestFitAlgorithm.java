package com.opsys.proj2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import com.opsys.proj2.Process.Blueprint;
import com.opsys.proj2.Process.Time;

public class BestFitAlgorithm extends Algorithm {
		
	public BestFitAlgorithm() {
		// Initialize the time
		time = 0;
		
		// Initialize memory
		memory = new ArrayList<>();
		freeMemory = frames; 
		memory.add( new Partition( ".", 0, frames ) );
	}
	
	@Override
	public void simulate(Blueprint[] blueprints) {

        // Create the ready queue as a priority queue and set the priority
        PriorityQueue<Event> eventQueue = new PriorityQueue<Event>( blueprints.length,
                new Comparator<Event>(){
                    public int compare(Event a, Event b){
                        if ( a.time < b.time ) { return -1; }
                        else if ( b.time < a.time ) { return 1; }
                        else {
                        	return a.processId.compareTo(b.processId);
                        }
                    }
                });
        
        
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
		
	}
	
	private void arrivedEvent( Event e ) {
		
		// Print Message
		printProcessArrived( time, e.processId, e.space );
		
		// Make sure there is enough free space available
		if ( freeMemory < e.space ) {
			printProcessSkipped( time, e.processId );
			return;
		}
		
		// Sort the partitions
		Collections.sort(memory);
		
		// Look for the best fit partition
		int loc = 0;
		int smallest = frames + 1;
		int found = 0;
		for ( int i = 0; i < memory.size(); ++i ) {
			// Continue if the partition is full
			if ( !memory.get(i).partitionId.equals(".") ) {
				continue;
			}
			
			// See if the data will fit
			if ( memory.get(i).size < e.space ) {
				continue;
			}
			
			found = 1;
			
			if ( memory.get(i).size < smallest ) {
				smallest = memory.get(i).size;
				loc = i;
			}
			
		}
		
		if ( found == 0 ) {
			//DEFRAG 
			System.out.println("DEFRAG");
			return;
		}
		
		if ( memory.get(loc).size == e.space ) {
			// Set the partition ID
			memory.get(loc).partitionId = e.processId;
		} else {
			// Create the two new partitions
			Partition p1 = new Partition( e.processId, memory.get(loc).startLocation, e.space );
			Partition p2 = new Partition( ".", memory.get(loc).startLocation + e.space, memory.get(loc).size-e.space );
			
			// Remove the old partition
			memory.remove(loc);
			
			// Add the two new partitions
			memory.add(p1);
			memory.add(p2);
			
			// Sort the partitions
			Collections.sort(memory);
		}
		
		freeMemory -= e.space;
		
		// Print the message
		printProcessPlaced( time, e.processId );
		printMemory( memory );
		
		
		
	}
	
	private void removeEvent( Event e ) {
		
		// If ID is not in memory, return
		int found = 0;
		for ( Partition p : memory ) {
			if ( p.partitionId.equals(e.processId) ) {
				found = 1;
			}
		}
		
		if ( found == 0 ) { return; }
		
		// Sort the partitions
		Collections.sort( memory );
		
		// Find the location of the desired partition
		int loc = 0;
		for ( int i = 0; i < memory.size(); ++i ) {
			if ( memory.get(i).partitionId.equals(e.processId) ) {
				loc = i;
				break;
			}
		}
		
		// Set the partition id to empty
		memory.get(loc).partitionId = ".";
		
		// Add free memory
		freeMemory += memory.get(loc).size;
		
		// See if the partitions can be merged
		if ( loc - 1 >= 0 && loc + 1 < memory.size() && memory.get(loc-1).equals(".") && memory.get(loc+1).equals(".") ) {
			memory.get(loc-1).size += memory.get(loc).size + memory.get(loc+1).size;
			memory.remove(loc+1);
			memory.remove(loc);
		} else if ( loc - 1 >= 0 && memory.get(loc-1).equals(".") ) {
			memory.get(loc-1).size += memory.get(loc).size;
			memory.remove(loc);
		} else if ( loc + 1 < memory.size() && memory.get(loc+1).equals(".") ) {
			memory.get(loc).size += memory.get(loc+1).size;
			memory.remove(loc+1);
		}
		
		// Sort the partitions
		Collections.sort( memory );
			
		// Print message
		printProcessRemoved( time, e.processId );
		printMemory( memory );
	}

	private class Event {		
		// Create member variables
	    public final EventType type;
	    public final String processId;
	    public final int time;
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
		return "Contiguous -- Best-Fit";
	}
	
}