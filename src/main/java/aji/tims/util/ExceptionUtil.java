package aji.tims.util;

public class ExceptionUtil {
    public static boolean willThrowException(Runnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
