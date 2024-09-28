package ru.tensor.sbis.toolbox_decl.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Запрос для загрузки дашборда.
 *
 * @property size размер верстки дашборда
 *
 * @author am.boldinov
 */
sealed interface DashboardRequest : Parcelable {

    val size: DashboardSize

    /**
     * Загружает дашборд по идентификатору [id] элемента структуры навигации.
     */
    @Parcelize
    data class NavxId(
        val id: NavxIdDecl,
        override val size: DashboardSize = DashboardSize.XS
    ) : DashboardRequest

    /**
     * Загружает дашборд по идентификатору [id] конкретной страницы (фрейма).
     */
    @Parcelize
    data class PageId(
        val id: String,
        override val size: DashboardSize = DashboardSize.XS
    ) : DashboardRequest
}