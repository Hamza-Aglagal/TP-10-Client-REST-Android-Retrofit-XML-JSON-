package ma.projet.restclient.utils;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomUtils {
    private static final String APP_TAG = "RestClientAppv1";
    private static final boolean DEBUG_MODE = true;

    // Helper method to log formatted messages
    public static void logInfo(String component, String message) {
        if (DEBUG_MODE) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            Log.i(APP_TAG, String.format("[%s] [%s]: %s", timestamp, component, message));
        }
    }

    public static void logError(String component, String message, Throwable t) {
        if (DEBUG_MODE) {
            Log.e(APP_TAG, String.format("[ERROR] [%s]: %s", component, message), t);
        }
    }

    // Dummy method for obfuscation purposes
    public static String generateSessionId() {
        return "SESSION-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
    }
}
