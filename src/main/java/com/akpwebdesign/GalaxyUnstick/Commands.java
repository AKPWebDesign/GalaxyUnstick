package com.akpwebdesign.GalaxyUnstick;

/**
 * All supported commands for GalaxyUnstick
 */
public enum Commands {

    MODE("mode"), ADDWORLD("addworld"), REMOVEWORLD("removeworld"), LISTWORLDS("listworlds"), RELOAD("reload"), NULL("");

    private final String toString;

    private Commands(String commandString) {
    	this.toString = commandString;
    }

    public String toString() {
        return toString;
    }
    
    public boolean equals(String command) {
    	if (command.equalsIgnoreCase(this.toString()))
    	{
    		return true;
    	}
		return false;
    }

}
