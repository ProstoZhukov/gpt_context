package ru.tensor.sbis.design_selection

import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionMenu
import ru.tensor.sbis.communication_decl.selection.SelectionMenuConfig
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design_selection.contract.SelectionDependenciesFactory
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.menu.SelectionMenuFragment
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl

/**
 * Фабрика для создания фрагмента меню компонента выбора.
 *
 * @author vv.chekurda
 */
object SelectionMenuFragmentFactory {

    /**
     * Создать инстанс фрагмента меню компонента выбора.
     *
     * @param config конфигурация компонента.
     * @param dependenciesProvider поставщик зависимостей компонента.
     */
    fun <I : SelectionItem, C : SelectionConfig, MC : SelectionMenuConfig<C>> createSelectionMenuFragment(
        config: MC,
        dependenciesProvider: SelectionDependenciesFactory.Provider<I, C>,
        @AttrRes themeAttr: Int = R.attr.selectionMenuTheme,
        @StyleRes defTheme: Int = R.style.SelectionMenuTheme
    ): SelectionMenu {
        val contentCreator = Creator(
            config = config.selectionConfig,
            dependenciesProvider = dependenciesProvider,
            themeAttr = themeAttr,
            defTheme = defTheme
        )
        return SelectionMenuFragment.Builder()
            .setSelectionContentCreator(contentCreator)
            .setAutoHideEmptyMenu(config.autoHideEmptyMenu)
            .setIgnoreWindowInsets(config.ignoreWindowInsets)
            .setShowStubs(config.showStubs)
            .setShowLoaders(config.showLoaders)
            .setCloseOnShadowClick(config.closable)
            .setAutoCloseable(config.closable)
            .hideKeyboardOnStart(false)
            .setIgnoreLock(true)
            .instant(false)
            .setPeekHeightParams(
                listOf(
                    ContainerMovableDelegateImpl.PeekHeightParams(
                        type = ContainerMovableDelegateImpl.PeekHeightType.HIDDEN,
                        peekHeight = MovablePanelPeekHeight.Absolute(0)
                    ),
                    ContainerMovableDelegateImpl.PeekHeightParams(
                        type = ContainerMovableDelegateImpl.PeekHeightType.INIT,
                        peekHeight = MovablePanelPeekHeight.Dimen(R.dimen.selection_menu_init_height)
                    ),
                    ContainerMovableDelegateImpl.PeekHeightParams(
                        type = ContainerMovableDelegateImpl.PeekHeightType.EXPANDED,
                        peekHeight = MovablePanelPeekHeight.FitToContent()
                    ),
                )
            )
            .build()
    }

    @Parcelize
    private class Creator<ITEM : SelectionItem, CONFIG : SelectionConfig>(
        private val config: CONFIG,
        private val dependenciesProvider: SelectionDependenciesFactory.Provider<ITEM, CONFIG>,
        @AttrRes private val themeAttr: Int = R.attr.selectionTheme,
        @StyleRes private val defTheme: Int = R.style.SelectionTheme
    ) : ContentCreatorParcelable {

        override fun createFragment(): Fragment =
            SelectionFragmentFactory.createSelectionFragment(
                config = config,
                dependenciesProvider = dependenciesProvider,
                themeAttr = themeAttr,
                defTheme = defTheme
            )
    }
}