package ru.tensor.sbis.main_screen_basic.view

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.design_menu.showMenu
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationApi
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterContent
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterItem
import ru.tensor.sbis.design.topNavigation.internal_view.SbisTopNavigationFooterView
import ru.tensor.sbis.design.topNavigation.internal_view.footer.SbisTopNavigationSearchFooterItemView
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.image_loading.BitmapSource
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.main_screen_basic.R
import ru.tensor.sbis.main_screen_basic.widget.BasicMainScreenWidget
import ru.tensor.sbis.main_screen_decl.basic.ProvideContentAction
import ru.tensor.sbis.main_screen_decl.basic.TopNavigationConfigurator
import ru.tensor.sbis.main_screen_decl.basic.data.ContentHost
import ru.tensor.sbis.main_screen_decl.basic.data.ContentPlacement
import ru.tensor.sbis.main_screen_decl.basic.data.Counter
import ru.tensor.sbis.main_screen_decl.basic.data.CustomCounter
import ru.tensor.sbis.main_screen_decl.basic.data.ScreenEntryPoint
import ru.tensor.sbis.main_screen_decl.basic.data.ScreenId
import ru.tensor.sbis.main_screen_decl.basic.data.ServiceCounter
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.toolbox_decl.counters.CountersSubscriptionProvider

/**
 * Реализация [TopNavigationConfigurator] для настройки [SbisTopNavigationView] в компоненте Раскладка.
 *
 * @author us.bessonov
 */
internal class TopNavigationConfiguratorImpl(
    private val topNavigation: SbisTopNavigationView,
    private val mainScreenWidget: BasicMainScreenWidget,
    private val countersSubscriptionProvider: CountersSubscriptionProvider?,
    private val viewLifecycleOwner: LifecycleOwner,
    private val menuFragmentManager: FragmentManager
) : TopNavigationConfigurator {

    private val context: Context
        get() = topNavigation.context

    private val entryPointViews = mutableMapOf<ScreenId, View>()
    private val hiddenEntryPoints = mutableSetOf<ScreenId>()

    private var menuAnchorView: View? = null
    private val menuBuilder = MenuBuilder(context, hiddenEntryPoints)

    private val counterManager by lazy {
        CounterManager(countersSubscriptionProvider, viewLifecycleOwner.lifecycle.coroutineScope)
    }

    /** @SelfDocumented */
    fun updateTopNavigationViews() {
        val newItems = entryPointViews.entries
            .filterNot { hiddenEntryPoints.contains(it.key) }
            .map { it.value }
            .toMutableList()
            .apply {
                menuAnchorView?.let {
                    if (!menuBuilder.isEmpty()) add(it)
                }
            }

        if (topNavigation.rightItems != newItems) {
            topNavigation.rightItems = newItems
        }
    }

    override fun addScreenEntryPoint(
        entryPoint: ScreenEntryPoint,
        provideContent: ProvideContentAction?,
        contentPlacement: ContentPlacement?
    ) {
        val id = entryPoint.id
        val entryPointView = getEntryPointView(entryPoint)
            ?.also { entryPointViews[id] = it }

        contentPlacement ?: return
        provideContent ?: return

        fun showContent(id: ScreenId) {
            mainScreenWidget.showContent(
                contentPlacement,
                provideContent,
                id.toString(),
                ContentController.MenuClick()
            )
        }

        if (entryPoint is ScreenEntryPoint.MenuItem) {
            addMenuEntryPoint(entryPoint, ::showContent)
        } else {
            entryPointView?.setOnClickListener {
                showContent(id)
            }
        }

        mainScreenWidget.registerControllerProvider(id, provideContent, contentPlacement)
    }

    override fun addCustomActionEntryPoint(entryPoint: ScreenEntryPoint, action: (ContentHost) -> Unit) {
        val view = getEntryPointView(entryPoint)
        view?.setOnClickListener {
            mainScreenWidget.performCustomShowAction(action)
        }
    }

    override fun hideEntryPoint(id: ScreenId) {
        hiddenEntryPoints.add(id)
    }

    override fun showEntryPoint(id: ScreenId) {
        hiddenEntryPoints.remove(id)
    }

    override fun setLogo(logoType: SbisLogoType) {
        topNavigation.content = SbisTopNavigationContent.Logo(logoType)
    }

    override fun setBackground(background: BitmapSource?, roundCorners: Boolean) {
        topNavigation.setGraphicBackground(background, roundCorners)
    }

    override fun configureFooter(
        newFooterItems: List<SbisTopNavigationFooterItem>?,
        actionWithFooter: SbisTopNavigationFooterView.() -> Unit
    ) {
        newFooterItems?.let {
            topNavigation.footerItems = it
        }
        topNavigation.footerView.actionWithFooter()
    }

    override fun configureSearchFooter(configure: SearchInput.() -> Unit) {
        configureFooter(
            listOf(
                SbisTopNavigationFooterItem(content = SbisTopNavigationFooterContent.SearchInput)
            )
        ) {
            configure<SbisTopNavigationSearchFooterItemView> {
                it.searchView.configure()
            }
        }
    }

    override fun applyCustomTopNavigationConfiguration(configure: SbisTopNavigationApi.() -> Unit) {
        topNavigation.configure()
    }

    private fun getEntryPointView(entryPoint: ScreenEntryPoint) = when (entryPoint) {
        is ScreenEntryPoint.Icon -> createIconView(entryPoint.icon, entryPoint.counter)
        is ScreenEntryPoint.Profile -> createProfileView(entryPoint.photoData)
        is ScreenEntryPoint.CustomView -> entryPoint.view
        is ScreenEntryPoint.ViewLocator -> findEntryPointView(entryPoint.viewId)
        else -> null
    }

    private fun addMenuEntryPoint(
        entryPoint: ScreenEntryPoint.MenuItem,
        showContent: (ScreenId) -> Unit
    ) {
        menuBuilder.addItem(entryPoint.id, entryPoint.title, showContent)

        if (menuAnchorView == null) {
            menuAnchorView = createIcon(
                SbisButtonTextIcon(SbisMobileIcon.Icon.smi_navBarMore, SbisButtonIconSize.X3L)
            ).apply {
                setOnClickListener(::showMenu)
            }
        }
    }

    private fun createIconView(icon: Char, counter: Counter?) =
        createIcon(SbisButtonTextIcon(icon, SbisButtonIconSize.X5L)).apply {
            when (counter) {
                is ServiceCounter -> {
                    counterManager.registerServiceCounterView(this, counter.name, counter.counterSource)
                }

                is CustomCounter -> {
                    counterManager.registerCustomCounterView(this, counter.counterFlow)
                }

                null -> { /* ignore */
                }
            }
        }

    private fun createProfileView(photoData: PhotoData) = PersonView(context).apply {
        setSize(PhotoSize.S)
        setData(photoData)
        id = R.id.basic_main_screen_profile_view_id
    }

    private fun findEntryPointView(@IdRes viewId: Int) =
        topNavigation.findViewById<View>(viewId)

    private fun createIcon(icon: SbisButtonIcon) = SbisButton(context).apply {
        model = model.copy(
            icon = icon,
            backgroundType = SbisButtonBackground.Transparent
        )
        backgroundType = SbisButtonBackground.Transparent
    }

    private fun showMenu(anchor: View) {
        menuBuilder.buildMenu().showMenu(
            menuFragmentManager,
            AnchorVerticalLocator(VerticalAlignment.BOTTOM).apply {
                anchorView = anchor
            },
            AnchorHorizontalLocator(HorizontalAlignment.RIGHT).apply {
                anchorView = anchor
            }
        )
    }

}