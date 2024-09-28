package ru.tensor.sbis.communicator.di.quickreply;

import androidx.annotation.Nullable;

import dagger.BindsInstance;
import dagger.Component;
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent;
import ru.tensor.sbis.communicator.quickreply.QuickReplyManager;
import ru.tensor.sbis.communicator.quickreply.QuickReplyModel;
import ru.tensor.sbis.communicator.ui.quickreply.QuickReplyContract;
import ru.tensor.sbis.pushnotification.center.PushCenter;
import ru.tensor.sbis.user_activity_track.service.UserActivityService;

/**
 * Created by aa.mironychev on 08.08.17.
 */
@QuickReplyScope
@Component(
        modules = { QuickReplyModule.class },
        dependencies = {
                CommunicatorCommonComponent.class
        }
)
public interface QuickReplyComponent {

    QuickReplyContract.Presenter getPresenter();

    QuickReplyManager getManager();

    PushCenter getPushCenter();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder quickReplyModel(@Nullable QuickReplyModel model);

        @BindsInstance
        Builder userActivityService(@Nullable UserActivityService userActivityService);

        Builder communicatorCommonComponent(CommunicatorCommonComponent communicatorCommonComponent);

        QuickReplyComponent build();
    }
}