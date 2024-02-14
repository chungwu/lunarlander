package lunarlander.network.packet;

import java.io.Serializable;


/**
 * A TCP packet from either the client or server.  These packets are used during game setup
 * and also to announce game-start and game-end events.  This is a "dumb" class - it does only
 * very basic parsing and validation.  Most validation must be done by the user.
 * 
 * @author mike
 */
public class CommandPacket extends Packet implements Serializable {

    public CommandPacket(Command command, Object[] arguments) {
    	this.command = command;
    	this.arguments = arguments;
    }
    
    public CommandPacket(Command command) {
    	this(command, null);
    }
    
    public static enum Command {
    	START_GAME, RECEIVED_MAP, START_PLAYING
    }
    
    public Command command;  
    public Object[] arguments;
}
