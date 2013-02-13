/**
 * SakuraCmd - Package: net.syamn.sakuracmd.exception
 * Created: 2013/02/13 18:03:33
 */
package net.syamn.sakuracmd.exception;

/**
 * SignException (SignException.java)
 * @author syam(syamn)
 */
public class SignException extends Exception{
    private static final long serialVersionUID = -4848896931124689348L;

    public SignException(String message) {
        super(message);
    }

    public SignException(Throwable cause) {
        super(cause);
    }

    public SignException(String message, Throwable cause) {
        super(message, cause);
    }
}
