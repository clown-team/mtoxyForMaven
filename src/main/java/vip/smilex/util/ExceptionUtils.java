package vip.smilex.util;

import java.io.IOException;

/**
 * @author smilex
 * @date 2023/5/3/18:39
 */
public final class ExceptionUtils {
    private ExceptionUtils() {
    }

    public static boolean isNotIoException(Throwable throwable) {
        return !(throwable instanceof IOException);
    }
}
