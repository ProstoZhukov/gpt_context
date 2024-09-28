package ru.tensor.sbis.design.retail_views.utils

import android.content.Context
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull

/*** Добавить недостающие атрибуты стиля View в тему контекста. */
internal fun Context.applyStyle(@AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int): Context =
    ContextThemeWrapper(this, resources.newTheme().apply {
        // накладываем стиль вьюшки на отдельный экземпляр темы,
        // чтобы атрибуты не распространялись в глобальном контексте
        setTo(this@applyStyle.theme)
        applyStyle(getDataFromAttrOrNull(defStyleAttr) ?: defStyleRes, /* force = */ false)
    })

/**
 * Выходит ли вьюшка за границы своих контейнеров по вертикали.
 * Проверяет расположение вьюшек относительно их родителей по всей иерархии
 * т. к. сама вьюшка может растягивать родителя и уже он выйдет за границы своего контейнера.
 * @param targetAncestor контейнер, относительно которого необходимо делать проверку. Вглубь всей иерархии, если не задан.
 */
internal fun View.isOutOfHierarchyBoundsVertically(targetAncestor: View? = null): Boolean {
    var topOffset = 0
    var bottomOffset = 0
    var currentView = this
    var currentParent = parent as? View
    while (currentParent != null && currentParent != targetAncestor) {
        topOffset += currentView.top
        bottomOffset += currentParent.measuredHeight - currentView.bottom
        if (topOffset < 0 || bottomOffset < 0) return true

        currentView = currentParent
        currentParent = currentView.parent as? View
    }
    return false
}

/**
 * Выходит ли вьюшка за границы своих контейнеров по горизонтали.
 * Проверяет расположение вьюшек относительно их родителей по всей иерархии
 * т. к. сама вьюшка может растягивать родителя и уже он выйдет за границы своего контейнера.
 * @param targetAncestor контейнер, относительно которого необходимо делать проверку. Вглубь всей иерархии, если не задан.
 */
internal fun View.isOutOfHierarchyBoundsHorizontally(targetAncestor: View? = null): Boolean {
    var leftOffset = 0
    var rightOffset = 0
    var currentView = this
    var currentParent = parent as? View
    while (currentParent != null && currentParent != targetAncestor) {
        leftOffset += currentView.left
        rightOffset += currentParent.measuredWidth - currentView.right
        if (leftOffset < 0 || rightOffset < 0) return true

        currentView = currentParent
        currentParent = currentView.parent as? View
    }
    return false
}
