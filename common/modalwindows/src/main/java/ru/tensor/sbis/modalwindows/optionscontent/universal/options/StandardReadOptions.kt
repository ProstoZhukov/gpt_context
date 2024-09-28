package ru.tensor.sbis.modalwindows.optionscontent.universal.options

import android.content.Context
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.modalwindows.R
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption
import ru.tensor.sbis.design.R as RDesign

/**
 * Стандартные опции для формирования меню прочтения
 *
 * @author sr.golovkin
 */
@Suppress("unused")
enum class StandardReadOptions(val value: Int) {

    /**
     * Прочесть всё
     */
    READ_ALL(0),

    /**
     * Прочесть на текущей странице и ниже
     */
    READ_ON_THIS_PAGE_AND_BELOW(1),

    /**
     * Удалить всё
     */
    DELETE_ALL(2);

    companion object {
        /**
         * Получить полный список опций
         */
        fun getListOfOptions(context: Context): List<BottomSheetOption> {
            return getOptionsForTypes(context, *values())
        }

        /**
         * Получить список опций для переданных значений перечисления
         */
        fun getOptionsForTypes(context: Context, vararg types: StandardReadOptions): List<BottomSheetOption> {
            val list = mutableListOf<BottomSheetOption>()
            for (type in types) {
                list.add(
                    createOptionFromType(context, type)
                )
            }
            return list
        }

        private fun createOptionFromType(context: Context, type: StandardReadOptions): BottomSheetOption {
            return BottomSheetOption().apply {
                optionValue = type.value
                when (type) {
                    READ_ALL -> {
                        icon = SbisMobileIcon.Icon.smi_read.character.toString()
                        name = context.getString(R.string.modalwindows_read_option_read_all_text)
                    }
                    READ_ON_THIS_PAGE_AND_BELOW -> {
                        icon = SbisMobileIcon.Icon.smi_read.character.toString()
                        name = context.getString(R.string.modalwindows_read_option_read_below_text)
                    }
                    DELETE_ALL -> {
                        icon = SbisMobileIcon.Icon.smi_delete.character.toString()
                        name = context.getString(R.string.modalwindows_read_option_delete_all_text)
                        iconColor = ContextCompat.getColor(context, RDesign.color.red_color_for_delete_icon)
                    }
                }
            }
        }
    }


}