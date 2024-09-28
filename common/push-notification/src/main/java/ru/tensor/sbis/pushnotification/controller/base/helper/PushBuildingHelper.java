package ru.tensor.sbis.pushnotification.controller.base.helper;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.PluralsRes;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.tensor.sbis.pushnotification.model.PushData;
import ru.tensor.sbis.pushnotification.notification.decorator.impl.ContentDecorator;
import ru.tensor.sbis.pushnotification.notification.impl.SbisPushNotification;
import ru.tensor.sbis.pushnotification.notification.PushNotification;
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage;

/**
 * Вспомогательный класс для формирования стандартного для приложений сбис отображения push-уведомления.
 *
 * @author am.boldinov
 */
public final class PushBuildingHelper {

    @NonNull
    private final Context mContext;

    public PushBuildingHelper(@NonNull Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Создание уведомления, стилизованного под стандартный для сбис приложений вид.
     * Контент декорируется с помощью {@link ContentDecorator}
     *
     * @param data модель данных push-уведомления
     * @return стилизованное уведомление
     */
    @NonNull
    public PushNotification createSbisNotification(@NonNull PushData data) {
        return createSbisNotification(data.getMessage());
    }

    /**
     * Создание уведомления, стилизованного под стандартный для сбис приложений вид.
     * Контент декорируется с помощью {@link ContentDecorator}
     *
     * @param message тело push-уведомелния
     * @return стилизованное уведомление
     */
    @NonNull
    public PushNotification createSbisNotification(@NonNull PushNotificationMessage message) {
        final PushNotification notification = new SbisPushNotification(mContext);
        return notification.decorate(new ContentDecorator(message));
    }

    /**
     * Создание уведомления, стилизованного под стандартный для сбис приложений вид.
     * Контент декорируется с помощью {@link ContentDecorator}
     *
     * @param decorator декоратор контента
     * @return стилизованное уведомление
     */
    @NonNull
    public PushNotification createSbisNotification(@NonNull ContentDecorator decorator) {
        PushNotification notification = new SbisPushNotification(mContext);
        return notification.decorate(decorator);
    }

    /**
     * Интерфейс поставщика строк для контента отображаемого push-уведомления.
     *
     * @param <T> тип источника данных
     */
    public interface LineProvider<T> {

        /**
         * @param model модель источника данных
         * @return строка для отображения
         */
        String getLine(@NonNull T model);
    }

    /**
     * Поставщик строк по умолчанию.
     */
    public static class DefaultLineProvider implements LineProvider<String> {

        @Override
        public String getLine(@NonNull String text) {
            return text;
        }
    }

    /**
     * Стилизация push-уведомления в виде группового события.
     *
     * @param notification уведомление для отображения
     * @param pluralsId    ид ресурса строки со счетчиком для отображения в заголовке уведомления
     * @param items        список данных для отображения
     * @param provider     поставщик строк
     * @param reverse      флаг о необходимости отображения строк в обратном порядке
     * @param <T>          тип данных
     */
    public <T> void customizeAsGrouped(
            @NonNull PushNotification notification,
            @PluralsRes int pluralsId,
            @NonNull List<T> items,
            @NonNull LineProvider<? super T> provider,
            boolean reverse) {

        // Create content
        List<String> content = new ArrayList<>(items.size());
        for (T item : items) {
            content.add(provider.getLine(item));
        }
        if (reverse) {
            Collections.reverse(content);
        }
        // Build inbox style
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        int counter = content.size();
        String lineForSingle = null;
        for (int i = 0; i < counter; i++) {
            String line = content.get(i);
            if (lineForSingle == null) {
                lineForSingle = line;
            }
            inboxStyle.addLine(line);
        }
        StringBuilder title = new StringBuilder();
        title.append(counter);
        title.append(" ");
        title.append(mContext.getResources().getQuantityString(pluralsId, counter));
        inboxStyle.setBigContentTitle(title);
        // Set style and number
        NotificationCompat.Builder builder = notification.getBuilder();
        builder.setStyle(inboxStyle)
                .setNumber(items.size())
                //in collapsed mode
                .setContentTitle(title);
        if (lineForSingle != null) {
            builder.setContentText(lineForSingle);
        }
    }

    /**
     Стилизация push-уведомления в виде группового события.
     *
     * @param notification уведомление для отображения
     * @param pluralsId    ид ресурса строки со счетчиком для отображения в заголовке уведомления
     * @param content      список строк для отображения
     * @param reverse      флаг о необходимости отображения строк в обратном порядке
     */
    public void customizeAsGrouped(@NonNull PushNotification notification, @PluralsRes int pluralsId, @NonNull List<String> content, boolean reverse) {
        customizeAsGrouped(notification, pluralsId, content, new DefaultLineProvider(), reverse);
    }

}
