/**
 * SakuraCmd - Package: net.syamn.sakuracmd.exception
 * Created: 2013/01/04 3:21:00
 */
package net.syamn.sakuracmd.exception;

/**
 * NotSupportedException (NotSupportedException.java)
 * @author syam(syamn)
 */
public class NotSupportedException extends Exception{
    private static final long serialVersionUID = -1151594657374375702L;

    public NotSupportedException(String message) {
        super(message);
    }

    public NotSupportedException(Throwable cause) {
        super(cause);
    }

    public NotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
