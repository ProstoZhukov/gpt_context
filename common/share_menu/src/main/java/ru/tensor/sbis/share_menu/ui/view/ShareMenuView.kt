package ru.tensor.sbis.share_menu.ui.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.theme.res.SbisString
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_notification.toast.showToast
import ru.tensor.sbis.share_menu.databinding.ShareMenuFragmentBinding
import ru.tensor.sbis.share_menu.ui.data.ShareMenuTabItem
import ru.tensor.sbis.share_menu.ui.data.ShareMenuTabsData
import ru.tensor.sbis.share_menu.ui.view.header.ShareHeaderViewState
import ru.tensor.sbis.share_menu.ui.view.ShareMenuView.Event
import ru.tensor.sbis.share_menu.ui.view.ShareMenuView.Model
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuHeightMode

/**
 * MVI-View меню для "поделиться".
 *
 * @author vv.chekurda
 */
internal class ShareMenuView(
    private val fragment: Fragment,
    view: View
) : BaseMviView<Model, Event>() {

    /**
     * UI состояние.
     *
     * @property headerState состояние шапки.
     * @property isBackButtonVisible признак видимости кнопки назад в шапке.
     * @property isTabPanelVisible признак видимости панели с навигационными вкладками.
     * @property filesCount количество файлов, которые шарятся.
     * @property tabsData данные о вкладках для навигационной панели.
     * @property heightMode режим измерения высоты контейнера контента.
     */
    internal data class Model(
        val headerState: ShareHeaderViewState,
        val isBackButtonVisible: Boolean,
        val isTabPanelVisible: Boolean,
        val filesCount: Int,
        val tabsData: ShareMenuTabsData,
        val heightMode: ShareMenuHeightMode
    )

    /**
     * События экрана.
     */
    sealed interface Event {
        /** Нажатие на кнопку назад. */
        object OnBackButtonClicked : Event
        /** Нажатие на кнопку закрыть. */
        object OnCloseButtonClicked : Event
        /** Пользователь выбрал вкладку. */
        class OnTabSelected(val item: ShareMenuTabItem) : Event
        /** Изменилась высота панели вкладок. */
        class OnTabPanelHeightChanged(val height: Int) : Event
    }

    private val binding = ShareMenuFragmentBinding.bind(view)
    private val context: Context
        get() = binding.root.context
    private lateinit var model: Model

    private val isTabletPort = DeviceConfigurationUtils.isTablet(context)
        && !DeviceConfigurationUtils.isLandscape(context)

    override val renderer: ViewRenderer<Model> = diff {
        diff(
            get = Model::isBackButtonVisible,
            set = { binding.shareMenuHeaderView.setBackButtonVisibility(model.isBackButtonVisible) }
        )
        diff(
            get = Model::headerState,
            set = { binding.shareMenuHeaderView.onStateChanged(model.headerState, model.filesCount) }
        )
        diff(
            get = Model::isTabPanelVisible,
            set = { binding.shareMenuTabPanelContainer.isVisible = model.isTabPanelVisible }
        )
        diff(
            get = Model::tabsData,
            set = {
                binding.designTabPanelViewId.setTabPanelItems(model.tabsData.items)
                model.tabsData.selected?.also(binding.designTabPanelViewId::setSelectedItem)
            }
        )
        diff(
            get = Model::heightMode,
            set = { mode ->
                when (mode) {
                    ShareMenuHeightMode.Full -> {
                        if (!isTabletPort) updateRootContainerHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                    ShareMenuHeightMode.Short -> {
                        updateRootContainerHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    }
                }
                (fragment.parentFragment as? Container.Resizable)?.changeHeightParams(
                    wrapContent = isTabletPort || mode == ShareMenuHeightMode.Short
                )
            }
        )
    }

    init {
        with(binding) {
            shareMenuHeaderView.setOnBackListener {
                dispatch(Event.OnBackButtonClicked)
            }
            shareMenuHeaderView.setOnCloseListener {
                dispatch(Event.OnCloseButtonClicked)
            }
            designTabPanelViewId.setClickItemHandler { selectedTab ->
                dispatch(Event.OnTabSelected(selectedTab as ShareMenuTabItem))
            }
            designTabPanelViewId.addOnLayoutChangeListener { _, _, top, _, bottom, _, _, _, _ ->
                dispatch(Event.OnTabPanelHeightChanged(bottom - top))
            }
        }
    }

    override fun render(model: Model) {
        val isInitialized = this::model.isInitialized
        this.model = model
        if (!isInitialized) onModelInitialized()
        super.render(model)
    }

    private fun onModelInitialized() {
        if (isTabletPort) {
            fun updateTabletPortFullHeight(heightMode: ShareMenuHeightMode) {
                if (heightMode !is ShareMenuHeightMode.Full) return
                val decorViewHeight = fragment.requireActivity().window.decorView.height
                updateRootContainerHeight((decorViewHeight * TABLET_PORT_CONTAINER_HEIGHT_FACTOR).toInt())
            }

            updateTabletPortFullHeight(model.heightMode)
            fragment.requireActivity().window.decorView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateTabletPortFullHeight(this.model.heightMode)
            }
        }
    }

    private fun updateRootContainerHeight(height: Int) {
        binding.root.updateLayoutParams<ViewGroup.LayoutParams> { this.height = height }
    }

    /**
     * Показать сообщение об ошибке.
     */
    fun showErrorMessage(message: SbisString) {
        fragment.showToast(message.getCharSequence(context))
    }

    /**
     * Показать контейнер меню.
     */
    fun showMenuContainer() {
        (fragment.parentFragment as? Container.Showable)?.showContent()
    }
}

private const val TABLET_PORT_CONTAINER_HEIGHT_FACTOR = 2 / 3f