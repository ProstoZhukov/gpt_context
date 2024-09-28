package ru.tensor.sbis.main_screen_decl.content

import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Контракт сущности, предоставляющей конфигурацию дашборда.
 *
 * @author us.bessonov
 */
interface DashboardContentController : ContentController {

    /**
     * Конфигурация дашборда.
     */
    val dashboardConfig: DashboardConfiguration

    /** @SelfDocumented */
    val navxId: NavxIdDecl
        get() = dashboardConfig.navxId

}