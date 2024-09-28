package ru.tensor.sbis.communicator.contract

import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.calendar_decl.schedule.ViolationActivityProvider
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SupportChannelListFragmentFactory
import ru.tensor.sbis.info_decl.dialogs.DialogNotificationPushDelegate
import ru.tensor.sbis.info_decl.news.ui.NewsActivityProvider
import ru.tensor.sbis.user_activity_track.service.UserActivityService

/**
 * Внешние зависимости модуля пушей сообщений
 * @see NewsActivityProvider
 * @see MainActivityProvider
 * @see ViolationActivityProvider
 * @see UserActivityService
 *
 * @author vv.chekurda
 */
interface CommunicatorPushDependency :
    MainActivityProvider {

    val newsActivityProvider: NewsActivityProvider?

    val violationActivityProvider: ViolationActivityProvider?

    val complainServiceProvider: ComplainService.Provider?

    val userActivityService: UserActivityService?
        get() = null

    val supportChannelListFragmentFactory: SupportChannelListFragmentFactory?

    val dialogNotificationPushDelegate: DialogNotificationPushDelegate?
}
