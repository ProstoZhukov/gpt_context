package ru.tensor.sbis.design.tabs.api

import android.graphics.drawable.Drawable
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounterStyle
import ru.tensor.sbis.design.tabs.tabItem.SbisTabView
import ru.tensor.sbis.design.tabs.util.SbisTabsViewDsl
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisDimen

/**
 * Контент вкладки [SbisTabView].
 *
 * Для создания можно использовать [SbisTabsViewDsl].
 * @author da.zolotarev
 */
sealed interface SbisTabViewItemContent {

    /**
     * Основной текст.
     */
    class Text(val text: PlatformSbisString) : SbisTabViewItemContent

    /**
     * Дополнительный текст.
     */
    class AdditionalText(val text: PlatformSbisString) : SbisTabViewItemContent

    /**
     * Счетчик.
     */
    class Counter(val accentedCounter: StateFlow<Int>? = null, val unaccentedCounter: StateFlow<Int>? = null) :
        SbisTabViewItemContent

    /**
     * Иконка.
     */
    class Icon(val textIcon: PlatformSbisString, val customDimen: SbisDimen? = null) : SbisTabViewItemContent

    /**
     * Иконка c счетчиком в верхнем правом углу.
     */
    class IconCounter(
        val textIcon: PlatformSbisString,
        val customDimen: SbisDimen? = null,
        val counterValue: StateFlow<Int>?,
        val counterStyle: SbisCounterStyle
    ) : SbisTabViewItemContent

    /**
     * Изображение.
     */
    class Image(val image: Drawable) : SbisTabViewItemContent
}