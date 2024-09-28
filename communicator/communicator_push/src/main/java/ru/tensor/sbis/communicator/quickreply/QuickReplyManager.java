package ru.tensor.sbis.communicator.quickreply;

import androidx.annotation.Nullable;

/**
 * Created by aa.mironychev on 14.08.17.
 */
public interface QuickReplyManager {

    void sendMessage(QuickReplyModel quickReplyModel, @Nullable SendCallback callback);

    void close();

    /**
     * Колбэк, который будет вызван по завершению отправки сообщения
     */
    interface SendCallback {
        void call();
    }
}
