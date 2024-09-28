package ru.tensor.sbis.application_tools.logcrashesinfo

import ru.tensor.sbis.application_tools.base.BasePresenter
import ru.tensor.sbis.application_tools.logcrashesinfo.utils.CrashAnalyticsHelper

/**
 * @author du.bykov
 *
 * @SelfDocumented
 */
class LogAndCrashesPresenter(private val crashAnalyticsHelper: CrashAnalyticsHelper) :
    BasePresenter<LogAndCrashesFragment> {

    private var mView: LogAndCrashesFragment? = null

    fun onCrashesButtonClicked() {
        mView?.showCrashesInfoListFragment()
    }

    fun onLogsButtonClicked() {
        mView?.showLogsInfoListFragment()
    }

    fun onClearButtonClicked() {
        mView?.showClearDialogFragment()
    }

    fun deleteCrashes() {
        crashAnalyticsHelper.deleteCrashes()
    }

    fun deleteAll() {
        deleteCrashes()
    }

    override fun attachView(view: LogAndCrashesFragment) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun onDestroy() = Unit
}