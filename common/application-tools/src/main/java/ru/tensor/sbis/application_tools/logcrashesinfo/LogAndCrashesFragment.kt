package ru.tensor.sbis.application_tools.logcrashesinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.base.BasePresenterFragment
import ru.tensor.sbis.application_tools.logcrashesinfo.logandcrasheslist.LogAndCrashesListFragment
import ru.tensor.sbis.application_tools.logcrashesinfo.utils.CrashAnalyticsHelper
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design_dialogs.fragment.AlertDialogFragment

/**
 * @author du.bykov
 *
 * Экран отображения данных о логах и крашах.
 */
class LogAndCrashesFragment : BasePresenterFragment<LogAndCrashesFragment, LogAndCrashesPresenter>(),
    View.OnClickListener,
    AlertDialogFragment.YesNoListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView = inflater.inflate(
            R.layout.application_tools_base_log_and_crashes_info_fragment,
            container,
            false
        )
        mainView.findViewById<View>(R.id.crashes_btn).setOnClickListener(this)
        mainView.findViewById<View>(R.id.logs_btn).setOnClickListener(this)
        mainView.findViewById<View>(R.id.clear_btn).setOnClickListener(this)
        mainView.findViewById<SbisTopNavigationView>(R.id.sbisToolbar)?.run {
            content = SbisTopNavigationContent.SmallTitle(
                title = PlatformSbisString.Res(R.string.application_tools_settings_log_crashes_title)
            )
            backBtn?.setOnClickListener { requireActivity().onBackPressed() }
        }
        return mainView
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.crashes_btn -> {
                presenter.onCrashesButtonClicked()
            }
            R.id.logs_btn -> {
                presenter.onLogsButtonClicked()
            }
            R.id.clear_btn -> {
                presenter.onClearButtonClicked()
            }
        }
    }

    private fun showFragment(fragment: LogAndCrashesListFragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container_frame, fragment, LogAndCrashesListFragment::class.java.canonicalName)
            .addToBackStack(null)
            .commit()
    }

    fun showCrashesInfoListFragment() {
        showFragment(LogAndCrashesListFragment.newInstance(R.string.application_tools_crashes_list_title))
    }

    fun showLogsInfoListFragment() {
        showFragment(LogAndCrashesListFragment.newInstance(R.string.application_tools_logs_list_title))
    }

    fun showClearDialogFragment() {
        val dialogFragmentArray = arrayOf(
            getString(R.string.application_tools_clear_crashes),
            getString(R.string.application_tools_clear_logs),
            getString(
                R.string.application_tools_clear_all
            )
        )
        val dialogFragment = AlertDialogFragment.newInstance(
            DIALOG_CODE_CLEAR, "", ArrayList(listOf(*dialogFragmentArray))
        )
        dialogFragment.show(
            childFragmentManager,
            AlertDialogFragment::class.java.simpleName
        )
    }

    override fun onYes(dialogCode: Int) = Unit

    override fun onNo(dialogCode: Int) = Unit

    override fun onItem(dialogCode: Int, which: Int) {
        when (which) {
            DELETE_CRASHES_INTEGER_VALUE -> presenter.deleteCrashes()
            DELETE_ALL_INTEGER_VALUE -> presenter.deleteAll()
        }
    }

    override fun inject() {
        //ignore
    }

    override fun createPresenter(): LogAndCrashesPresenter {
        return LogAndCrashesPresenter(CrashAnalyticsHelper(requireContext().applicationContext))
    }

    override fun getPresenterView() = this
}

private const val DELETE_CRASHES_INTEGER_VALUE = 0
private const val DELETE_ALL_INTEGER_VALUE = 2
private const val DIALOG_CODE_CLEAR = 1515