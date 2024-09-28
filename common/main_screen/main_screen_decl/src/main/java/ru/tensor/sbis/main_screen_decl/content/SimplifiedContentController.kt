package ru.tensor.sbis.main_screen_decl.content

import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.design.navigation.util.ActiveTabOnClickListener
import ru.tensor.sbis.design.navigation.util.NavigationDrawerStateListener
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.utils.errorSafe
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.content.install.FragmentInstallationStrategy
import ru.tensor.sbis.main_screen_decl.content.install.NonCacheFragmentInstallationStrategy
import ru.tensor.sbis.main_screen_decl.env.BottomBarProviderExt
import ru.tensor.sbis.main_screen_decl.fab.getIconButtons
import ru.tensor.sbis.toolbox_decl.navigation.NavigationItemHostScreen
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Упрощенная реализация [ContentController].
 *
 * @property fragmentInstallationStrategy стратегия встраивания фрагментов в контентную область.
 *
 * @author kv.martyshenko
 */
abstract class SimplifiedContentController @JvmOverloads constructor(
    protected val fragmentInstallationStrategy: FragmentInstallationStrategy = NonCacheFragmentInstallationStrategy()
) : ContentController,
    SideMenuSlideListener {

    private var lastEntryPoint: ContentController.EntryPoint? = null

    /**
     * Метод для создания контента экрана.
     *
     * @param selectionInfo информация о выбранном элементе.
     * @param mainScreen компонент главного экрана.
     */
    protected abstract fun createScreen(
        selectionInfo: ContentController.SelectionInfo,
        mainScreen: MainScreen
    ): ContentInfo

    /**
     * Метод для оповещения о смене состояния выбранности.
     *
     * @param navigationItem выбранный элемент.
     * @param isSelected выбран или нет.
     * @param mainScreen компонент главного экрана.
     * @param contentContainer информация о контенте.
     */
    protected open fun onSelectionChanged(
        navigationItem: NavigationItem,
        isSelected: Boolean,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) = Unit

    /**
     * Выполняет конфигурацию плавающих кнопок при переходе на экран. По умолчанию все кнопки скрываются.
     * Для переопределения начальной видимости используйте нужные аргументы в [resetActionButtonsVisibility].
     */
    protected open fun BottomBarProviderExt.configureActionButtons(mainScreen: MainScreen) {
        resetActionButtonsVisibility()
    }


    /**
     * Сбрасывает плавающие кнопки в исходное состояние.
     */
    @CallSuper
    protected open fun resetActionButtons(bottomBarProviderExt: BottomBarProviderExt) = with(bottomBarProviderExt) {
        resetNavigationFabStyle()
        resetNavigationFabClickListener()
        setExtraFabClickListener(null)
        setExtraFab2ClickListener(null)
        setExtraFab3ClickListener(null)
        setExtraFab4ClickListener(null)
        setTodayExtraFabClickListener(null)
    }

    /**
     * Скрывает избыточные плавающие кнопки.
     *
     * @param maxInitialVisibleButtonCount максимальное число кнопок, которое может быть отображено при открытии
     * данного экрана. Если сейчас кнопок отображается больше, чем может потребоваться, то остальные будут
     * скрыты. По умолчанию скрываются все кнопки.
     * @param hideTodayFab требуется ли скрывать кнопку с датой, или оставить её видимость неизменной. По умолчанию
     * кнопка скрывается
     */
    protected fun BottomBarProviderExt.resetActionButtonsVisibility(
        maxInitialVisibleButtonCount: Int = 0,
        hideTodayFab: Boolean = true
    ) {
        val buttons = getIconButtons()
        val maxVisibleCount = maxInitialVisibleButtonCount.coerceAtMost(buttons.size)
        val visibleButtons = buttons.filter { it.isVisible() }
        if (visibleButtons.size >= maxVisibleCount) {
            visibleButtons.take(visibleButtons.size - maxVisibleCount).forEach {
                it.hide()
            }
        }
        if (hideTodayFab) {
            hideTodayExtraFabButton(false)
        }
    }

    // region ContainerController
    override fun restore(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        configureNavigationItemHost(contentContainer, navigationItem.navxIdentifier)
        onSelectionChanged(navigationItem, isSelected = true, mainScreen, contentContainer)
    }

    override fun update(
        navigationItem: NavigationItem,
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) = Unit

    override fun activeTabClicked(navigationItem: NavigationItem, contentContainer: ContentContainer) {
        (fragmentInstallationStrategy.findContent(contentContainer) as? ActiveTabOnClickListener)
            ?.onActiveTabClicked(navigationItem)
    }

    override fun onItemVisibilityChanged(item: NavigationItem, isVisible: Boolean) = Unit

    override fun select(
        selectionInfo: ContentController.SelectionInfo,
        mainScreen: MainScreen,
        contentContainer: ContentContainer,
        transaction: FragmentTransaction
    ) {
        val screenInfo = createScreen(mainScreen, selectionInfo)
        (screenInfo.fragment as? NavigationItemHostScreen)?.navxId = selectionInfo.newSelectedItem.navxIdentifier

        fragmentInstallationStrategy.show(
            screenInfo.fragment,
            screenInfo.tag,
            selectionInfo,
            contentContainer,
            transaction,
            beforeTransactionAction = { contentContainer.bottomBarProvider.configureActionButtons(mainScreen) },
            onTransactionAction = {
                onSelectionChanged(selectionInfo.newSelectedItem, isSelected = true, mainScreen, contentContainer)
            },
            fragmentDiffCallback = FragmentInstallationStrategy.DefaultFragmentDiffCallback(
                fragmentUpdater = { _, selection ->
                    update(selection.newSelectedItem, selection.entryPoint, mainScreen, contentContainer)
                }
            )
        )
    }

    override fun selectSubScreen(
        navxId: NavxIdDecl,
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        val contentFragment = fragmentInstallationStrategy.findContent(contentContainer)
        val container = (contentFragment as? NavigationItemHostScreen)?.containerId
        if (container == null || container == View.NO_ID) return
        val screenInfo = mainScreen.screenEntries.find { it.id == navxId && it.isTab }
            ?.createScreen(entryPoint, mainScreen)
            ?: run {
                errorSafe("Cannot find MainScreen entry with id $navxId")
                return
            }
        contentFragment.childFragmentManager.beginTransaction()
            .replace(container, screenInfo.fragment, screenInfo.tag)
            .commit()
    }

    override fun reselect(
        navigationItem: NavigationItem,
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        // stub
    }

    override fun deselect(
        selectionInfo: ContentController.SelectionInfo,
        mainScreen: MainScreen,
        contentContainer: ContentContainer,
        transaction: FragmentTransaction
    ) {
        fragmentInstallationStrategy.hide(
            contentContainer,
            transaction,
            beforeTransactionAction = {
                resetActionButtons(contentContainer.bottomBarProvider)
                onSelectionChanged(selectionInfo.oldSelectedItem!!, isSelected = false, mainScreen, contentContainer)
            }
        )
    }

    override fun backPressed(
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ): Boolean {
        val currentFragment = fragmentInstallationStrategy.findContent(contentContainer)
        return if (currentFragment is FragmentBackPress) {
            currentFragment.onBackPressed()
        } else false
    }

    override fun start(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) = Unit

    override fun resume(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        if (shouldMakeVisibleBottomBarOnResume(navigationItem, mainScreen, contentContainer)) {
            contentContainer.scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE_SOFT)
        }
    }

    override fun pause(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) = Unit

    override fun stop(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) = Unit

    // endregion

    // region SideMenuSlideListener
    override fun onDrawerSlide(drawerView: View, slideOffset: Float, contentContainer: ContentContainer) {
        // do nothing
    }

    override fun onDrawerOpened(drawerView: View, contentContainer: ContentContainer) {
        contentContainer.drawerListener()?.onNavigationDrawerOpened()
    }

    override fun onDrawerClosed(drawerView: View, contentContainer: ContentContainer) {
        contentContainer.drawerListener()?.onNavigationDrawerClosed()
    }

    override fun onDrawerStateChanged(newState: Int, contentContainer: ContentContainer) {
        contentContainer.drawerListener()?.onNavigationDrawerStateChanged()
    }
    // endregion

    /**
     * Метод для проверки, нужно ли показывать ННП при переходе в состояние [Lifecycle.State.RESUMED].
     */
    protected open fun shouldMakeVisibleBottomBarOnResume(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ): Boolean = true

    private fun ContentContainer.drawerListener(): NavigationDrawerStateListener? =
        fragmentInstallationStrategy.findContent(this) as? NavigationDrawerStateListener

    private fun configureNavigationItemHost(contentContainer: ContentContainer, navxId: String) {
        (fragmentInstallationStrategy.findContent(contentContainer) as? NavigationItemHostScreen)?.navxId = navxId
    }

    private fun createScreen(mainScreen: MainScreen, selectionInfo: ContentController.SelectionInfo): ContentInfo {
        return mainScreen.screenEntries
            .find {
                /**
                 * Одного navxId недостаточно, т.к. они повторяются.
                 * На стороне MainScreenWidget контроллер выбирается корректный с помощью persistentUniqueIdentifier.
                 */
                val newSelectedItem = selectionInfo.newSelectedItem
                it.id == newSelectedItem.navxId
                    && !it.isTab
                    && it associatedWith newSelectedItem.persistentUniqueIdentifier
            }
            ?.createScreen(selectionInfo.entryPoint, mainScreen)
            ?: createScreen(selectionInfo, mainScreen)
    }

    /**
     * Информация по контенту
     */
    class ContentInfo(
        val fragment: Fragment,
        val tag: String = fragment::class.java.simpleName
    )
}