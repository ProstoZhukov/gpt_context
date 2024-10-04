package ru.tensor.sbis.design.retail_views.double_button

import android.view.View
import android.widget.EditText
import com.mikepenz.iconics.typeface.IIcon

/** Описание публичного Api "двойной" кнопки. */
interface DoubleButtonApi {

    /** Доступ к [ViewPropertiesApi]. */
    val viewPropertiesApi: ViewPropertiesApi

    /** Доступ к [ActionApi]. */
    val actionApi: ActionApi

    /** Доступ к [DangerousApi]. */
    val dangerousApi: DangerousApi

    /** Режимы работы "двойной" кнопки. */
    sealed interface Mode {
        /**
         * "Режим редактирования".
         *
         * Если [isLocked] true:
         * "Заблокированный режим редактирования".
         * Кнопка НЕ может менять свое состояние:
         *  - активен ввод числового значения.
         *  - активно нажатие на кнопку с иконкой.
         *
         * Если [isLocked] false:
         * Кнопка может менять свое состояние:
         *  - активен ввод числового значения.
         *  - НЕ активно нажатие на кнопку с иконкой.
         */
        class Editing(val isLocked: Boolean) : Mode

        /**
         * "Режим обычной кнопки".
         *
         * Кнопка может менять свое состояние:
         *  - доступно только нажатие на всю кнопку.
         */
        object Button : Mode
    }

    /** Api для работы с чтением/установкой данных из/в "двойную" кнопку. */
    interface ViewPropertiesApi {
        /** Переключение режима видимости кнопки [View.VISIBLE], [View.GONE]. */
        var isVisible: Boolean

        /** Переключение режима невидимости кнопки  [View.INVISIBLE], [View.VISIBLE]. */
        var isInvisible: Boolean

        /** Переключение режима возможности нажатия на кнопку. */
        var isEnabled: Boolean

        /** Состояние "двойной" кнопки - статичное/режим редактирования. */
        val isEditMode: Boolean

        /** Находится ли "двойная" кнопка в заблокированном режиме редактирования. */
        val isEditModeLocked: Boolean

        /** Текущий режим работы "двойной" кнопки. */
        val currentButtonMode: Mode

        /** Текущее введенное пользователем значение. */
        val currentInputValue: String

        /** Установка иконки [icon] из файла шрифта. */
        fun setIcon(icon: IIcon)

        /** Установка текста [value] в поле ввода. */
        fun setInputTextValue(value: String)

        /** Переключение режима [Mode] "двойной" кнопки. */
        fun changeDoubleButtonModeTo(mode: Mode)
    }

    /** Api для обработки действий пользователя при работе с "двойной" кнопкой. */
    interface ActionApi {
        /** Ввод значения [moneyValue] в поле ввода двойной кнопки. */
        var onMoneyChangedAction: ((moneyValue: String?) -> Unit)?

        /** Нажатие на кнопку с иконкой с указанием текущего режима [Mode]. */
        var onIconClickAction: ((buttonMode: Mode) -> Unit)?

        /** Действие по нажатию на основную часть кнопки. */
        var onFullButtonClickAction: (() -> Unit)?
    }

    /**
     * "Опасное" Api для прямого доступа к View элементам "двойной" кнопки.
     *
     * p.s. Может использоваться только как временное решение насущных проблем,
     * в дальнейшем необходимо выписать задачу на доработку API.
     */
    interface DangerousApi {
        /** Получить доступ к root View элементу - "двойная" кнопка. */
        val doubleButtonRoot: View

        /** Получить доступ к View элементу - "поле ввода двойной кнопки". */
        val editableView: EditText
    }
}