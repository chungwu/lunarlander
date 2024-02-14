package lunarlander.map;
/**
 * @author Chung
 *
 * Represents Exception when errors are encountered while reading a .moon file
 */
public class MoonFileException extends Exception {
    public MoonFileException(String msg) {
        super(msg);
    }
    
    private static final long serialVersionUID = 1L;
}
