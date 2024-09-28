package ru.tensor.sbis.communicator.ui.quickreply;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.util.UUID;

import ru.tensor.sbis.communicator.push.model.MessagePushModel;
import ru.tensor.sbis.common.util.UUIDUtils;
import ru.tensor.sbis.common.util.UrlUtils;
import ru.tensor.sbis.communicator.quickreply.QuickReplyModel;
import ru.tensor.sbis.communicator.quickreply.QuickReplyManager;
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate;

/**
 * Created by aa.mironychev on 08.08.17.
 */

public class QuickReplyPresenterImpl implements QuickReplyContract.Presenter {

    @Nullable
    private QuickReplyContract.View mView;

    @NonNull
    private final QuickReplyManager mManager;

    private final QuickReplyModel mQuickReplyModel;

    // Data
    @Nullable
    private UUID mDialogUuid;
    @Nullable
    private UUID mMessageUuid;
    @Nullable
    private MessagePushModel.Sender mRecipient;
    @Nullable
    private String mTargetMessage;

    private boolean mIsComment;

    public QuickReplyPresenterImpl(@NonNull QuickReplyManager manager, @Nullable QuickReplyModel model) {
        mManager = manager;
        mQuickReplyModel = model;
        if (model != null) {
            mDialogUuid = model.getDialogUuid();
            mMessageUuid = model.getMessageUuid();
            mRecipient = model.getRecipient();
            mTargetMessage = model.getTargetMessage();
            mIsComment = model.isComment();
        }
    }

    @Override
    public void attachView(@NonNull QuickReplyContract.View view) {
        mView = view;
        if (mRecipient != null) {
            UUID personUuid = mRecipient.uuid;
            mView.setRecipientData(
                    personUuid,
                    getRecipientIconLink(personUuid),
                    getRecipientName()
            );
        }
        mView.setTargetMessage(mTargetMessage);
        mView.updateSendButtonEnabled(!isEmptyMessage(mView.getMessage()));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isEmptyMessage(@Nullable String message) {
        return message == null || message.trim().isEmpty();
    }

    @Nullable
    private String getRecipientIconLink(@Nullable UUID personUuid) {
        if (personUuid != null) {
            final String localUrl = UrlUtils.getImageUrl(UUIDUtils.toString(personUuid));
            if (localUrl != null) {
                return UrlUtils.formatUrl(localUrl);
            }
        }
        return null;
    }

    private String getRecipientName() {
        if (mRecipient != null) {
            return PersonNameTemplate.SURNAME_NAME.format(
                    mRecipient.surname,
                    mRecipient.name,
                    mRecipient.patronymic
            );
        }
        return "";
    }

    @Override
    public void onSendMessageClick() {
        if (mView != null) {
            String message = mView.getMessage();
            if (!TextUtils.isEmpty(message)) {
                if (mIsComment) {
                    mView.showIsCommentDialog();
                } else {
                    sendMessage();
                }
            }
        }
    }

    @Override
    public void onSendCommentConfirm() {
        sendMessage();
    }

    @Override
    public void onMessageChanged(@Nullable String message) {
        if (mView != null) {
            mView.updateSendButtonEnabled(!isEmptyMessage(message));
        }
    }

    private void sendMessage() {
        if (mView != null) {
            if (mDialogUuid != null && mMessageUuid != null && mRecipient != null && mRecipient.uuid != null) {
                mQuickReplyModel.setTargetMessage(mView.getMessage());
                mManager.sendMessage(mQuickReplyModel, null);
            } else {
                mView.showSendingError();
            }
            mView.close();
        }
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {
        mManager.close();
        // do nothing
    }
}
