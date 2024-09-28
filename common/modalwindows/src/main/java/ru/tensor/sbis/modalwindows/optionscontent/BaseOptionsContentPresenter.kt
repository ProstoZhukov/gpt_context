package ru.tensor.sbis.modalwindows.optionscontent

import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption
import ru.tensor.sbis.mvp.presenter.AbstractBasePresenter

/**
 * Базовый презентер для отображения опций внутри DialogFragment
 * @see [AbstractOptionSheetContentFragment]
 *
 * @author sr.golovkin
 */
abstract class BaseOptionsContentPresenter<VIEW: AbstractOptionSheetContentContract.View, OPTION: BottomSheetOption>
    : AbstractBasePresenter<VIEW, Any>(null), AbstractOptionSheetContentContract.Presenter<VIEW, OPTION>