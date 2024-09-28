package ru.tensor.sbis.logging.log_packages.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.app_file_browser.feature.AppFileBrowserFeature
import ru.tensor.sbis.logging.R
import javax.inject.Inject

/**
 * Роутер экрана отображения списка логов.
 */
class LogPackageRouterImpl @Inject constructor(
    private val fragment: Fragment,
    private val appFileBrowserFeature: AppFileBrowserFeature
) : LogPackageRouter {

    /**
     * Live-data открытости файлового браузера.
     */
    override val fileBrowserOpenedLiveData: LiveData<Boolean>
        get() = _fileBrowserOpenedLiveData

    private var _fileBrowserOpenedLiveData = MutableLiveData<Boolean>()

    init {
        appFileBrowserFeature.panelCloseEvent.observe(fragment) {
            _fileBrowserOpenedLiveData.value = false
        }
    }

    /**
     * Показать файловый браузер.
     */
    override fun showAppFileBrowser() {
        _fileBrowserOpenedLiveData.value = true
        appFileBrowserFeature.show(
            fragment.requireContext(),
            fragment.childFragmentManager,
            R.id.logging_file_browser_container
        )
    }

    override fun showConfirmationDialog(totalSize: String) {
        SendLogDialogFragment.newInstance(totalSize).show(fragment.childFragmentManager, null)
    }

    /** @SelfDocumented */
    override fun backPressed() {
        fragment.requireActivity().onBackPressed()
    }
}