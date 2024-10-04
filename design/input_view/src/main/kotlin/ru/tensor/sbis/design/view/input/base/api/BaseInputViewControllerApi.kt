package ru.tensor.sbis.design.view.input.base.api

import android.content.Context
import android.content.res.ColorStateList
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Px
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.BaseInputViewTextWatcher
import ru.tensor.sbis.design.view.input.base.InputViewClickListener
import ru.tensor.sbis.design.view.input.base.InputViewFocusChangedListener
import ru.tensor.sbis.design.view.input.base.InputViewTouchListener
import ru.tensor.sbis.design.view.input.base.MaskEditText
import ru.tensor.sbis.design.view.input.base.utils.UpdateState
import ru.tensor.sbis.design.view.input.base.utils.UpdateValueState

/**
 * Api для базового класса логики [BaseInputView].
 *
 * @author ps.smirnyh
 */
internal interface BaseInputViewControllerApi : BaseInputViewApi {

    /**
     * Базовый класс поля ввода.
     */
    var baseInputView: BaseInputView

    /**
     * Реализация [android.widget.EditText] с поддержкой статической маски и режима постановки курсора.
     */
    val inputView: MaskEditText

    /**@SelfDocumented*/
    val context: Context

    /**
     * Отступ между элементами внутри поля ввода.
     */
    @get:Px
    val innerSpacing: Int

    /**
     * [ColorStateList] для хранения цветов состояний валидации.
     */
    var underlineColorStateList: ColorStateList?

    /**
     * Элемент управления, который предопределен для поля ввода пароля и выбора.
     */
    val iconView: TextLayout

    /**
     * Иконка очистки текста.
     */
    val clearView: TextLayout

    /**
     * Заголовок поля ввода.
     */
    val titleView: TextLayout

    /**
     * Текст валидации под подчеркиванием.
     */
    val validationStatusView: TextLayout

    /**
     * Хелпер для обработки кликов по текстовым элементам.
     */
    val touchManager: TextLayoutTouchManager

    /**
     * Прогресс поля ввода.
     */
    val progressView: CircularProgressDrawable

    /**
     * Слушатель кликов по полю ввода с поддержкой внутренних механик.
     */
    val clickListener: InputViewClickListener

    /**
     * Слушатель событий касаний по полю ввода с поддержкой внутренних механик.
     */
    val touchListener: InputViewTouchListener

    /**
     * Внутренняя обёртка над [View.OnFocusChangeListener].
     */
    val focusChangedListener: InputViewFocusChangedListener

    /**
     * Обёртка над колбеком изменения текста в поле ввода, см. [onValueChanged].
     */
    val valueChangedWatcher: BaseInputViewTextWatcher

    /**
     * Текущий тип ввода, в наследниках он может быть другим.
     */
    var actualKeyListener: KeyListener

    /**
     * Callback для обновления ellipsize.
     */
    var updateEllipsisCallback: UpdateState

    /**
     * Callback для обновления состояния после изменения свойств.
     */
    var updatePropertyCallback: UpdateState

    /**
     * Callback для обновления текста и видимости заголовка и подсказки.
     */
    var updateHintCallback: UpdateState

    /**
     * Callback для обновления при изменении фокуса.
     */
    var updateFocusCallback: UpdateState

    var updateDigitsCallback: UpdateValueState<String?>

    /**@SelfDocumented*/
    fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    )

    /**
     * Обновление текста и видимости заголовка и подсказки.
     */
    fun updateInputViewHint(isFocus: Boolean)

    /** Получить обновленные значения метки и подсказки. */
    fun getPlaceholderAndTitle(
        isFocus: Boolean,
        placeholder: String = this.placeholder,
        title: String = this.title
    ): Pair<String, String>

    /** Обновить значения метки и подсказки переданными значениями. */
    fun updatePlaceholderAndTitle(updatePlaceholder: String, updateTitle: String)

    /**
     * Callback на изменение состояние drawableState вью.
     */
    fun onViewStateChanged(drawableState: IntArray)

    /**
     * Обновление состояния при изменении фокуса.
     */
    fun updateOnFocusChanged(isFocus: Boolean)

    /**
     * Обновляет многоточие и поле ввода.
     */
    fun updateEllipsis(isFocus: Boolean)

    /**
     * Обновляет видимость внутренних элементов,
     * зависящих от значений свойств [isProgressVisible], [isClearVisible], [readOnly].
     */
    fun updateOnPropertiesChanged(): Boolean

    /** Перезаписывает [actualKeyListener], разрешает для ввода только переданные в [digits] символы. */
    fun setDigits(digits: String?)

    /**@SelfDocumented*/
    fun getDefaultWidthChildMeasureSpec(@Px size: Int, parentMode: Int): Int

    /**@SelfDocumented*/
    fun getDefaultHeightChildMeasureSpec(@Px size: Int, parentMode: Int): Int

}