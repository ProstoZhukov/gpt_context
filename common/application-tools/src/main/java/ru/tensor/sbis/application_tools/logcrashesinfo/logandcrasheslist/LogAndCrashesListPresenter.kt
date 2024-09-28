package ru.tensor.sbis.application_tools.logcrashesinfo.logandcrasheslist

import androidx.annotation.StringRes
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.base.BasePresenter
import ru.tensor.sbis.application_tools.debuginfo.model.DebugInfo
import ru.tensor.sbis.application_tools.logcrashesinfo.utils.CrashAnalytics

/**
 * @author du.bykov
 *
 * @SelfDocumented
 */
class LogAndCrashesListPresenter : BasePresenter<LogAndCrashesListFragment> {

    private var mView: LogAndCrashesListFragment? = null

    private val crashes: ArrayList<DebugInfo>
        get() {
            val crashes = CrashAnalytics.allCrashes
            val crashesInfo = ArrayList<DebugInfo>(if (crashes.isEmpty()) 1 else crashes.size)
            for (crash in crashes) {
                crashesInfo.add(DebugInfo(crash))
            }
            return crashesInfo
        }

    fun init(@StringRes mKey: Int) {
        if (mKey == R.string.application_tools_crashes_list_title) {
            showCrashesData()
        } else if (mKey == R.string.application_tools_logs_list_title) {
            showLogsData()
        }
    }

    private fun showCrashesData() {
        mView?.showData(
            crashes,
            true
        )
    }

    private fun showLogsData() {
        mView?.showData(
            ArrayList(),
            false
        )
    }

    override fun attachView(view: LogAndCrashesListFragment) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun onDestroy() {}
}
