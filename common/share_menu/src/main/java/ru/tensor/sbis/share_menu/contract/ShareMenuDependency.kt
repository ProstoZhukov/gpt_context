package ru.tensor.sbis.share_menu.contract

import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.permission.PermissionFeature

/**
 * Зависимости меню для "поделиться".
 *
 * @author vv.chekurda
 */
internal interface ShareMenuDependency : LoginInterface.Provider {

    /**
     * Список обработчиков функциональности "поделиться".
     */
    val shareHandlers: List<ShareHandler>

    /**
     * API модуля разрешений.
     */
    val permissionFeature: PermissionFeature?

    /**
     * Сервис навигации, поставляющий доступные разделы в приложении для пользователя.
     */
    val navigationService: NavigationService

    /** Утилита для отправки аналитики. */
    val analyticsUtil: AnalyticsUtil?
}