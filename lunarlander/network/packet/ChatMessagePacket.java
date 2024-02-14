package lunarlander.network.packet;

import java.io.Serializable;


public class ChatMessagePacket extends Packet implements Serializable {

	public ChatMessagePacket(int speakerId, int receiverId, String message) {
		this.speakerId = speakerId;
		this.message = message;
        this.receiverId = receiverId;
	}
	
	public ChatMessagePacket(int speakerId, String message) {
		this(speakerId, EVERYONE, message);
	}
    
    public String toString() {
        return "CHAT from " + speakerId + " to " + receiverId + ": " + message;
    }
	
	public Integer speakerId;
    public Integer receiverId;
	public String message;
	
	public static final int EVERYONE = -1;
}
