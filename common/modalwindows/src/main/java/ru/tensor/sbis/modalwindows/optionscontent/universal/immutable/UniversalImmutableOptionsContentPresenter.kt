package ru.tensor.sbis.modalwindows.optionscontent.universal.immutable

import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption
import ru.tensor.sbis.modalwindows.optionscontent.BaseOptionsContentPresenter

/**
 * Презентер универсального окна опций прочтения
 * 
 * @author sr.golovkin
 */
open class UniversalImmutableOptionsContentPresenter(
    private val options: List<BottomSheetOption>
): BaseOptionsContentPresenter<UniversalImmutableOptionsContentContract.View, BottomSheetOption>(),
    UniversalImmutableOptionsContentContract.Presenter {

    override fun createOptions(isLandscape: Boolean): List<BottomSheetOption> {
        return options
    }

    override fun onOptionClick(option: BottomSheetOption) {
        mView?.notifyOptionSelected(option)
        mView?.closeDialog()
    }
}