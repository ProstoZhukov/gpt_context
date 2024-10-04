package ru.tensor.sbis.design_selection.ui.menu.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.selection.SelectionMenuDelegate
import ru.tensor.sbis.design_selection.contract.listeners.SelectionDelegate
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl.PeekHeightType

/**
 * Вью-модель меню выбора.
 *
 * @author vv.chekurda
 */
internal class SelectionMenuViewModel :
    ViewModel(),
    SelectionMenuDelegate {

    private val _menuPeekHeightType = MutableStateFlow(PeekHeightType.HIDDEN)
    private val _isShowingState = MutableStateFlow(false)

    private lateinit var selectionDelegate: SelectionDelegate

    override val searchQuery: MutableStateFlow<String>
        get() = selectionDelegate.searchQuery
    override val hasSelectableItems: StateFlow<Boolean>
        get() = selectionDelegate.hasSelectableItems

    override val isShowingState: StateFlow<Boolean> = _isShowingState

    val menuPeekHeightType: StateFlow<PeekHeightType> = _menuPeekHeightType

    private var isShowingStateRequested = false

    init {
        viewModelScope.launch(Dispatchers.Main) {
            hasSelectableItems.collect { hasItems ->
                when {
                    !isShowingStateRequested -> {
                        return@collect
                    }
                    !hasItems -> {
                        changePeekHeightType(PeekHeightType.HIDDEN)
                    }
                    isShowingState.value && menuPeekHeightType.value == PeekHeightType.HIDDEN -> {
                        changePeekHeightType(PeekHeightType.EXPANDED)
                    }
                }
            }
        }
    }

    override fun show() {
        isShowingStateRequested = true
        _isShowingState.compareAndSet(expect = false, update = true)
        changePeekHeightType(PeekHeightType.INIT)
    }

    override fun hide() {
        isShowingStateRequested = false
        changePeekHeightType(PeekHeightType.HIDDEN)
    }

    private fun changePeekHeightType(peekHeightType: PeekHeightType) {
        val changed = _menuPeekHeightType.compareAndSet(expect = _menuPeekHeightType.value, update = peekHeightType)
        if (changed && peekHeightType == PeekHeightType.INIT && ::selectionDelegate.isInitialized) {
            selectionDelegate.resetScroll()
        }
    }

    fun setSelectionDelegate(delegate: SelectionDelegate) {
        selectionDelegate = delegate
    }

    fun onMenuStateChanged(isShowing: Boolean) {
        if (isShowingStateRequested) return

        val isChanged = _isShowingState.compareAndSet(expect = !isShowing, update = isShowing)
        val newState = if (isShowing) PeekHeightType.INIT else PeekHeightType.HIDDEN
        _menuPeekHeightType.compareAndSet(expect = _menuPeekHeightType.value, update = newState)

        if (isChanged && !isShowing) {
            selectionDelegate.apply {
                closeAllFolders()
                searchQuery.tryEmit(StringUtils.EMPTY)
            }
        }
    }
}