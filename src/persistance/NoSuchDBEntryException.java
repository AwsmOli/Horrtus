package persistance;

/**
 * Created by olfad on 23.01.14.
 */
public class NoSuchDBEntryException extends Exception {
    public NoSuchDBEntryException(String s) {
        super(s);
    }
}
