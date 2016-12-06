package com.opsys.proj2;

public class Partition implements Comparable<Partition> {

	String partitionId;
	int startLocation;
	int size;

    public Partition( String id, int sl, int s ) {
        partitionId = id;
        startLocation = sl;
        size = s;
    }
    
	@Override
	public int compareTo(Partition p2) {
		return startLocation - p2.startLocation;
	}
    
}
