/**
 * Набор классов и функций для создания вкладок через DSL.
 *
 * @author da.zolotarev
 */
package ru.tensor.sbis.design.tabs.util

import android.graphics.drawable.Drawable
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounterStyle
import ru.tensor.sbis.design.tabs.api.SbisTabViewItemContent
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.res.SbisDimen
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import java.util.LinkedList

@DslMarker
annotation class SbisTabsViewDsl

/**
 *
 * Создать набор вкладок для [SbisTabsView.tabs].
 *
 * Пример:
 * ```
 * tabs {
 *     tab {
 *         content {
 *             text(PlatformSbisString.Value("ss"))
 *             icon(PlatformSbisString.Icon(SbisMobileIcon.Icon.smi_GoogleIcon))
 *             counter(flowOf(3))
 *         }
 *         isSelected = true
 *         customTitleColor = null
 *         customIconColor = null
 *     }
 *     tab {...}
 *     tab {...}
 * }
 */
fun tabs(block: TabsBuilder.() -> Unit) = TabsBuilder().apply(block).build()

/**
 * Создать одну вкладку для [SbisTabsView].
 *
 * Пример:
 * ```
 * singleTab {
 *     content {
 *         text(PlatformSbisString.Value("ss"))
 *         icon(PlatformSbisString.Icon(SbisMobileIcon.Icon.smi_GoogleIcon))
 *         counter(3, true)
 *     }
 *     isSelected = true
 *     customTitleColor = null
 *     customIconColor = null
 * }
 */
fun singleTab(block: TabBuilder.() -> Unit) = TabBuilder().apply(block).build()

/**
 * Хелпер для создания вкладок через DSL.
 */
@SbisTabsViewDsl
class TabsBuilder {
    private val tabBuilders = mutableListOf<TabBuilder>()

    /** Создать вкладку. */
    fun tab(block: TabBuilder.() -> Unit) {
        tabBuilders.add(TabBuilder().apply(block))
    }

    /** @SelfDocumented */
    internal fun build(): LinkedList<SbisTabsViewItem> = LinkedList<SbisTabsViewItem>().apply {
        tabBuilders.map { it.build() }.forEach { add(it) }
    }
}

/**
 * Хелпер для создания вкладки через DSL.
 */
@SbisTabsViewDsl
class TabBuilder {
    private val contentBuilder: ContentBuilder = ContentBuilder()

    /**
     *  Будет удалено по https://dev.sbis.ru/opendoc.html?guid=07557630-6756-41e2-aa41-58bdbea1add1&client=3
     */
    @Deprecated("Теперь выбранностью управляет панель вкладок (selectedTabIndex)")
    var isSelected = false

    /** [SbisTabsViewItem.customTitleColor] */
    var customTitleColor: SbisColor? = null

    /** [SbisTabsViewItem.customIconColor] */
    var customIconColor: SbisColor? = null

    /** [SbisTabsViewItem.isMain] */
    var isMain = false

    /** [SbisTabsViewItem.id] */
    var id: String? = null

    /** [SbisTabsViewItem.navxId] */
    var navxId: NavxIdDecl? = null

    /** [SbisTabsViewItem.position] */
    var position: HorizontalPosition = HorizontalPosition.LEFT

    /** Создать контент вкладки. */
    fun content(block: ContentBuilder.() -> Unit) = contentBuilder.apply(block)

    /** @SelfDocumented */
    internal fun build() =
        SbisTabsViewItem(
            contentBuilder.build(),
            isSelected,
            isMain,
            position,
            customTitleColor,
            customIconColor,
            id,
            navxId
        )
}

/**
 * Хелпер для создания контента вкладок через DSL.
 */
class ContentBuilder {
    private val content: LinkedList<SbisTabViewItemContent> = LinkedList()

    /** Создать [SbisTabViewItemContent.Text]. */
    fun text(text: PlatformSbisString) = content.add(SbisTabViewItemContent.Text(text))

    /** Создать [SbisTabViewItemContent.AdditionalText]. */
    fun additionalText(text: PlatformSbisString) = content.add(SbisTabViewItemContent.AdditionalText(text))

    /** Создать [SbisTabViewItemContent.Text]. */
    fun text(text: String) = content.add(SbisTabViewItemContent.Text(PlatformSbisString.Value(text)))

    /** Создать [SbisTabViewItemContent.AdditionalText]. */
    fun additionalText(text: String) = content.add(
        SbisTabViewItemContent.AdditionalText(PlatformSbisString.Value(text))
    )

    /** Создать [SbisTabViewItemContent.Icon]. */
    fun icon(textIcon: PlatformSbisString, customDimen: SbisDimen? = null) =
        content.add(SbisTabViewItemContent.Icon(textIcon, customDimen))

    /** Создать [SbisTabViewItemContent.Icon]. */
    fun icon(textIcon: SbisMobileIcon.Icon, customDimen: SbisDimen? = null) =
        content.add(SbisTabViewItemContent.Icon(PlatformSbisString.Icon(textIcon), customDimen))

    /** Создать [SbisTabViewItemContent.IconCounter]. */
    fun iconCounter(
        textIcon: PlatformSbisString,
        customDimen: SbisDimen? = null,
        counterValue: StateFlow<Int>?,
        counterStyle: SbisCounterStyle
    ) =
        content.add(SbisTabViewItemContent.IconCounter(textIcon, customDimen, counterValue, counterStyle))

    /** Создать [SbisTabViewItemContent.IconCounter]. */
    fun iconCounter(
        textIcon: SbisMobileIcon.Icon,
        customDimen: SbisDimen? = null,
        counterValue: StateFlow<Int>?,
        counterStyle: SbisCounterStyle
    ) =
        content.add(
            SbisTabViewItemContent.IconCounter(
                PlatformSbisString.Icon(textIcon),
                customDimen,
                counterValue,
                counterStyle
            )
        )

    /** Создать [SbisTabViewItemContent.Image]. */
    fun image(image: Drawable) =
        content.add(SbisTabViewItemContent.Image(image))

    /** Создать [SbisTabViewItemContent.Counter]. */
    fun counter(accentedCounter: StateFlow<Int>? = null, unaccentedCounter: StateFlow<Int>? = null) =
        content.add(SbisTabViewItemContent.Counter(accentedCounter, unaccentedCounter))

    /** @SelfDocumented */
    internal fun build() = content
}
