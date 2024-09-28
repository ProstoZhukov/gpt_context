package ru.tensor.sbis.scanner.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.content.Context;
import android.view.View;
import ru.tensor.sbis.design.SbisMobileIcon;
import ru.tensor.sbis.design_notification.SbisPopupNotification;
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle;

/**
 * @author am.boldinov
 */
public class ScannerUiUtil {

    public static void setEnabledView(@Nullable View view, boolean enabled) {
        if (view != null) {
            view.setEnabled(enabled);
            view.setAlpha(enabled ? 1 : 0.5f);
        }
    }

    public static void showSuccessMessage(@NonNull final Context context, @StringRes final int messageResId) {
        SbisPopupNotification.INSTANCE.push(
            context,
            SbisPopupNotificationStyle.SUCCESS,
            context.getString(messageResId),
            String.valueOf(SbisMobileIcon.Icon.smi_Yes.getCharacter())
        );
    }

    public static void showErrorMessage(@NonNull final Context context, @NonNull final String message) {
        SbisPopupNotification.INSTANCE.push(
            context,
            SbisPopupNotificationStyle.ERROR,
            message,
            String.valueOf(SbisMobileIcon.Icon.smi_alert.getCharacter())
        );
    }

    public static void showErrorMessage(@NonNull final Context context, @StringRes final int messageResId) {
        showErrorMessage(context, context.getString(messageResId));
    }

    public static void showNetworkErrorMessage(@NonNull final Context context, @StringRes final int messageResId) {
        SbisPopupNotification.INSTANCE.push(
            context,
            SbisPopupNotificationStyle.ERROR,
            context.getString(messageResId),
            String.valueOf(SbisMobileIcon.Icon.smi_WiFiNone.getCharacter())
        );
    }
}
