package ru.tensor.sbis.design.topNavigation.api

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.profile_decl.titleview.ListContent
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.internal_view.ChildsConfigurationState
import ru.tensor.sbis.design.topNavigation.internal_view.MeasureContract
import ru.tensor.sbis.design.topNavigation.internal_view.emptyMeasurer
import ru.tensor.sbis.design.topNavigation.internal_view.largeTitleMeasurer
import ru.tensor.sbis.design.topNavigation.internal_view.logoMeasurer
import ru.tensor.sbis.design.topNavigation.internal_view.searchInputMeasurer
import ru.tensor.sbis.design.topNavigation.internal_view.smallTitleMeasurer
import ru.tensor.sbis.design.topNavigation.internal_view.tabsMeasurer
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import java.util.LinkedList

/**
 * Виды контента шапки.
 *
 * @param childsContent содержит "доступность" элементов шапки для коннкретного вида контента.
 * @param measurer контракт, содержащий метод измерения конкретного контента шапки.
 * @param isCollapsible определяет, сворачивается ли конкретный контент при скролле.
 *
 * @author da.zolotarev
 */
sealed class SbisTopNavigationContent(
    internal val childsContent: ChildsConfigurationState = ChildsConfigurationState.createAllAvailableContent(),
    internal val measurer: MeasureContract,
    internal val isCollapsible: Boolean = false
) {

    /**
     * Большой заголовок.
     *
     * @param title текст заголовка.
     */
    data class LargeTitle(
        val title: PlatformSbisString,
        val subtitle: PlatformSbisString? = null,
        val accentedCounter: StateFlow<Int> = MutableStateFlow(0),
        val unaccentedCounter: StateFlow<Int> = MutableStateFlow(0),
        val icon: SbisMobileIcon.Icon? = null,
        val navxId: NavxIdDecl? = null,
        val onIconClicked: () -> Unit = {}
    ) : SbisTopNavigationContent(measurer = largeTitleMeasurer, isCollapsible = true)

    /**
     * Маленький заголовок.
     *
     * @param title текст заголовка.
     * @param subtitle текст подзаголовка.
     * @param image изображение.
     */
    data class SmallTitle(
        val title: PlatformSbisString,
        val subtitle: PlatformSbisString? = null,
        val image: SbisTopNavigationImage? = null,
        val icon: SbisMobileIcon.Icon? = null,
        val navxId: NavxIdDecl? = null,
        val onIconClicked: () -> Unit = {}
    ) : SbisTopNavigationContent(measurer = smallTitleMeasurer, isCollapsible = true)

    /**
     * Маленький заголовок с поддержкой [ListContent].
     *
     * Предполагается использование с [SbisTopNavigationListContentTextHandler], так как через него ставится заголовок.
     *
     * @param model модель формирования заголовка.
     * @param image изображение(приоритетнее, изображений в [model]).
     */
    class SmallTitleListContent(
        val model: ListContent,
        val image: SbisTopNavigationImage? = null
    ) : SbisTopNavigationContent(measurer = smallTitleMeasurer, isCollapsible = true)

    /**
     * Панель поиска.
     */
    object SearchInput : SbisTopNavigationContent(measurer = searchInputMeasurer)

    /**
     * Панель вкладок.
     *
     * @param tabs список вкладок.
     */
    class Tabs(val tabs: LinkedList<SbisTabsViewItem>) :
        SbisTopNavigationContent(measurer = tabsMeasurer, isCollapsible = true)

    /**
     * Пустая шапка, может пригодиться, если шапка кастомная и задаётся через custom view.
     */
    object EmptyContent : SbisTopNavigationContent(measurer = emptyMeasurer)

    /**
     * Логотип.
     */
    class Logo(val logoType: SbisLogoType) : SbisTopNavigationContent(
        childsContent = ChildsConfigurationState(false, true, true),
        measurer = logoMeasurer
    )

    // region internal
    /**
     * Внутренний тип контента, указывающий что шапка еще не инициализрована (дочерние view не настроены).
     *
     * Если необходимо показывать пустую шапку, например пока данные не пришли, нужно установить [EmptyContent].
     */
    internal object NotInitializedContent : SbisTopNavigationContent(measurer = emptyMeasurer)
    // endregion
}