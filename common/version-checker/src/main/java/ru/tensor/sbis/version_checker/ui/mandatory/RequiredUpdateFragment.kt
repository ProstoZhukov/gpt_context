package ru.tensor.sbis.version_checker.ui.mandatory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.stubview.ResourceImageStubContent
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment
import ru.tensor.sbis.version_checker.R
import ru.tensor.sbis.version_checker.VersionCheckerPlugin
import ru.tensor.sbis.version_checker.data.UpdateCommand
import ru.tensor.sbis.version_checker.ui.utils.GradientShaderFactory
import ru.tensor.sbis.version_checker_decl.VersionedComponent
import javax.inject.Inject

/**
 * Фрагмент отображения Принудительного обновления.
 * Используется только из [RequiredUpdateActivity].
 */
internal class RequiredUpdateFragment :
    BasePresenterFragment<RequiredUpdateContract.View, RequiredUpdateContract.Presenter>(),
    RequiredUpdateContract.View,
    VersionedComponent {

    @Inject
    lateinit var requiredPresenter: RequiredUpdatePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState?.containsKey(ANALYTICS_ARGS) == false) {
            presenter.sendAnalytics()
        }
    }

    override fun inject() = VersionCheckerPlugin
        .versioningComponent
        .requiredComponentFactory()
        .inject(this)

    override fun getPresenterView(): RequiredUpdateContract.View = this

    override fun createPresenter(): RequiredUpdateContract.Presenter = requiredPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = applyThemeIfNeeded(inflater).inflate(
            R.layout.versioning_fragment_required_update,
            container,
            false
        )
        view.setContent()
        view.findViewById<View>(R.id.versioning_version_btn_accept)?.setOnClickListener {
            presenter.onAcceptUpdate()
        }
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ANALYTICS_ARGS, true)
    }

    override fun runCommand(command: UpdateCommand) = context?.let(command::run)

    private fun View.setContent() {
        findViewById<StubView>(R.id.versioning_version_stub_view)?.setContent(
            ResourceImageStubContent(
                icon = R.drawable.versioning_update_drawable,
                messageRes = R.string.versioning_update_required_title,
                details = resources.getString(R.string.versioning_update_required_details, presenter.getAppName())
            )
        )
        findViewById<SbisButton>(R.id.versioning_version_btn_accept).style = presenter.getButtonStyle()
        findViewById<View>(R.id.versioning_required_update_content)?.background =
            GradientShaderFactory.createBrandGradient(context)
    }

    private fun applyThemeIfNeeded(inflater: LayoutInflater): LayoutInflater {
        /*
         * Для приложений без фиксированной базовой темы в AndroidManifest.xml - не требуется преднастройка
         * LayoutInflater-a, т.к. в активити родителя (частный случай 'ForcedUpdateActivity.kt') уже будет
         * установлена необходимая тема 'VersioningUpdateTheme'.
         */
        val overrideThemeApplication = VersionCheckerPlugin.customizationOptions.overrideThemeApplication

        return if (overrideThemeApplication) {
            inflater
        } else {
            inflater.cloneInContext(
                ThemeContextBuilder(
                    requireContext(),
                    R.attr.versioningTheme,
                    R.style.VersioningUpdateTheme
                ).build()
            )
        }
    }

    companion object {
        const val TAG = "RequiredUpdateFragment"
        private const val ANALYTICS_ARGS = "analytics_args"

        /** @SelfDocumented */
        @JvmStatic
        fun newInstance() = RequiredUpdateFragment()
    }
}