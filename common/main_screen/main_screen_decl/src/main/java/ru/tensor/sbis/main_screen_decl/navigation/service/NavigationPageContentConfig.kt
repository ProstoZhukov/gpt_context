package ru.tensor.sbis.main_screen_decl.navigation.service

import java.util.UUID

/**
 * Конфигурация страницы мобильного приложения.
 *
 * @param frameId идентификатор фрейма.
 * @param dashboards идентификаторы дашбордов.
 *
 * @author us.bessonov
 */
class NavigationPageContentConfig(
    val frameId: UUID?,
    val dashboards: List<UUID>
)