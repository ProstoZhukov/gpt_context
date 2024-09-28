package ru.tensor.sbis.app_file_browser.feature

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.tensor.sbis.app_file_browser.fragment.AppFileBrowserContentCreator
import ru.tensor.sbis.app_file_browser.util.getThemedContext
import ru.tensor.sbis.common.util.SingleLiveEvent
import ru.tensor.sbis.common.util.hasFragmentOrPendingTransaction
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.mfb.generated.MobileFileController
import ru.tensor.sbis.mfb.generated.MobileFileControllerProvider
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableFragment

/**
 * Реализация фичи файлового браузера.
 *
 * @author us.bessonov
 */
internal class AppFileBrowserFeatureImpl : ViewModel(), AppFileBrowserFeatureInternal {

    private var _controller: MobileFileController? = null
    private val _selectedFiles = SingleLiveEvent<Set<String>>()
    private val _panelCloseEvent = SingleLiveEvent<Unit>()
    override val controller: MobileFileController
        get() = _controller ?: run {
            _controller = MobileFileControllerProvider.instance().get()
            _controller!!
        }
    override val selectedFiles: LiveData<Set<String>> = _selectedFiles
    override val panelCloseEvent: LiveData<Unit> = _panelCloseEvent

    override fun show(context: Context, fragmentManager: FragmentManager, containerViewId: Int) {
        if (fragmentManager.hasFragmentOrPendingTransaction(FRAGMENT_TAG)) return

        fragmentManager.beginTransaction()
            .add(containerViewId, createFragment(context), FRAGMENT_TAG)
            .addToBackStack(FRAGMENT_TAG)
            .commit()
    }

    override fun onSelectionChanged(selectedFiles: Set<String>) {
        _selectedFiles.value = selectedFiles
    }

    override fun onPanelClosed() {
        _panelCloseEvent.value = Unit
    }

    override fun getSelectedTotalSize() = controller.getSelectedTotalSize()

    override fun reset() {
        _selectedFiles.value = emptySet()
        _controller = MobileFileControllerProvider.instance().get()
    }

    private fun createFragment(context: Context): Fragment {
        val containerBackground = getThemedContext(context)
            .getThemeColorInt(R.attr.unaccentedBackgroundColor)
        return ContainerMovableFragment.Builder()
            .instant(true)
            .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
            .setContentCreator(AppFileBrowserContentCreator())
            .setContainerBackgroundColor(containerBackground)
            .build().apply { stateCallback = { if (!it) onPanelClosed() } }
    }
}

private const val FRAGMENT_TAG = "APP_FILE_BROWSER"