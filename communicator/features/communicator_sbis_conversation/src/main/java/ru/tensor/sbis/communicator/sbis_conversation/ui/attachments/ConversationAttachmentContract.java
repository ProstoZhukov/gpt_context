package ru.tensor.sbis.communicator.sbis_conversation.ui.attachments;

import java.util.List;
import java.util.UUID;

import androidx.annotation.StringRes;

/** SelfDocumented */
public interface ConversationAttachmentContract {

    interface View extends AttachmentsSigningView {

        void showSigningActionsMenu(boolean onlySignButton);

        void showDeletingConfirmationDialog(@StringRes int deleteConfirmationString);
    }

    interface AttachmentsSigningView {

        void showAttachmentsSigning(List<UUID> attachmentsUuids);
    }
}
