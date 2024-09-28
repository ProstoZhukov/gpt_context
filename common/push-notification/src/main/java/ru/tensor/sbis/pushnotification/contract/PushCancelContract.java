package ru.tensor.sbis.pushnotification.contract;

import android.os.Bundle;

import androidx.annotation.NonNull;
import ru.tensor.sbis.pushnotification.PushType;

import java.util.UUID;

/**
 * Утилита для работы с программным удалением пуш-уведомлений из шторки.
 * Включает в себя набор распространенных параметров для
 * передачи в {@link ru.tensor.sbis.pushnotification.center.PushCenter#cancel(PushType, Bundle)}
 * и обработки этих параметров внутри прикладного котнроллера.
 *
 * @author am.boldinov
 */
public final class PushCancelContract {

    // region Document
    private static final String KEY_DOCUMENT_ID = getPrefix().concat(".KEY_DOCUMENT_ID");

    /**
     * Создает параметры документа на основе идентификатора документа.
     */
    @NonNull
    public static Bundle createDocumentParams(String documentId) {
        Bundle bundle = new Bundle(1);
        bundle.putString(KEY_DOCUMENT_ID, documentId);
        return bundle;
    }

    /**
     * Возвращает идентификатор документа из параметров
     */
    public static String getDocumentId(@NonNull Bundle bundle) {
        return bundle.getString(KEY_DOCUMENT_ID);
    }
    // endregion

    // region Notification
    private static final String KEY_NOTIFICATION_UUID = getPrefix().concat(".KEY_NOTIFICATION_UUID");

    /**
     * Создает параметры уведомления на основе идентификатора документа и идентификатора уведомления
     */
    @NonNull
    public static Bundle createNotificationParams(String documentId, String notificationUuid) {
        Bundle bundle = new Bundle(2);
        bundle.putString(KEY_DOCUMENT_ID, documentId);
        bundle.putString(KEY_NOTIFICATION_UUID, notificationUuid);
        return bundle;
    }

    /**
     * Возвращает идентификатор уведомления из параметров
     */
    public static String getNotificationUuid(@NonNull Bundle bundle) {
        return bundle.getString(KEY_NOTIFICATION_UUID);
    }
    // endregion

    // region Dialog
    private static final String KEY_DIALOG_UUID = getPrefix().concat(".KEY_DIALOG_UUID");

    /**
     * Создает параметры диалога на основе идентификатора темы
     */
    @NonNull
    public static Bundle createDialogParams(UUID dialogUuid) {
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(KEY_DIALOG_UUID, dialogUuid);
        return bundle;
    }

    /**
     * Возвращает идентификатор темы из параметров
     */
    public static UUID getDialogUuid(@NonNull Bundle bundle) {
        return (UUID) bundle.getSerializable(KEY_DIALOG_UUID);
    }
    // endregion

    // region Orders
    private static final String KEY_ORDER_ID = getPrefix().concat(".KEY_ORDER_ID");

    /**
     * Создает параметры наряда на основе идентификатора наряда
     */
    @NonNull
    public static Bundle createOrderParams(int orderId) {
        Bundle bundle = new Bundle(1);
        bundle.putInt(KEY_ORDER_ID, orderId);
        return bundle;
    }

    /**
     * Возвращает идентификатор наряда из параметров
     */
    public static int getOrderId(@NonNull Bundle bundle) {
        return bundle.getInt(KEY_ORDER_ID, -1);
    }
    // endregion

    @NonNull
    private static String getPrefix() {
        return PushCancelContract.class.getCanonicalName();
    }

}
