package vip.smilex.util;

import java.io.IOException;

/**
 * @author smilex
 * @date 2023/5/3/18:39
 */
public final class ExceptionUtils {
    private static final String EXCEPTION_ERROR_MSG_CONNECTION_RESET = "Connection reset by peer";

    private ExceptionUtils() {
    }

    public static boolean isConnectionResetException(Throwable exception) {
        if (exception instanceof IOException) {
            final IOException ioException = (IOException) exception;
            return EXCEPTION_ERROR_MSG_CONNECTION_RESET.equals(ioException.getMessage());
        }

        return false;
    }
}
