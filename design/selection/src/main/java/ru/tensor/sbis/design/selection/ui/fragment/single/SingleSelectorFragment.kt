package ru.tensor.sbis.design.selection.ui.fragment.single

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModel
import ru.tensor.sbis.design.selection.databinding.SelectionFragmentHostBinding
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionCancelListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionListener
import ru.tensor.sbis.design.selection.ui.di.single.DaggerSingleSelectionComponent
import ru.tensor.sbis.design.selection.ui.di.single.SingleSelectionComponent
import ru.tensor.sbis.design.selection.ui.factories.SELECTOR_CANCEL_LISTENER
import ru.tensor.sbis.design.selection.ui.factories.SELECTOR_COMPLETE_LISTENER
import ru.tensor.sbis.design.selection.ui.factories.SELECTOR_ENABLE_SWIPE_BACK
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.cloneWithTheme
import ru.tensor.sbis.design.selection.ui.utils.counterFormat
import ru.tensor.sbis.design.selection.ui.utils.isSmallSearchInputLeftSpace
import ru.tensor.sbis.design.selection.ui.utils.itemHandleStrategy
import ru.tensor.sbis.design.selection.ui.utils.needCloseButton
import ru.tensor.sbis.design.selection.ui.utils.selectorStrings
import ru.tensor.sbis.design.selection.ui.utils.serializableArg
import ru.tensor.sbis.design.selection.ui.utils.singleDependenciesFactory
import ru.tensor.sbis.design.selection.ui.utils.themeRes
import ru.tensor.sbis.design.selection.ui.utils.useCaseValue
import ru.tensor.sbis.design.swipeback.SwipeBackFragment
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.util.dpToPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticAction.OPEN_SELECTION
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticEvent
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticUseCase
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionStatisticUtil
import timber.log.Timber
import ru.tensor.sbis.design.R as RDesign

/**
 * Экран одиночного выбора со списком элементов и шапкой со строкой поиска
 *
 * @author us.bessonov
 */
internal class SingleSelectorFragment : SwipeBackFragment(), AdjustResizeHelper.KeyboardEventListener, Content {

    private val completeListener: SelectionListener<SelectorItemModel, FragmentActivity>
        by serializableArg(SELECTOR_COMPLETE_LISTENER)
    private val cancelListener: SelectionCancelListener<FragmentActivity>
        by serializableArg(SELECTOR_CANCEL_LISTENER)
    internal val selectorStrings: SelectorStrings by lazy {
        requireArguments().selectorStrings
    }
    internal val isSwipeBackEnabled: Boolean by lazy {
        requireArguments().getBoolean(SELECTOR_ENABLE_SWIPE_BACK)
    }

    private val disposable = CompositeDisposable()
    private val selectionVM: SingleSelectionViewModel<SelectorItemModel> get() = selectionComponent.selectionVm

    private var viewBindingNullable: SelectionFragmentHostBinding? = null
    private val viewBinding get() = viewBindingNullable!!

    internal lateinit var selectionComponent: SingleSelectionComponent
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val arguments = requireArguments()
        selectionComponent = DaggerSingleSelectionComponent.factory().create(
            this,
            arguments.singleDependenciesFactory.getSelectionLoader(context.applicationContext),
            arguments.itemHandleStrategy,
            requireArguments().counterFormat
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val context = arguments?.useCaseValue ?: SelectionStatisticUseCase.UNKNOWN.value
            SelectionStatisticUtil.sendStatistic(SelectionStatisticEvent(context, OPEN_SELECTION.value))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val arguments = requireArguments()
        val themedInflater = inflater.cloneWithTheme(requireContext(), arguments.themeRes)
        viewBindingNullable = SelectionFragmentHostBinding.inflate(themedInflater, container, false)
        viewBinding.headerContent.toolbar.leftIcon.isVisible = arguments.needCloseButton
        val inputView = viewBinding.headerContent.searchPanel.children.find { it is BaseInputView } as? BaseInputView
        inputView?.valueColor = SbisColor.Int(requireContext().getThemeColorInt(RDesign.attr.textColor))
        configureSearchInputLeftSpace()
        return addToSwipeBackLayout(viewBinding.root)
    }

    private fun configureSearchInputLeftSpace() {
        if (requireArguments().isSmallSearchInputLeftSpace) {
            viewBinding.headerContent.toolbar.customViewContainer.updatePadding(left = 0)
            viewBinding.headerContent.toolbar.leftPanel.updateLayoutParams<MarginLayoutParams> {
                width = requireContext().dpToPx(SMALL_SEARCH_INPUT_LEFT_SPACE_DP)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.headerContent.searchPanel.setSearchHint(getString(selectorStrings.searchHint))

        savedInstanceState?.run {
            // TODO: 3/5/2020 https://online.sbis.ru/opendoc.html?guid=d4f9226d-9353-4ff3-b9de-f37d5aa81fdd
        } ?: childFragmentManager
            .beginTransaction()
            .add(R.id.contentContainer, SingleSelectorContentFragment.newInstance())
            .commit()
    }

    override fun onStart() {
        super.onStart()

        disposable.add(selectionVM.result.subscribe(::onComplete, Timber::e, ::onCancel))

        viewBinding.headerContent.toolbar.leftIcon.setOnClickListener { selectionVM.cancel() }
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBindingNullable = null
    }

    private fun onComplete(result: SelectorItemModel) = completeListener.onComplete(requireActivity(), result)

    private fun onCancel() = cancelListener.onCancel(this)

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        viewBinding.contentContainer.run { setPadding(paddingLeft, paddingTop, paddingRight, keyboardHeight) }
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        viewBinding.contentContainer.run { setPadding(paddingLeft, paddingTop, paddingRight, 0) }
        return true
    }

    override fun onBackPressed(): Boolean {
        onCancel()
        return true
    }

    override fun swipeBackEnabled() = isSwipeBackEnabled

    override fun onViewGoneBySwipe() {
        onCancel()
    }
}

private const val SMALL_SEARCH_INPUT_LEFT_SPACE_DP = 50