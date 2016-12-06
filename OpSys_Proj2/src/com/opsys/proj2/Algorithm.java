package com.opsys.proj2;

public abstract class Algorithm {
	
	private final int framesPerLine = 32;
	private final int frames = 256;

	//main simulation method
    public abstract void simulate(Process.Blueprint[] blueprints);

    public void printMemory(Character[] memory) {
    	int rows = frames/framesPerLine;
    	int index = 0;
    	
    	for(int j = 0; j < framesPerLine; ++j)
    		System.out.print("=");
    	System.out.println();
    	
    	for(int i = 0; i < rows; ++i) {
    		for(int j = 0; j < framesPerLine; ++j) {
    			System.out.print(memory[index]);
    			++index;
    		}
    		System.out.println();
    	}
    	
    	for(int j = 0; j < framesPerLine; ++j)
    		System.out.print("=");
    	System.out.println();
    }
}
