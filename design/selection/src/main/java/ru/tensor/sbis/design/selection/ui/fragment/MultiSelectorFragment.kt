package ru.tensor.sbis.design.selection.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.databinding.SelectionFragmentHostBinding
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionCancelListener
import ru.tensor.sbis.design.selection.ui.di.multi.MultiSelectionComponent
import ru.tensor.sbis.design.selection.ui.factories.SELECTOR_CANCEL_LISTENER
import ru.tensor.sbis.design.selection.ui.factories.SELECTOR_COMPLETE_LISTENER
import ru.tensor.sbis.design.selection.ui.factories.SELECTOR_ENABLE_SWIPE_BACK
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.cloneWithTheme
import ru.tensor.sbis.design.selection.ui.utils.createMultiSelectionComponent
import ru.tensor.sbis.design.selection.ui.utils.needCloseButton
import ru.tensor.sbis.design.selection.ui.utils.selectorStrings
import ru.tensor.sbis.design.selection.ui.utils.serializableArg
import ru.tensor.sbis.design.selection.ui.utils.themeRes
import ru.tensor.sbis.design.selection.ui.utils.useCaseValue
import ru.tensor.sbis.design.swipeback.SwipeBackFragment
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticAction.OPEN_SELECTION
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticEvent
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticUseCase
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionStatisticUtil
import timber.log.Timber
import ru.tensor.sbis.design.R as RDesign

/**
 * Реализация фрагмента для множественного выбора
 *
 * @author ma.kolpakov
 */
internal class MultiSelectorFragment : SwipeBackFragment(), AdjustResizeHelper.KeyboardEventListener, Content {

    private val completeListener: MultiSelectionListener<SelectorItemModel, FragmentActivity>
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
    private val selectionVM: MultiSelectionViewModel<SelectorItemModel> get() = selectionComponent.selectionVm

    private var viewBindingNullable: SelectionFragmentHostBinding? = null
    private val viewBinding get() = viewBindingNullable!!

    /**
     * Компонент доступен для использования в качестве зависимости в других DI графах
     */
    internal lateinit var selectionComponent: MultiSelectionComponent
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)

        selectionComponent = createMultiSelectionComponent()
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
        return addToSwipeBackLayout(viewBinding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.headerContent.searchPanel.setSearchHint(getString(selectorStrings.searchHint))

        savedInstanceState?.run {
            // TODO: 3/5/2020 https://online.sbis.ru/opendoc.html?guid=d4f9226d-9353-4ff3-b9de-f37d5aa81fdd
        } ?: childFragmentManager
            .beginTransaction()
            .add(R.id.contentContainer, MultiSelectorContentFragment.newInstance())
            .commit()
    }

    override fun onStart() {
        super.onStart()

        disposable.addAll(
            selectionVM.doneButtonVisible.subscribe { viewBinding.headerContent.doneButton.isVisible = it },
            selectionVM.doneButtonEnabled.subscribe { viewBinding.headerContent.doneButton.isEnabled = it },
            selectionVM.result.subscribe(::onComplete, Timber::e, ::onCancel),
            selectionVM.limitExceed.subscribe(::showLimitToast, Timber::w)
        )

        viewBinding.headerContent.toolbar.leftIcon.setOnClickListener { selectionVM.cancel() }
        viewBinding.headerContent.doneButton.setOnClickListener { selectionVM.complete() }
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBindingNullable = null
    }

    private fun onComplete(result: List<SelectorItemModel>) = completeListener.onComplete(requireActivity(), result)

    private fun onCancel() = cancelListener.onCancel(this)

    private fun showLimitToast(limit: Int) {
        val message = resources.getString(selectorStrings.limitExceeded, limit)
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.ERROR, message)
    }

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