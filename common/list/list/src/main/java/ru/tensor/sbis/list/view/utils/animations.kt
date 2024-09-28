/**
 * Набор инструментов для настройки и работы с анимациями компонента списка
 *
 * @author du.bykov
 */
package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator

/**
 * Применяет к [ItemAnimator] настройки времени анимаций по умолчанию
 */
internal fun ItemAnimator.setupAnimations() {
    changeDuration = DEFAULT_ITEM_CHANGE_DURATION
    moveDuration = DEFAULT_ITEM_MOVE_DURATION
    addDuration = DEFAULT_ITEM_ADD_DURATION
    removeDuration = DEFAULT_ITEM_REMOVE_DURATION
    (this as SimpleItemAnimator).supportsChangeAnimations = false
}

/**
 * Время анимации по умолчанию для изменения контента в элементе. Анимация отключена
 *
 * @see ItemAnimator.setChangeDuration
 */
private const val DEFAULT_ITEM_CHANGE_DURATION = 0L

/**
 * Время анимации перемещений элементов списка
 *
 * @see ItemAnimator.setMoveDuration
 */
private const val DEFAULT_ITEM_MOVE_DURATION = 80L

/**
 * Время анимации добавления элементов списка
 *
 * @see ItemAnimator.setAddDuration
 */
private const val DEFAULT_ITEM_ADD_DURATION = 80L

/**
 * Время анимации удаления элементов списка
 *
 * @see ItemAnimator.setRemoveDuration
 */
private const val DEFAULT_ITEM_REMOVE_DURATION = 80L