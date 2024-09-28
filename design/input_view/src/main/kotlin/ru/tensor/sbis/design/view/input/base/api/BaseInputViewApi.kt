package ru.tensor.sbis.design.view.input.base.api

import android.graphics.Typeface
import android.text.InputFilter
import android.text.method.MovementMethod
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.annotation.Px
import ru.tensor.sbis.design.theme.zen.ZenThemeSupport
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.ValidationStatus

/**
 * Базовое api поля ввода.
 *
 * @author ps.smirnyh
 */
interface BaseInputViewApi : ZenThemeSupport {

    /**
     * Значение в поле ввода.
     */
    var value: CharSequence

    /**
     * Вызывается при изменении текста в поле ввода.
     * @see [value]
     */
    var onValueChanged: ((view: BaseInputView, value: String) -> Unit)?

    /**
     * Максимальная длина вводимого текста, количество символов.
     */
    @get:IntRange(from = 0L)
    @setparam:IntRange(from = 0L)
    var maxLength: Int

    /**
     * Только для чтения - true, доступно редактирование - false.
     */
    var readOnly: Boolean

    /**
     * Текст подсказки внутри поля ввода.
     */
    var placeholder: String

    /**
     * Текст в заголовке над полем ввода.
     */
    var title: String

    /**
     * true если это обязательное поле.
     */
    var isRequiredField: Boolean

    /**
     * Текст подсказки показывается и перетекает наверх при получении фокуса если true,
     * заголовок фиксируется над полем ввода если false.
     */
    var showPlaceholderAsTitle: Boolean

    /**
     * true если должна быть кнопка очистки, иначе false.
     */
    var isClearVisible: Boolean

    /**
     * true если прогресс видим, иначе false.
     * Другие кнопки-иконки при этом невидимы если прогресс видим.
     */
    var isProgressVisible: Boolean

    /**
     * Статус валидации, см. [ValidationStatus].
     */
    var validationStatus: ValidationStatus

    /**
     * Слушатель клика по полю ввода.
     *
     * Для установки слушателя на поле ввода и
     * возможные дочерние элементы см. метод [BaseInputView.setOnClickListener]
     */
    var onFieldClickListener: View.OnClickListener?

    /**
     * Слушатель событий касания по полю ввода.
     *
     * Для установки слушателя на поле ввода и
     * возможные дочерние элементы см. метод [BaseInputView.setOnTouchListener]
     */
    var onFieldTouchListener: View.OnTouchListener?

    /**
     * Конфигурация кнопки действия на клавиатуре.
     * @see EditorInfo и его константы.
     */
    var imeOptions: Int

    /**
     * Раскладка кнопок на клавиатуре.
     */
    var inputType: Int

    /**
     * Список фильтров поля ввода.
     */
    var filters: Array<InputFilter>

    /**
     * Слушатель кнопки действия на клавиатуре.
     */
    var onEditorActionListener: ((inputView: BaseInputView, actionId: Int, event: KeyEvent?) -> Boolean)?

    /**
     * Является ли поле акцентным.
     */
    var isAccent: Boolean

    /**
     * Режим работы с selection при первом тапе.
     *
     * True - при первом тапе весь текст будет выделен.
     * False - при первом тапе каретка будет всегда перемещаться в конец текста.
     */
    var isSelectAllOnBeginEditing: Boolean

    /**
     * Убирать фокус при нажатии кнопки back.
     */
    var clearFocusOnBackPressed: Boolean

    /**
     * Выравнивание текста в поле ввода.
     *
     * @see EditText.setGravity
     * @see EditText.getGravity
     */
    var gravity: Int

    /**
     * Флаг для того, нужно ли поднимать клавиатуру при клике.
     */
    var onHideKeyboard: Boolean

    /**
     * @see [EditText.setShowSoftInputOnFocus].
     */
    var showSoftInputOnFocus: Boolean

    /**
     * Флаг того, что метка развернута на максимальное количество строк.
     * При false действует стандартное ограничение в 3 строки.
     * При нажатии на метку ограничение снимается (действие на клик работает только единожды).
     * Следует сохранять значение при использовании в RecyclerView, чтобы при скролле
     * состояние развернутости метки сохранялось, а не сбрасывалось.
     */
    var isExpandedTitle: Boolean

    /**
     * Способ взаимодействия с текстом поля ввода.
     */
    var movementMethod: MovementMethod?

    /**
     * Размер текста поля ввода.
     */
    @get:Px
    @setparam:Px
    var valueSize: Float

    /**
     * Цвет основного текста.
     */
    var valueColor: SbisColor

    /**
     * Метод аналогичный [EditText.setSelection] для управления позицией курсора.
     */
    fun setSelection(index: Int)

    /**
     * Установить отступ от текста до подчеркивания.
     */
    fun setBottomOffsetUnderline(@Px offset: Int)

    /**
     * Получить ширину текста в поле ввода, либо получить ширину переданного [text].
     */
    @Px
    fun getValueWidth(text: CharSequence = value): Int

    /**
     * Получить высоту текста в поле ввода.
     */
    @Px
    fun getValueHeight(): Int

    /**
     * В фокусе ли поле ввода.
     */
    fun isInputViewFocused(): Boolean

    /**
     * Установить шрифт текста поля ввода.
     *
     * @see [TextView.setTypeface]
     */
    fun setTypeface(typeface: Typeface, style: Int = Typeface.NORMAL)
}