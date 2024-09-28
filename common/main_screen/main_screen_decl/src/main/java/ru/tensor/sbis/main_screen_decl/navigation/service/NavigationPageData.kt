package ru.tensor.sbis.main_screen_decl.navigation.service

import java.util.UUID

/**
 * Модель для описания свойств страницы мобильного приложения.
 *
 * @param navigationPageContentConfig конфигурация страницы.
 *
 * @author us.bessonov
 */
class NavigationPageData(val navigationPageContentConfig: NavigationPageContentConfig) {

    /** @SelfDocumented */
    val frameId: UUID?
        get() = navigationPageContentConfig.frameId
}