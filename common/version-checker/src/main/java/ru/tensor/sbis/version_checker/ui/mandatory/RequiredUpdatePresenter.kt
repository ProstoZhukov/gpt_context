package ru.tensor.sbis.version_checker.ui.mandatory

import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.version_checker.VersionCheckerPlugin
import ru.tensor.sbis.version_checker.analytics.Analytics
import ru.tensor.sbis.version_checker.analytics.AnalyticsEvent
import ru.tensor.sbis.version_checker.analytics.AnalyticsEvent.ClickCriticalUpdate
import ru.tensor.sbis.version_checker.data.UpdateCommand
import ru.tensor.sbis.version_checker.di.AppName
import ru.tensor.sbis.version_checker.di.VersioningFragmentScope
import ru.tensor.sbis.version_checker.domain.source.UpdateCommandFactory
import javax.inject.Inject

/**
 * Презентер экрана принудительного обновления.
 *
 * @author as.chadov
 */
@VersioningFragmentScope
internal class RequiredUpdatePresenter @Inject constructor(
    @AppName private val applicationName: String,
    private val commandFactory: UpdateCommandFactory,
    private val analytics: Analytics
) : RequiredUpdateContract.Presenter {

    private var mView: RequiredUpdateContract.View? = null

    override fun attachView(view: RequiredUpdateContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun onDestroy() = Unit

    override fun onAcceptUpdate() =
        commandFactory.create(false) { command: UpdateCommand, hasGooglePlay: Boolean ->
            val updateSource = mView?.runCommand(command)
            analytics.send(ClickCriticalUpdate(), updateSource, hasGooglePlay)
        }

    override fun getAppName(): String = applicationName

    override fun getButtonStyle(): SbisButtonStyle = VersionCheckerPlugin.customizationOptions.buttonStyle

    override fun sendAnalytics() {
        analytics.send(AnalyticsEvent.ShowCriticalScreen())
    }
}