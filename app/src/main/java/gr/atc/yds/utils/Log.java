package gr.atc.yds.utils;

import gr.atc.yds.BuildConfig;

/**
 * Created by ipapas on 12/09/17.
 */

public class Log {

    static final boolean LOG = BuildConfig.DEBUG;
    static final String GLOBAL_TAG = "";

    public static void i(String string) {
        i(GLOBAL_TAG, string);
    }
    public static void i(String tag, String string) {
        if (LOG) android.util.Log.i(tag, string);
    }
    public static void e(String string) {
        e(GLOBAL_TAG, string);
    }
    public static void e(String tag, String string) {
        if (LOG) android.util.Log.e(tag, string);
    }
    public static void d(String string) {
        d(GLOBAL_TAG, string);
    }
    public static void d(String tag, String string) {

        int maxLogSize = 1000;
        for(int i = 0; i <= string.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > string.length() ? string.length() : end;

            if (LOG) android.util.Log.d(tag, string.substring(start, end));

        }
    }
    public static void v(String string) {
        v(GLOBAL_TAG, string);
    }
    public static void v(String tag, String string) {
        if (LOG) android.util.Log.v(tag, string);
    }
    public static void w(String string) {
        w(GLOBAL_TAG, string);
    }
    public static void w(String tag, String string) {
        if (LOG) android.util.Log.w(tag, string);
    }
}
