package ru.tensor.sbis.common.util;

import android.content.pm.PackageManager;
import androidx.annotation.NonNull;

/**
 * @author am.boldinov
 */
public class PermissionUtil {

    public static final int PERMISSION_CAMERA_REQUEST_CODE = 8;
    public static final int PERMISSION_STORAGE_REQUEST_CODE = 9;

    public static boolean verifyPermissions(@NonNull int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
