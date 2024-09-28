package ru.tensor.sbis.common.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import ru.tensor.sbis.design.utils.KeyboardUtils;
import timber.log.Timber;

/**
 * Created by da.rodionov on 07.05.15.
 */
public class DeviceUtils {

    private static String sDeviceId;

    @NonNull
    private final Context mContext;

    public DeviceUtils(@NonNull Context context) {
        mContext = context;
    }

    @Px
    public int getScreenWidthInPx() {
        return getScreenWidthInPx(mContext);
    }

    @Px
    public int getScreenHeightInPx() {
        return getScreenHeightInPx(mContext);
    }

    public DisplayMetrics getDisplayMetrics() {
        return getDisplayMetrics(mContext);
    }

    @Px
    public static int getScreenWidthInPx(@NonNull Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    @Px
    public static int getScreenHeightInPx(@NonNull Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static int getScreenMinSideInPx(@NonNull Context context) {
        final DisplayMetrics metrics = getDisplayMetrics(context);
        return Math.min(metrics.widthPixels, metrics.heightPixels);
    }

    @NonNull
    private static DisplayMetrics getDisplayMetrics(@NonNull Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    @Deprecated
    /*Используй KeyboardUtils.showKeyboard*/
    public static void showKeyboard(@NonNull Context context, @NonNull View view) {
        KeyboardUtils.showKeyboard(view);
    }

    @Deprecated
    /*Используй KeyboardUtils.showKeyboard*/
    public static void showKeyboardForced(@NonNull Context context, @NonNull View view) {
        KeyboardUtils.showKeyboard(view);
    }

    @Deprecated
    /*Используй KeyboardUtils.showKeyboard*/
    public static void showKeyboard(@NonNull View view) {
        KeyboardUtils.showKeyboard(view);
    }

    @Deprecated
    /*Используй KeyboardUtils.hideKeyboard*/
    public static void hideKeyboard(@NonNull View view) {
        KeyboardUtils.hideKeyboard(view);
    }

    public static String getDeviceId(@NonNull Context context) {
        if (sDeviceId == null) {
            final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) + "";
            final String hdSerial = Build.SERIAL + "";
            final UUID deviceUuid = new UUID(androidId.hashCode(), (long) hdSerial.hashCode() << 32);
            sDeviceId = deviceUuid.toString();
        }
        return sDeviceId;
    }

    /**
     * Check conditions for 'value' in {@link okhttp3.Headers.Builder.checkNameAndValue(String, String)}.
     */
    private static String escapeBannedChars(String string, char exchangeChar) {
        final int length = string.length();
        final char[] value = new char[length];
        char c;
        for (int i = 0; i < length; ++i) {
            c = string.charAt(i);
            if ((c <= '\u001f' && c != '\t') || c >= '\u007f') {
                value[i] = exchangeChar;
            } else {
                value[i] = string.charAt(i);
            }
        }
        return new String(value);
    }

    /**
     * Returns the consumer friendly device information
     */
    public static String getDeviceInfo() {
        String release = Build.VERSION.RELEASE;
        String android = "Android SDK: " + Build.VERSION.SDK_INT + " (" + release + ")";
        return String.format("%s; %s", getDeviceName(), android);
    }

    /**
     * Returns the consumer friendly device name
     */
    public static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(@NonNull String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }
        return phrase.toString();
    }

    public static String getPrintUsedMemoryString() {
        long freeSize = 0;
        long totalSize = 0;
        long usedSize = 0;
        try {
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory();
            totalSize = info.totalMemory();
            usedSize = totalSize - freeSize;
        } catch (Exception e) {
            //ignored
        }
        return "freeSize = " + freeSize
                + " , totalSize = " + totalSize
                + " , usedSize = " + usedSize;
    }

    /**
     * Проверка наличия камеры на устройстве.
     * Примечание: наличие {@link PackageManager.FEATURE_CAMERA} не гарантирует на 100%, что на устройстве есть физическая камера.
     * Могут быть случаи, когда камера поддерживается системой и даже есть разъём для камеры, но самой камеры нет.
     */
    public static boolean checkCameraHardware(Context context) {
        boolean hasCameraFeature = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        Timber.d("Has camera feature is %s", hasCameraFeature);
        return hasCameraFeature;
    }

    /**
     * Получение доступного места для папки, назначенной для хранения данных приложения
     */
    public static long getAvailableDiskSpaceForAppFolderInMb(Context context) {
        long sizeMb = 1024L * 1024L;
        return new StatFs(context.getApplicationInfo().dataDir).getAvailableBytes() / sizeMb;
    }

    /**
     * Признак того что приложение запущено на эмуляторе
     *
     * https://github.com/flutter/plugins/blob/master/packages/device_info/device_info/android/src/main/java/io/flutter/plugins/deviceinfo/MethodCallHandlerImpl.java#L115-L131
     */
    public static boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86_64")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86_64");
    }

}
