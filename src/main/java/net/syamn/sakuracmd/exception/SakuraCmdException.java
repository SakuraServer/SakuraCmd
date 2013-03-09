package net.syamn.sakuracmd.exception;

/**
 * workspace - Package: net.syamn.sakuracmd.exception
 * User: syam
 * Date: 13/03/10 3:14
 */
public class SakuraCmdException extends Exception{
    private static final long serialVersionUID = -1L;

    public SakuraCmdException(String message) {
        super(message);
    }

    public SakuraCmdException(Throwable cause) {
        super(cause);
    }

    public SakuraCmdException(String message, Throwable cause) {
        super(message, cause);
    }
}
