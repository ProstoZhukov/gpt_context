package ru.tensor.sbis.design.files_picker.view.ui

import android.content.Context
import android.view.View
import androidx.annotation.IntRange
import androidx.core.view.isVisible
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.files_picker.R
import ru.tensor.sbis.design.files_picker.databinding.FilesPickerV2FragmentBinding
import ru.tensor.sbis.design.tab_panel.TabPanelItem
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegate

/**
 * MVI View для экрана "Компонент выбора файлов".
 *
 * @author ai.abramenko
 */
internal interface FilesPickerView : MviView<FilesPickerView.ViewModel, FilesPickerView.Event> {

    /**
     * Фабрика [FilesPickerView].
     */
    fun interface Factory : (View) -> FilesPickerView

    /**
     * UI события от [FilesPickerView].
     */
    sealed interface Event {

        object OnCancelButtonClick : Event

        object OnAddButtonClick : Event

        data class OnMenuButtonClick(
            val verticalLocator: AnchorVerticalLocator,
            val horizontalLocator: AnchorHorizontalLocator
        ) : Event

        data class OnTabSelected(val tabPanelItem: TabPanelItem) : Event
    }

    /**
     * UI модель для [FilesPickerView].
     */
    data class ViewModel(
        val tabPanelItems: List<TabPanelItem>,
        val selectedTabPanelItem: TabPanelItem,
        val headerViewModel: HeaderViewModel,
        val footerViewModel: FooterViewModel
    )

    data class HeaderViewModel(
        val isVisible: Boolean,
        val isCounterVisible: Boolean,
        @IntRange(0L, Long.MAX_VALUE) val selectedCounter: Int,
        val isMenuButtonVisible: Boolean,
        val backgroundColor: SbisColor
    )

    data class FooterViewModel(
        val isVisible: Boolean,
        val state: State
    ) {
        enum class State {
            TAB_PANEL,
            ADD_BUTTON
        }
    }
}

/**
 * Реализация MVI View для экрана "Компонент выбора файлов".
 *
 * @author ai.abramenko
 */
internal class FilesPickerViewImpl(
    view: View,
    containerMovableDelegate: ContainerMovableDelegate?
) : BaseMviView<FilesPickerView.ViewModel, FilesPickerView.Event>(),
    FilesPickerView {

    private val binding: FilesPickerV2FragmentBinding = FilesPickerV2FragmentBinding.bind(view)

    private val context: Context
        get() = binding.root.context

    init {
        with(binding) {
            filesPickerHeaderCancelBtn.dispatchOnClick(FilesPickerView.Event.OnCancelButtonClick)
            filesPickerAddBtn.dispatchOnClick(FilesPickerView.Event.OnAddButtonClick)
            val menuVerticalLocator = AnchorVerticalLocator(
                alignment = VerticalAlignment.TOP,
                innerPosition = true
            )
            val menuHorizontalLocator = AnchorHorizontalLocator(
                alignment = HorizontalAlignment.RIGHT,
                innerPosition = true
            )
            menuVerticalLocator.anchorView = binding.filesPickerHeaderMoreBtn
            menuHorizontalLocator.anchorView = binding.filesPickerHeaderMoreBtn
            filesPickerHeaderMoreBtn.dispatchOnClick(
                FilesPickerView.Event.OnMenuButtonClick(menuVerticalLocator, menuHorizontalLocator)
            )
            designTabPanelViewId.setClickItemHandler {
                dispatch(FilesPickerView.Event.OnTabSelected(it))
            }
        }
    }

    override val renderer: ViewRenderer<FilesPickerView.ViewModel> = diff {
        with(binding) {
            diff(
                get = FilesPickerView.ViewModel::tabPanelItems,
                set = {
                    designTabPanelViewId.setTabPanelItems(it.toList())
                    designTabPanelViewId.isVisible = it.size > 1
                }
            )
            diff(
                get = FilesPickerView.ViewModel::selectedTabPanelItem,
                set = { designTabPanelViewId.setSelectedItem(it) }
            )
            diff(
                get = FilesPickerView.ViewModel::headerViewModel,
                set = { headerViewModel ->
                    filesPickerHeaderContainer.isVisible = headerViewModel.isVisible
                    filesPickerHeaderSelectedFilesText.isVisible = headerViewModel.isCounterVisible
                    filesPickerHeaderSelectedFilesText.text =
                        root.resources.getString(
                            R.string.files_picker_panel_header_title,
                            headerViewModel.selectedCounter
                        )
                    filesPickerHeaderMoreBtn.isVisible = headerViewModel.isMenuButtonVisible
                    val headerColor = headerViewModel.backgroundColor.getColor(context)
                    filesPickerHeaderContainer.setBackgroundColor(headerColor)
                    containerMovableDelegate?.changeBackground(headerColor)
                }
            )
            diff(
                get = FilesPickerView.ViewModel::footerViewModel,
                set = { footerViewModel ->
                    filesPickerFooterContainer.isVisible = footerViewModel.isVisible
                    filesPickerTabPanelContainer.isVisible =
                        footerViewModel.state == FilesPickerView.FooterViewModel.State.TAB_PANEL
                    filesPickerAddBtnContainer.isVisible =
                        footerViewModel.state == FilesPickerView.FooterViewModel.State.ADD_BUTTON
                }
            )
        }
    }

    private fun View.dispatchOnClick(event: FilesPickerView.Event) {
        setOnClickListener { dispatch(event) }
    }
}