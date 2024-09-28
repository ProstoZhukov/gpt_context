package ru.tensor.sbis.business_tools_decl.reporting.event;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.info_decl.notification.NotificationUUID;

/**
 * Событие обновления состояния требования
 *
 * @author ev.grigoreva
 */
public class RequirementStateChangeEvent {
    @NonNull
    public final NotificationUUID notificationUuid;
    @NonNull
    public final RequirementState state;
    @Nullable
    public final String rejectionReasonCode;

    public RequirementStateChangeEvent(@NonNull NotificationUUID notificationUuid,
                                       @NonNull RequirementState state,
                                       @Nullable String rejectionReasonCode) {
        this.notificationUuid = notificationUuid;
        this.state = state;
        this.rejectionReasonCode = rejectionReasonCode;
    }

    public enum RequirementState {
        CONFIRMED, REJECTED, FINISHED
    }
}
