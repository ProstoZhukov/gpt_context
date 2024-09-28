package ru.tensor.sbis.version_checker.ui.recommended

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.stubview.ResourceImageStubContent
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design_dialogs.movablepanel.isEqual
import ru.tensor.sbis.mvp.fragment.BottomSheetDialogPresenterFragment
import ru.tensor.sbis.version_checker.R
import ru.tensor.sbis.version_checker.VersionCheckerPlugin
import ru.tensor.sbis.version_checker.data.UpdateCommand
import ru.tensor.sbis.version_checker.ui.utils.GradientShaderFactory
import ru.tensor.sbis.version_checker_decl.VersionedComponent
import javax.inject.Inject
import ru.tensor.sbis.design.design_dialogs.R as RDesignDialogs

/** Фрагмент отображения Рекомендованного обновления. */
internal class RecommendedUpdateFragment :
    BottomSheetDialogPresenterFragment<RecommendedUpdateContract.View, RecommendedUpdateContract.Presenter>(),
    RecommendedUpdateContract.View,
    VersionedComponent {

    private val expandedPeekHeight = MovablePanelPeekHeight.FitToContent()
    private val hiddenPeekHeight = MovablePanelPeekHeight.Percent(0F)
    private var panelStateDisposables: Disposable? = null

    @Inject
    lateinit var presenter: RecommendedUpdatePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, RDesignDialogs.style.TransparentBottomSheetTheme)
    }

    override fun inject() = VersionCheckerPlugin
        .versioningComponent
        .recommendedComponentFactory()
        .inject(this)

    @SuppressLint("MissingInflatedId") // Id устанавливается через app:MovablePanel_contentContainerId
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val themedContext = provideThemedContext()
        val themedInflater = inflater.cloneInContext(themedContext)
        val rootView = themedInflater.inflate(
            R.layout.versioning_fragment_recommended_update,
            container,
            true
        )
        rootView.findViewById<MovablePanel>(R.id.versioning_version_movable_panel).apply {
            contentContainer?.background = GradientShaderFactory.createBrandGradient(context)
            adjustPopup()
        }
        val panelContainer = rootView.findViewById<ViewGroup>(R.id.versioning_movable_content_view_container_id)
        themedInflater.inflate(R.layout.versioning_fragment_recommended_update_content, panelContainer, true)
        setStubView(panelContainer)
        setClickListeners(panelContainer)
        dialog?.applyDialogBehavior()
        return rootView
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(SKIP_DELAY)
            mPresenter.skipNextRecommendation()
        }
    }

    private fun Dialog.applyDialogBehavior() {
        castTo<BottomSheetDialog>()?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
    }

    override fun onDestroyView() {
        if (view != null) {
            val parent = requireView().parent as ViewGroup
            parent.removeView(view)
        }
        super.onDestroyView()
        panelStateDisposables?.dispose()
    }

    override fun getPresenterLoaderId(): Int = R.id.versioning_stub_presenter_loader_id

    override fun createPresenter(): RecommendedUpdatePresenter = presenter

    override fun getPresenterView(): RecommendedUpdateContract.View = this

    override fun runCommand(command: UpdateCommand): String? = context?.let(command::run)

    /** Установить содержимое заглушки для отображения */
    private fun setStubView(view: View) {
        view.findViewById<StubView>(R.id.versioning_version_stub_view).setContent(
            ResourceImageStubContent(
                icon = R.drawable.versioning_update_drawable,
                messageRes = R.string.versioning_update_optional_title,
                detailsRes = R.string.versioning_update_optional_detail
            )
        )
        view.findViewById<SbisButton>(R.id.versioning_version_btn_accept).style = presenter.getButtonStyle()
    }

    /** Установить обработчики кликов по кнопкам "Отложить" и "Обновить"  */
    private fun setClickListeners(view: View) {
        setClickListenerToViewWithId(view, R.id.versioning_version_btn_postpone) {
            mPresenter.onPostponeUpdate(true)
            dismiss()
        }
        setClickListenerToViewWithId(view, R.id.versioning_version_btn_accept) {
            mPresenter.onAcceptUpdate()
            dismiss()
        }
    }

    private fun setClickListenerToViewWithId(view: View, buttonId: Int, function: (v: View) -> Unit) =
        view.findViewById<View>(buttonId).setOnClickListener(function)

    private fun MovablePanel.adjustPopup() {
        setPeekHeightList(
            listOf(
                expandedPeekHeight,
                hiddenPeekHeight
            ),
            expandedPeekHeight
        )
        panelStateDisposables = getPanelStateSubject().subscribe { height ->
            if (height.isEqual(expandedPeekHeight)) {
                view?.findViewById<View>(R.id.versioning_version_layout)?.requestFocus()
            }
            if (height.isEqual(hiddenPeekHeight)) dismiss()
        }
    }

    private fun provideThemedContext() =
        ThemeContextBuilder(
            requireContext(),
            R.attr.versioningTheme,
            R.style.VersioningUpdateTheme
        ).build()

    companion object {

        /** Тег, чтобы находить в стеке фрагментов и не создавать новый, если уже есть. */
        const val screenTag: String = "RecommendedUpdateFragment"

        /** Задержка по истечении которой фрагмент считается показаным. */
        private const val SKIP_DELAY = 800L

        /**
         * Создание новой копии [RecommendedUpdateFragment].
         */
        @JvmStatic
        fun newInstance() = RecommendedUpdateFragment()
    }
}

internal inline fun <reified T> Any.castTo(): T? = this as? T
