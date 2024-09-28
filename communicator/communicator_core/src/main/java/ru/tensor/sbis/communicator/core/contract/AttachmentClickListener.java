package ru.tensor.sbis.communicator.core.contract;

import java.util.List;
import java.util.UUID;
import androidx.annotation.NonNull;
import ru.tensor.sbis.communicator.generated.AttachmentViewModel;

/**
 * Интерфейс обработчика клика на вложение.
 *
 * @author vv.chekurda
 */
public interface AttachmentClickListener {

    /**
     * Клик по вложению диалога.
     *
     * @param dialogUuid          идентификатор диалога.
     * @param attachments         список вложений модели диалога.
     * @param attachmentsPosition вложение, по которому кликнули.
     */
    void onAttachmentClick(
        @NonNull UUID dialogUuid,
        @NonNull List<AttachmentViewModel> attachments,
        int attachmentsPosition
    );
}
