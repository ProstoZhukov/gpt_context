package ru.tensor.sbis.modalwindows.optionscontent.universal.mutable

import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption
import ru.tensor.sbis.modalwindows.optionscontent.AbstractOptionSheetContentContract

/**
 * Контракт окна с динамически создаваемыми опциями
 *
 * @author sr.golovkin
 */
interface UniversalMutableOptionsContentContract {

    /**@SelfDocumented**/
    interface View<O: BottomSheetOption> : AbstractOptionSheetContentContract.View {

        /**
         * Обновить список опций
         */
        fun updateOptionList(list: List<O>)

    }

    /**@SelfDocumented**/
    interface Presenter<V: View<O>, O: BottomSheetOption>: AbstractOptionSheetContentContract.Presenter<V, O> {

        /**
         * Callback, сообщающий об обновлении списка опций.
         */
        fun onOptionsUpdated(newOptions: List<O>)
    }
}