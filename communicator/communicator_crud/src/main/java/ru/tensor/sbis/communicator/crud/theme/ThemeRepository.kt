package ru.tensor.sbis.communicator.crud.theme

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.common.crud.ThemeRepositoryProvider
import ru.tensor.sbis.communicator.common.util.PersonAvatarPrefetchHelper
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.*

/** @SelfDocumented */
class ThemeRepositoryProviderImpl : ThemeRepositoryProvider {

    override fun getThemeRepository(themeController: DependencyProvider<ThemeController>): ThemeRepository {
        return ControllerThemeRepository(themeController)
    }
}

/**
 * Репозиторий реестра диалогов/чатов
 */
internal class ControllerThemeRepository(
    private val controller: DependencyProvider<ThemeController>,
) : ThemeRepository {

    override var avatarPrefetchHelper: PersonAvatarPrefetchHelper? = null

    //region list
    override fun list(filter: ThemeFilter): ListResultOfConversationViewDataMapOfStringString {
        Timber.d("Theme CRUD list. $filter")
        return controller.get().list(filter).also {
            tryPrefetchAvatars(filter, it)
        }
    }

    override fun refresh(filter: ThemeFilter): ListResultOfConversationViewDataMapOfStringString {
        Timber.d("Theme CRUD refresh. $filter")
        return controller.get().refresh(filter).also {
            tryPrefetchAvatars(filter, it)
        }
    }

    private fun tryPrefetchAvatars(filter: ThemeFilter, result: ListResultOfConversationViewDataMapOfStringString) {
        if (avatarPrefetchHelper != null && filter.fromUuid == null) {
            result.result.take(10).forEach { viewData ->
                avatarPrefetchHelper?.prefetchBitmaps(
                    viewData.participantsCollage,
                    viewData.photoUrl,
                    PersonAvatarPrefetchHelper.REGISTRY_AVATAR_SIZE_DP
                )
            }
        }
    }

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedThemeControllerCallback): Subscription =
        controller.get().dataRefreshed().subscribe(callback)

    override fun subscribeTypingUsers(callback: TypingNotificatorCallback): Subscription =
        controller.get().typingNotificator().subscribe(callback)

    //endregion

    override fun onThemeClosed(themeUUID: UUID) = controller.get().onThemeClosed(themeUUID)
    override fun getUrlByUuid(themeUuid: UUID): String = controller.get().getUrlByUuid(themeUuid)
    override fun onThemeBeforeOpened(themeUuid: UUID) = controller.get().onThemeBeforeOpened(themeUuid)
    override fun onThemeAfterOpened(themeUuid: UUID, filter: MessageFilter?, isChat: Boolean) =
        controller.get().onThemeAfterOpened(themeUuid, filter, isChat)
    override fun onThemeTabBeforeOpen() = controller.get().onThemeTabBeforeOpen()
    override fun onThemeTabAfterOpen() = controller.get().onThemeTabAfterOpen()
}
