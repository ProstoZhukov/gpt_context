package ru.tensor.sbis.communicator.common.crud

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.util.PersonAvatarPrefetchHelper
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/** @SelfDocumented */
interface ThemeRepositoryProvider : Feature {
    /** @SelfDocumented */
    fun getThemeRepository(themeController: DependencyProvider<ThemeController>): ThemeRepository
}

/** @SelfDocumented */
interface ThemeRepository : BaseListRepository<ListResultOfConversationViewDataMapOfStringString, ThemeFilter, DataRefreshedThemeControllerCallback> {

    var avatarPrefetchHelper: PersonAvatarPrefetchHelper?

    /** @SelfDocumented */
    fun onThemeClosed(themeUUID: UUID)

    /** @SelfDocumented */
    fun getUrlByUuid(themeUuid: UUID): String

    /** @SelfDocumented */
    fun subscribeTypingUsers(callback: TypingNotificatorCallback): Subscription

    /**
     * Переписка сейчас откроется.
     * Метод необходимо вызывать, чтобы контроллер сбросил timestampSentLocal для доставленных на облако сообщений.
     * Иначе сообщения в переписке могут отображаться в неправильном порядке.
     */
    fun onThemeBeforeOpened(themeUuid: UUID)

    /** @SelfDocumented */
    fun onThemeAfterOpened(themeUuid: UUID, filter: MessageFilter?, isChat: Boolean)

    /** @SelfDocumented */
    fun onThemeTabBeforeOpen()

    /** @SelfDocumented */
    fun onThemeTabAfterOpen()
}
