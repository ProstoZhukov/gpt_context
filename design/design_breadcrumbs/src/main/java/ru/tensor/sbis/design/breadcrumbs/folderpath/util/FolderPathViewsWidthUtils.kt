/**
 * Инструменты для распределения доступного пространства между содержимым FolderPathView
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.breadcrumbs.folderpath.util

import android.view.ViewGroup
import ru.tensor.sbis.design.breadcrumbs.folderpath.FolderPathView

/**
 * Ограничения ширины view, используемых в [FolderPathView]
 *
 * @author us.bessonov
 */
internal data class FolderPathViewsWidth(
    val folderWidth: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    val breadCrumbsWidth: Int = ViewGroup.LayoutParams.WRAP_CONTENT
)

/**
 * Определяет ширину заголовка и хлебных крошек, распределяя между ними доступное пространство
 *
 * @see [Правила распределения места](http://axure.tensor.ru/standarts/v7/хлебные_крошки__версия_02_.html)
 */
internal fun resolveFolderPathViewsWidth(
    desiredFolderWidth: Int,
    desiredBreadCrumbsWidth: Int,
    homeIconWidth: Int,
    availableWidth: Int
): FolderPathViewsWidth {

    /**
     * Половина доступной ширины
     */
    val halfWidth = availableWidth / 2

    /**
     * Общая желаемая ширина
     */
    val totalDesiredWidth = desiredFolderWidth + desiredBreadCrumbsWidth + homeIconWidth

    return when {
        // всё помещается полностью
        totalDesiredWidth <= availableWidth                  -> FolderPathViewsWidth()
        // хлебные крошки полностью, оставшееся место доступно для заголовка
        desiredBreadCrumbsWidth + homeIconWidth <= halfWidth -> FolderPathViewsWidth(
            folderWidth = availableWidth - desiredBreadCrumbsWidth - homeIconWidth
        )
        // заголовок полностью, оставшееся место доступно для хлебных крошек
        desiredFolderWidth <= halfWidth                      -> FolderPathViewsWidth(
            breadCrumbsWidth = availableWidth - desiredFolderWidth - homeIconWidth
        )
        // заголовок и хлебные крошки делят доступное место поровну
        else                                                 -> FolderPathViewsWidth(
            halfWidth, availableWidth - halfWidth - homeIconWidth
        )
    }
}
