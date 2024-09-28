package ru.tensor.sbis.version_checker.ui.recommended

import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.version_checker.VersionCheckerPlugin
import ru.tensor.sbis.version_checker.analytics.Analytics
import ru.tensor.sbis.version_checker.analytics.AnalyticsEvent
import ru.tensor.sbis.version_checker.data.UpdateCommand
import ru.tensor.sbis.version_checker.di.AppName
import ru.tensor.sbis.version_checker.di.VersioningFragmentScope
import ru.tensor.sbis.version_checker.domain.cache.VersioningLocalCache
import ru.tensor.sbis.version_checker.domain.source.UpdateCommandFactory
import javax.inject.Inject

/**
 * Презентер экрана предложения обновления
 *
 * @author as.chadov
 */
@VersioningFragmentScope
internal class RecommendedUpdatePresenter @Inject constructor(
    @AppName private val applicationName: String,
    private val commandFactory: UpdateCommandFactory,
    private val localCache: VersioningLocalCache,
    private val analytics: Analytics
) : RecommendedUpdateContract.Presenter {

    private var mView: RecommendedUpdateContract.View? = null
    private var skipped = false

    override fun attachView(view: RecommendedUpdateContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun onDestroy() = Unit

    override fun onAcceptUpdate() =
        commandFactory.create(true) { command: UpdateCommand, hasGooglePlay: Boolean ->
            val updateSource = mView?.runCommand(command)
            analytics.send(AnalyticsEvent.ClickRecommendedUpdate(), updateSource, hasGooglePlay)
        }

    override fun onPostponeUpdate(postponedByButton: Boolean) =
        localCache.postponeUpdateRecommendation(postponedByButton)

    override fun skipNextRecommendation() {
        if (!skipped) {
            localCache.postponeUpdateRecommendation(false)
        }
        skipped = true
    }

    override fun getAppName(): String = applicationName

    override fun getButtonStyle(): SbisButtonStyle = VersionCheckerPlugin.customizationOptions.buttonStyle
}
