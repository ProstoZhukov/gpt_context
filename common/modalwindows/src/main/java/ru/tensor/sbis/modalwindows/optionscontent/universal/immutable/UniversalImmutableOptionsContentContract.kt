package ru.tensor.sbis.modalwindows.optionscontent.universal.immutable

import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption
import ru.tensor.sbis.modalwindows.optionscontent.AbstractOptionSheetContentContract

/**
 * Контракт универсального окна опций прочтения
 *
 * @author sr.golovkin
 */
interface UniversalImmutableOptionsContentContract {

    /**
     * @SelfDocumented
     */
    interface View : AbstractOptionSheetContentContract.View {

        /**
         * Уведомить о выборе опции
         */
        fun notifyOptionSelected(option: BottomSheetOption)
    }

    /**
     * @SelfDocumented
     */
    interface Presenter: AbstractOptionSheetContentContract.Presenter<View, BottomSheetOption>
}