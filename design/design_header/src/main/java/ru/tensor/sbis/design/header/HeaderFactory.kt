/**
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.header

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import ru.tensor.sbis.design.header.data.HeaderAcceptSettings
import ru.tensor.sbis.design.header.data.HeaderTitleSettings
import ru.tensor.sbis.design.header.data.LeftCustomContent
import ru.tensor.sbis.design.header.data.RightCustomContent
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout

/**
 * Создать шапку с заголовком и кнопками: применить и закрыть. Все элементы опциональны.
 */
fun createContainerHeaderTitled(
    context: Context,
    @StringRes
    title: Int? = null,
    onAccept: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null
): View {
    return BaseHeaderView(context).apply {
        setHeaderContent(
            HeaderTitleSettings.withText(titleRes = title),
            headerAcceptSettings(onAccept),
            onClose != null
        )
        if (onAccept != null) addAcceptListener(onAccept)
        if (onClose != null) addCloseListener(onClose)
    }
}

/**
 * Создать шапку с вкладками и кнопками: применить и закрыть. Кнопки опциональны.
 */
fun createContainerHeaderTabbed(
    context: Context,
    tabs: LinkedHashMap<Int, ToolbarTabLayout.ToolbarTab>,
    selectedTab: Int,
    onTabChanged: ((Int) -> Unit)? = null,
    onAccept: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null
): View {
    return BaseHeaderView(context).apply {
        setHeaderContent(
            HeaderTitleSettings.TabsTitle(tabs, selectedTab),
            headerAcceptSettings(onAccept),
            onClose != null
        )
        if (onAccept != null) addAcceptListener(onAccept)
        if (onClose != null) addCloseListener(onClose)
        if (onTabChanged != null) addTabChangedListener(onTabChanged)
    }
}

/**
 * Создать шапку с заголовком, кнопкой принятия с текстом,
 * кнопкой закрытия и кастомным контентом.
 * Все элементы опциональны.
 */
fun createContainerHeaderAcceptText(
    context: Context,
    @StringRes
    title: Int?,
    @StringRes
    acceptTitle: Int?,
    customContent: ((Context) -> View)? = null,
    onAccept: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
): View {
    return BaseHeaderView(context).apply {
        setHeaderContent(
            titleSettings = HeaderTitleSettings.withText(titleRes = title),
            acceptSettings = if (acceptTitle != null)
                HeaderAcceptSettings.TextAccept(acceptTitle)
            else
                HeaderAcceptSettings.NoneAccept,
            hasClose = onClose != null,
            leftCustomContent = leftCustomContent(customContent),
        )
        if (onAccept != null) addAcceptListener(onAccept)
        if (onClose != null) addCloseListener(onClose)
    }
}

/**
 * Создает шапку со всеми возможными параметрами.
 * @param titleSettings настройки заголовка
 * @param acceptSettings настройки кнопки подтверждения
 * @param hasClose необходимость отобразить кнопу закрытия(с крестиком)
 * @param leftCustomContent контент слева от заголовка [LeftCustomContent]
 * @param rightCustomContent прикладной контент справа от заголовка
 * @param onAccept действие при нажатии на кнопку подтверждения
 * @param onClose действие при нажатии на кнопку закрытия
 */
fun createHeader(
    context: Context,
    titleSettings: HeaderTitleSettings = HeaderTitleSettings.NoneTitle,
    acceptSettings: HeaderAcceptSettings = HeaderAcceptSettings.NoneAccept,
    hasClose: Boolean = false,
    leftCustomContent: LeftCustomContent = LeftCustomContent.NoneContent,
    rightCustomContent: RightCustomContent = RightCustomContent.NoneContent,
    onAccept: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null
): View {
    return BaseHeaderView(context).apply {
        setHeaderContent(titleSettings, acceptSettings, hasClose, leftCustomContent, rightCustomContent)
        if (onAccept != null) addAcceptListener(onAccept)
        if (onClose != null) addCloseListener(onClose)
    }
}

private fun leftCustomContent(customContent: ((Context) -> View)?) =
    if (customContent != null) LeftCustomContent.Content(customContent) else LeftCustomContent.NoneContent

private fun headerAcceptSettings(onAccept: (() -> Unit)?) =
    if (onAccept != null) HeaderAcceptSettings.IconAccept else HeaderAcceptSettings.NoneAccept