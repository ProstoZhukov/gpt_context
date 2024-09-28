package ru.tensor.sbis.business_tools_decl.reporting.event;

import androidx.annotation.NonNull;
import ru.tensor.sbis.info_decl.notification.NotificationUUID;

/**
 * @author ev.grigoreva
 */
public class ReportingReadEvent {

    @NonNull
    public final NotificationUUID notificationUuid;

    public ReportingReadEvent(@NonNull NotificationUUID notificationUuid) {
        this.notificationUuid = notificationUuid;
    }
}
