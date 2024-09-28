package ru.tensor.sbis.design.tab_panel

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Описание вкладки
 *
 * @author ai.abramenko
 */
interface TabPanelItem : Parcelable {

    /**
     * Уникальный идентификатор
     */
    val id: String

    /**
     * Иконка
     */
    val icon: SbisMobileIcon.Icon

    /**
     * Заголовок
     */
    @get:StringRes
    val title: Int

    /**
     * Нужно ли отмечать вкладку при клике.
     * true -   При клике будет вызываться колбек, но вкладка отмечена не будет,
     *          т.е. выбранной останется та же вкладка, что и была.
     * false -  При клике будет вызываться колбек и вкладка будет помечаться выбранной.
     */
    val isUnmarked: Boolean
        get() = false
}

@Parcelize
data class DefaultTabPanelItem(
    override val id: String,
    override val icon: SbisMobileIcon.Icon,
    override val title: Int,
    override val isUnmarked: Boolean = false
) : TabPanelItem
