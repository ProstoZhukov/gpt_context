package ru.tensor.sbis.common.util;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import android.widget.Toast;

import ru.tensor.sbis.design_notification.SbisPopupNotification;

/**
 * Created by vb.kokanov on 25.12.15.
 */
public class ClipboardManager {

    @NonNull
    private final Context mContext;

    public ClipboardManager(@NonNull Context context) {
        mContext = context;
    }

    public void copyToClipboard(@NonNull final String text) {
        copyToClipboard(mContext, text);
    }

    /**
     * Копирует переданный текст в буфер обмена и показывает {@link Toast} уведомление о копироваании
     *
     * @param text копируемый текст
     * @param toastMsgRes ссылка на строковый ресурс, текст которого будет отображен в Toast
     */
    public void copyToClipboard(@NonNull final String text, @StringRes int toastMsgRes) {
        copyToClipboard(mContext, text);
        SbisPopupNotification.pushToast(mContext, toastMsgRes);
    }

	/**
	 * Копирует переданный текст в буфер обмена
	 *
	 * @param context контекст вызова
	 * @param text    копируемый текст
	 */
	public static void copyToClipboard(@NonNull final Context context, @NonNull final String text) {
        copyToClipboard(context, text, "Copied Text");
	}

    /**
     * Копирует переданный текст в буфер обмена
     *
     * @param context контекст вызова
     * @param text    копируемый текст
     * @param label   пользовательская метка
     */
    public static void copyToClipboard(@NonNull final Context context, @NonNull final String text, @NonNull final String label) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }
}
