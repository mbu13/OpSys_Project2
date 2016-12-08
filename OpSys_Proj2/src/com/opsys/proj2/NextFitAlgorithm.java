package com.opsys.proj2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import com.opsys.proj2.Process.Blueprint;
import com.opsys.proj2.Process.Time;

public class NextFitAlgorithm extends Algorithm{
		
	PriorityQueue<Event> eventQueue;
	int startIndex;
	
	public NextFitAlgorithm() {
		// Initialize the time
		time = 0;
		
		// Initialize memory
		memory = new ArrayList<>();
		freeMemory = frames; 
		memory.add( new Partition( ".", 0, frames ) );
		startIndex = 0;
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
		
		// Find the first possible partition the memory can go in
		int loc = 0;
		for ( int i = 0; i < memory.size(); ++i ) {
			if ( startIndex >= memory.get(i).startLocation && startIndex < memory.get(i).startLocation + memory.get(i).size ) {
				loc = i;
			}
		}
		
		// See if memory can be added starting at the old spot
		if ( memory.get(loc).partitionId.equals(".") ) {
			if ( memory.get(loc).startLocation + memory.get(loc).size >= startIndex + e.space ) {
				// Create the new partitions
				Partition p1 = new Partition( e.processId, startIndex, e.space );
				Partition p2 = new Partition( ".", memory.get(loc).startLocation, startIndex - memory.get(loc).startLocation );
				Partition p3 = new Partition( ".", startIndex + e.space, memory.get(loc).startLocation+memory.get(loc).size-(startIndex+e.space) );
				
				// Remove the old partition
				memory.remove(loc);
				
				// Add the new partitions
				memory.add(p1);
				
				if ( p2.size > 0 ) {
					memory.add(p2);
				}
				if ( p3.size > 0 ) {
					memory.add(p3);			
				}
		
				// Sort the partitions
				Collections.sort(memory);
				
				startIndex = startIndex + e.space;
				freeMemory -= e.space;
				
				// Print the message
				printProcessPlaced( time, e.processId );
				printMemory( memory );
				
				return;
			}
		}
		
		// Look for the next possible partition
		int found = 0;
		for ( int i = loc; i < memory.size(); ++i ) {
			// Continue if the partition is full
			if ( !memory.get(i).partitionId.equals(".") ) {
				continue;
			}
			
			// See if the data will fit
			if ( memory.get(i).size < e.space ) {
				continue;
			}
			
			found = 1;
			loc = i;
			break;	
		}
		
		if ( found == 0 ) {
			for ( int i = 0; i < loc; ++i ) {
				// Continue if the partition is full
				if ( !memory.get(i).partitionId.equals(".") ) {
					continue;
				}
				
				// See if the data will fit
				if ( memory.get(i).size < e.space ) {
					continue;
				}
				
				found = 1;
				loc = i;
				break;	
			}
		}
	
		if ( found == 0 ) {
			// Display the defrag message 
			printProcessStartDefrag( time, e.processId );
			
			// Defrag the memory
			DefragReturn dr = defrag();
			
			// Add the time delay to the time
			time += dr.timeDelay;
			addTimeDelay( dr.timeDelay );
			
			// Print end of defrag
			printProcessEndDefrag( time, dr );
			printMemory(memory);
			
			// Get the location of the empty partition and continue
			for ( int i = 0; i < memory.size(); ++i ) {
				if ( memory.get(i).partitionId.equals(".") ) {
					loc = i;
					break;
				}
			}
		}
		
		if ( memory.get(loc).size == e.space ) {
			// Set the partition ID
			memory.get(loc).partitionId = e.processId;
			
			// Sort the partitions
			Collections.sort(memory);
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
		
		startIndex = memory.get(loc).startLocation + e.space;
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
		if ( loc - 1 >= 0 && loc + 1 < memory.size() && memory.get(loc-1).partitionId.equals(".") && memory.get(loc+1).partitionId.equals(".") ) {
			memory.get(loc-1).size += memory.get(loc).size + memory.get(loc+1).size;
			memory.remove(loc+1);
			memory.remove(loc);
		} else if ( loc - 1 >= 0 && memory.get(loc-1).partitionId.equals(".") ) {
			memory.get(loc-1).size += memory.get(loc).size;
			memory.remove(loc);
		} else if ( loc + 1 < memory.size() && memory.get(loc+1).partitionId.equals(".") ) {
			memory.get(loc).size += memory.get(loc+1).size;
			memory.remove(loc+1);
		}
		
		// Sort the partitions
		Collections.sort( memory );
			
		// Print message
		printProcessRemoved( time, e.processId );
		printMemory( memory );
		
	}
	
	private void addTimeDelay( int timeDelay ) {
		
		// Create the new queue
		PriorityQueue<Event> nq = new PriorityQueue<Event>( eventQueue.size(),
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
		
		// Remove all old events, increment time, and place in new queue
	    while(!eventQueue.isEmpty()) {
	    	
	    	// Get the next event
	        Event e = eventQueue.poll();
	        
	        // Set the new time
	        e.time += timeDelay;
	        
	        // Add the event to the new queue
	        nq.add(e);
	        
	    }
	    
	    // Set the queue
	    eventQueue = nq;
	}
	
	public class Event {		
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
		return "Contiguous -- Next-Fit";
	}

}
