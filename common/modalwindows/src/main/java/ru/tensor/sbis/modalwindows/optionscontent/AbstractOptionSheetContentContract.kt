package ru.tensor.sbis.modalwindows.optionscontent

import ru.tensor.sbis.mvp.presenter.BasePresenter
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption

/**
 * Контракт контента опций, отображаемых в контейнере
 *
 * @author sr.golovkin
 */
interface AbstractOptionSheetContentContract {

    interface View {

        /**
         *  Закрыть диалоговое окно
         */
        fun closeDialog()
    }

    interface Presenter<V: View, O: BottomSheetOption>: BasePresenter<V> {

        /**
         * Создать список опций для отображения
         */
        fun createOptions(isLandscape: Boolean): List<O>

        /**
         * Произвести обработку нажатия на опцию
         */
        fun onOptionClick(option: O)
    }
}