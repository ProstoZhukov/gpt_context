package ru.tensor.sbis.application_tools.logcrashesinfo.crashes

import ru.tensor.sbis.application_tools.base.BasePresenter
import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models.Crash
import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models.CrashViewModel
import ru.tensor.sbis.application_tools.logcrashesinfo.utils.CrashAnalyticsHelper

/**
 * @author du.bykov
 *
 * @SelfDocumented
 */
class CrashInfoPresenter(private val cashAnalyticsHelper: CrashAnalyticsHelper) : BasePresenter<CrashInfoFragment> {

    private var mView: CrashInfoFragment? = null
    private var crashName: String? = null
    private var mCrashPosition: Int = 0

    private val crash = if (crashName != null) {
        cashAnalyticsHelper.getCrashByName(crashName!!)
    } else {
        cashAnalyticsHelper.getCrashByPosition(mCrashPosition)
    }

    override fun attachView(view: CrashInfoFragment) {
        mView = view
        setCrashInfoView(crash)
    }

    override fun detachView() {
        mView = null
    }

    override fun onDestroy() = Unit

    private fun setCrashInfoView(crash: Crash) {
        val crashViewModel = CrashViewModel(crash)
        mView?.setCrashInfo(crashViewModel)
    }

    fun onShareButtonClicked() {
        mView?.openSendApplicationChooser(cashAnalyticsHelper.fileToShare)
    }

    fun setCrashPosition(crashPosition: Int) {
        mCrashPosition = crashPosition
    }

    fun setCrashName(crashName: String) {
        this.crashName = crashName
    }
}