package ru.tensor.sbis.design.context_menu.dividers

import ru.tensor.sbis.design.context_menu.Item

/**
 * Модель элемента меню представляющего собой широкий разделитель.
 *
 * @author ma.kolpakov
 */
sealed class Divider(internal val type: DividerType = DividerType.SLIM) : Item

/**
 * Широкий разделитель.
 */
object BoldDivider : Divider(DividerType.BOLD)

/**
 * Тонкий разделитель.
 */
internal object SlimDivider : Divider(DividerType.SLIM)

/**
 * Тип разделителя.
 */
internal enum class DividerType {

    /** @SelfDocumented */
    BOLD,

    /** @SelfDocumented */
    SLIM
}