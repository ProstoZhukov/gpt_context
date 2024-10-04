package ru.tensor.sbis.design.files_picker.view.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabClickAction
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.tab_panel.TabPanelItem
import ru.tensor.sbis.design.theme.res.SbisColor

/**
 * MVI Store для экрана "Компонент выбора файлов".
 *
 * @author ai.abramenko
 */
internal interface FilesPickerStore : Store<FilesPickerStore.Intent, FilesPickerStore.State, FilesPickerStore.Label> {

    /**
     * MVI Intent для экрана "Компонент выбора файлов".
     *
     * @author ai.abramenko
     */
    sealed interface Intent {

        object OnCancelButtonClick : Intent

        object OnAddButtonClick : Intent

        class OnMenuButtonClick(
            val verticalLocator: AnchorVerticalLocator,
            val horizontalLocator: AnchorHorizontalLocator
        ) : Intent

        class OnTabPanelItemSelected(val tabPanelItem: TabPanelItem) : Intent
    }

    /**
     * MVI Label для экрана "Компонент выбора файлов".
     *
     * @author ai.abramenko
     */
    sealed interface Label {

        object Close : Label

        class ShowMenu(
            val menu: SbisMenu,
            val verticalLocator: AnchorVerticalLocator,
            val horizontalLocator: AnchorHorizontalLocator
        ) : Label

        class ShowTabScreen(val clickAction: SbisFilesPickerTabClickAction) : Label
    }

    /**
     * MVI Message для экрана "Компонент выбора файлов".
     *
     * @author ai.abramenko
     */
    sealed interface Message {

        class SetSelectedTabPanelItem(val tabPanelItem: TabPanelItem) : Message

        class SetSelectedPickedItems(
            val selectedPickedItems: List<SbisPickedItem>,
            val isCompressImages: Boolean
        ) : Message

        class SetControlsVisible(val isVisible: Boolean) : Message

        class SetMenuVisible(val isVisible: Boolean) : Message

        class SetAppliedHeaderColor(val color: SbisColor) : Message
    }

    /**
     * MVI Action для экрана "Компонент выбора файлов".
     *
     * @author ai.abramenko
     */
    sealed interface Action {

        object InitSelectedTab : Action
    }

    /**
     * MVI State для экрана "Компонент выбора файлов".
     *
     * @author ai.abramenko
     */
    @Parcelize
    data class State(
        val selectedPickedItems: List<SbisPickedItem>,
        val isCompressImages: Boolean,
        val tabPanelItems: List<TabPanelItem>,
        val selectedTabPanelItem: TabPanelItem,
        val isControlsVisible: Boolean,
        val isMenuVisible: Boolean,
        val appliedHeaderColor: SbisColor
    ) : Parcelable
}