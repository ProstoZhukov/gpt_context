package ru.tensor.sbis.modalwindows.optionscontent.universal.mutable

import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOptionsAdapter
import ru.tensor.sbis.modalwindows.optionscontent.AbstractOptionSheetContentFragment
import ru.tensor.sbis.modalwindows.optionscontent.universal.mutable.interactor.MutableOptionsInteractor
import ru.tensor.sbis.common.util.requireParentAs
import ru.tensor.sbis.design_dialogs.dialogs.container.base.BaseContainerDialogFragment

/**
 * Контент окна опций, отличающийся способностью динамического отображения опций и загрузкой опций из диалогового окна
 *
 * @author sr.golovkin
 */
abstract class UniversalMutableOptionsContentFragment<
        V: UniversalMutableOptionsContentContract.View<O>,
        I: MutableOptionsInteractor<O>,
        A: BottomSheetOptionsAdapter<O>,
        O: BottomSheetOption>
    : AbstractOptionSheetContentFragment<V, UniversalMutableOptionsContentContract.Presenter<V, O>, O>(), UniversalMutableOptionsContentContract.View<O> {

    override fun updateOptionList(list: List<O>) {
        adapter?.options = list
    }

    override fun createOptionsAdapter(
        options: List<O>,
        isLandscape: Boolean,
        listener: BottomSheetOptionsAdapter.Listener<O>
    ): BottomSheetOptionsAdapter<O> {
        return BottomSheetOptionsAdapter<O>(options, listener)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getPresenterView(): V {
        return this as V
    }

    final override fun createPresenter(): UniversalMutableOptionsContentContract.Presenter<V, O> {
        return UniversalMutableOptionsContentPresenter(createInteractor())
    }

    override fun closeDialog() {
        requireParentAs<BaseContainerDialogFragment>().dismissAllowingStateLoss()
    }

    abstract fun createInteractor(): I
}