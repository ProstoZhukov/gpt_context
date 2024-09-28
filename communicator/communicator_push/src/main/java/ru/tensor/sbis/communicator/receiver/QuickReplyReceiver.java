package ru.tensor.sbis.communicator.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.RemoteInput;
import android.text.TextUtils;

import java.util.UUID;

import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent;
import ru.tensor.sbis.communicator.di.CommunicatorPushComponent;
import ru.tensor.sbis.communicator.push.controller.BaseMessageNotificationController;
import ru.tensor.sbis.communicator.push.model.MessagePushModel;
import ru.tensor.sbis.communicator.di.quickreply.DaggerQuickReplyComponent;
import ru.tensor.sbis.communicator.di.quickreply.QuickReplyComponent;
import ru.tensor.sbis.communicator.quickreply.QuickReplyManager;
import ru.tensor.sbis.communicator.quickreply.QuickReplyModel;
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver;

/**
 * Created by aa.mironychev on 11.11.16.
 */

public class QuickReplyReceiver extends EntryPointBroadcastReceiver {

    private QuickReplyManager getManager(@NonNull Context context, QuickReplyModel quickReplyModel) {
        QuickReplyComponent component = DaggerQuickReplyComponent
                .builder()
                .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(context))
                .userActivityService(CommunicatorPushComponent.getInstance(context).getDependency().getUserActivityService())
                .quickReplyModel(quickReplyModel)
                .build();
        return component.getManager();
    }

    @Override
    public void onReady(@NonNull Context context, @NonNull Intent intent) {
        QuickReplyModel model = getModelFromIntent(intent);

        if (model.getRecipient() != null && model.getRecipient().uuid != null) {
            Bundle result = RemoteInput.getResultsFromIntent(intent);
            if (result != null && model.getDialogUuid() != null && !TextUtils.isEmpty(model.getTargetMessage()) && model.getMessageUuid() != null) {
                PendingResult pendingResult = goAsync();
                getManager(context, model).sendMessage(model, pendingResult::finish);
            }
        }
    }

    private QuickReplyModel getModelFromIntent(Intent intent) {
        String targetMessage = null;
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
        if (resultsFromIntent != null) {
            targetMessage = resultsFromIntent.getString(BaseMessageNotificationController.QUICK_REPLY_RECEIVER_REPLY_MESSAGE_KEY);
        }
        return new QuickReplyModel(
                (UUID) intent.getSerializableExtra(BaseMessageNotificationController.QUICK_REPLY_DIALOG_UUID_KEY),
                (UUID) intent.getSerializableExtra(BaseMessageNotificationController.QUICK_REPLY_MESSAGE_UUID_KEY),
                (MessagePushModel.Sender) intent
                        .getSerializableExtra(BaseMessageNotificationController.QUICK_REPLY_RECIPIENT_PERSON_MODEL_KEY),
                        targetMessage,
                false
        );
    }
}
