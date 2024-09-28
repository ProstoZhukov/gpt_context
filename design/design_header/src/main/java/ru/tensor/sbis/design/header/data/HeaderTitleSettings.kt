package ru.tensor.sbis.design.header.data

import androidx.annotation.StringRes
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout

/**
 * Настройки заголовка в шапке.
 *
 * @author ma.kolpakov
 */
sealed class HeaderTitleSettings {

    /**
     * Заголовок из строкового ресурса.
     */
    class TextResTitle(@StringRes internal val textRes: Int) : HeaderTitleSettings()

    /**
     * Заголовок из строки.
     */
    class TextTitle(internal val text: String) : HeaderTitleSettings()

    /**
     * Вкладки в заголовке шапки. [ToolbarTabLayout]
     */
    class TabsTitle(
        internal val tabs: LinkedHashMap<Int, ToolbarTabLayout.ToolbarTab>,
        internal val selectedTab: Int,
    ) : HeaderTitleSettings()

    /**
     * Нет заголовка шапки.
     */
    object NoneTitle : HeaderTitleSettings()

    companion object {

        /**
         * Создать настройки для заголовка с текстом, который задаётся
         * либо строкой, либо ресурсом. Приоритет у строки.
         */
        fun withText(
            titleString: String? = null,
            @StringRes
            titleRes: Int? = null
        ) =
            when {
                titleString != null -> TextTitle(titleString)
                titleRes != null -> TextResTitle(titleRes)
                else -> NoneTitle
            }
    }
}
