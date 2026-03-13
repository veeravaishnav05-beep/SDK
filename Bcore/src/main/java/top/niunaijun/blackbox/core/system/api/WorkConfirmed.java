package top.niunaijun.blackbox.core.system.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public final class WorkConfirmed {
    private static volatile boolean activated = false;
    private static volatile String activeKey = null;
    private static volatile long expiryTime = 0L;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final Map<String, String> VALID_KEYS;

    static {
        HashMap<String, String> keys = new HashMap<>();

        // Key -> Expiry date time
        keys.put("BBOXSDKBYZENIN1MON", "2026-03-31 23:59:59");
        keys.put("BBOXSDKBYZENIN3MON", "2026-06-30 23:59:59");
        keys.put("BBOXSDKLIFETIME", "2099-12-31 23:59:59");

        VALID_KEYS = Collections.unmodifiableMap(keys);
    }

    private WorkConfirmed() {
    }

    public static synchronized boolean activateSdk(String key) {
        if (key == null) {
            clearActivation();
            return false;
        }

        String cleanKey = key.trim();
        String expiryString = VALID_KEYS.get(cleanKey);

        if (expiryString == null) {
            clearActivation();
            return false;
        }

        long exp = parseExpiry(expiryString);
        if (exp <= 0L) {
            clearActivation();
            return false;
        }

        if (System.currentTimeMillis() <= exp) {
            activated = true;
            activeKey = cleanKey;
            expiryTime = exp;
            return true;
        }

        clearActivation();
        return false;
    }

    public static boolean ConfirmedWork() {
        return activated
                && activeKey != null
                && expiryTime > 0L
                && System.currentTimeMillis() <= expiryTime;
    }

    public static String getActiveKey() {
        return activeKey;
    }

    public static long getExpiryTime() {
        return expiryTime;
    }

    public static String getExpiryDateTime() {
        if (expiryTime <= 0L) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(expiryTime);
    }

    public static synchronized void clearActivation() {
        activated = false;
        activeKey = null;
        expiryTime = 0L;
    }

    private static long parseExpiry(String dateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            sdf.setLenient(false);
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.parse(dateTime).getTime();
        } catch (ParseException e) {
            return 0L;
        }
    }
}