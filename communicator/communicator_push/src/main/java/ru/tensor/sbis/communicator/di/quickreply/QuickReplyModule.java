package ru.tensor.sbis.communicator.di.quickreply;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.Module;
import dagger.Provides;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.communicator.generated.MessageController;
import ru.tensor.sbis.communicator.quickreply.QuickReplyManager;
import ru.tensor.sbis.communicator.quickreply.QuickReplyManagerImpl;
import ru.tensor.sbis.communicator.quickreply.QuickReplyModel;
import ru.tensor.sbis.communicator.ui.quickreply.QuickReplyContract;
import ru.tensor.sbis.communicator.ui.quickreply.QuickReplyPresenterImpl;
import ru.tensor.sbis.pushnotification.center.PushCenter;
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentProvider;
import ru.tensor.sbis.user_activity_track.service.UserActivityService;

/**
 * Created by aa.mironychev on 08.08.17.
 */
@Module
public class QuickReplyModule {

    @Provides
    @QuickReplyScope
    @NonNull
    QuickReplyContract.Presenter providePresenter(@NonNull QuickReplyManager manager, @Nullable QuickReplyModel model) {
        return new QuickReplyPresenterImpl(manager, model);
    }

    @Provides
    @QuickReplyScope
    @NonNull
    QuickReplyManager provideManager(@NonNull Context context,
                                     @NonNull DependencyProvider<MessageController> controller,
                                     @NonNull PushCenter center,
                                     @Nullable UserActivityService userActivityService) {
        return new QuickReplyManagerImpl(context, controller, center, userActivityService);
    }

    @Provides
    @QuickReplyScope
    @NonNull
    PushCenter providePushCenter(@NonNull Context context) {
        return PushNotificationComponentProvider.get(context)
                .getPushCenter();
    }
}
