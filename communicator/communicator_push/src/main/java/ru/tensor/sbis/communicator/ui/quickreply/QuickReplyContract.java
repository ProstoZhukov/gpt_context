package ru.tensor.sbis.communicator.ui.quickreply;

import androidx.annotation.Nullable;

import java.util.UUID;

import ru.tensor.sbis.mvp.presenter.BasePresenter;

/**
 * Created by aa.mironychev on 08.08.17.
 */

public interface QuickReplyContract {

    interface View {

        void setRecipientData(@Nullable UUID personUuid, @Nullable String photoUrl, @Nullable String name);

        void setTargetMessage(@Nullable String message);

        String getMessage();

        void showIsCommentDialog();

        void updateSendButtonEnabled(boolean enabled);

        void showSendingError();

        void close();
    }

    interface Presenter extends BasePresenter<View> {

        void onSendMessageClick();

        void onSendCommentConfirm();

        void onMessageChanged(@Nullable String message);
    }

}
