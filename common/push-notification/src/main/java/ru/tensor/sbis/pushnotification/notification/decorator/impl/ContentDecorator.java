package ru.tensor.sbis.pushnotification.notification.decorator.impl;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import ru.tensor.sbis.pushnotification.notification.decorator.NotificationDecorator;
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage;

/**
 * Декоратор, формирующий основной контент уведомления.
 *
 * @author am.boldinov
 */
public final class ContentDecorator implements NotificationDecorator {

    private String mTicker;
    private String mTitle;
    private String mText;

    public ContentDecorator() {
    }

    /**
     * @param ticker текст, отображаемый в строке состояния
     *               (а также в качестве основного текста push на заблокированном экране)
     * @param title  заголовок
     * @param text   основной текст
     */
    public ContentDecorator(String ticker, String title, String text) {
        mTicker = ticker;
        mTitle = title;
        mText = text;
    }

    /**
     * @param message тело push-уведомления
     */
    public ContentDecorator(@NonNull PushNotificationMessage message) {
        this(message.getMessage(), message.getTitle(), message.getMessage());
    }

    /**
     * @param ticker текст, отображаемый в строке состояния
     *               (а также в качестве основного текста push на заблокированном экране)
     */
    public void setTicker(String ticker) {
        mTicker = ticker;
    }

    /**
     * @param title заголовок уведомления
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * @param text основной текст уведомления
     */
    public void setText(String text) {
        mText = text;
    }

    @Override
    public void decorate(@NonNull NotificationCompat.Builder builder) {
        builder
                .setTicker(mTicker)
                .setContentTitle(mTitle)
                .setContentText(mText);
    }

}
