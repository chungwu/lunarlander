package lunarlander.network;
/**
 * This exception is thrown when an invalid/corrupt packet is parsed
 * 
 * @author mike
 */
public class InvalidPacketException extends Exception {
    
    public InvalidPacketException() {
        super();
    }
    
    public InvalidPacketException(String message) {
        super(message);
    }
    
    public InvalidPacketException(Exception e) {
        super(e);
    }
    
    private static final long serialVersionUID = 1L;
}
