package ru.tensor.sbis.design_dialogs.multipicker

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design.design_dialogs.R
import ru.tensor.sbis.design.design_dialogs.databinding.DesignDialogsMultiPickerContainerBinding
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

import java.util.concurrent.TimeUnit

/**
 * Мультипикер(layout с NumberPicker) со следующими возможностями:
 * - Добавление нескольких барарбанов с различным цветом и размером текста
 * - Изменение данных в конкретном барабане в runtime
 * - Добавление текста справа от барабана с различным размером и цветом
 */

private const val REPLACE_MULTI_PICKER_TIMEOUT = 700L

class MultiPickerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val previousPickerValues = HashMap<String, String>()

    private val replaceMultiPicker = PublishSubject.create<MultiPickerDataItem>()

    private val viewBinding =
        DesignDialogsMultiPickerContainerBinding.inflate(
            LayoutInflater.from(getContext()),
            this,
            true
        )

    init {
        initReplaceMultiPicker()
    }

    /**
     * Функция для получения значения барабана по тэгу
     */
    lateinit var onChangeListener: (tag: String, value: String) -> Unit

    /**
     *  Функция для добавления барабанов в контейнер
     *
     *  @param pickerDataItems - список моделей барабанов
     */
    fun setSections(pickerDataItems: List<MultiPickerDataItem>) {
        viewBinding.designDialogsPickersContainer.removeAllViews()
        pickerDataItems.forEach { multiPickerItem ->
            val pickerItemView = getNumberPicker(multiPickerItem)
            viewBinding.designDialogsPickersContainer.addView(
                pickerItemView,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(
                        if (multiPickerItem.isAddMarginLeft)
                            context.resources.getDimensionPixelSize(R.dimen.multi_picker_item_section_margin) else 0,
                        0,
                        if (multiPickerItem.isAddMarginRight)
                            context.resources.getDimensionPixelSize(R.dimen.multi_picker_item_section_margin) else 0,
                        0
                    )
                    weight = if (multiPickerItem.values.any { it.length > 2 }) 2f else 1f
                }
            )

            val currentValue = multiPickerItem.values.safeGet(multiPickerItem.selectedPosition)
            previousPickerValues[multiPickerItem.tag] = currentValue

            if (multiPickerItem.rightText.isNotBlank()) {
                val rightNumberPickerText = SbisTextView(context).apply {
                    tag = "${multiPickerItem.tag}text"
                    text = multiPickerItem.rightText
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        multiPickerItem.rightTextSize.toFloat()
                    )
                    setTextColor(multiPickerItem.rightTextColor)

                }
                viewBinding.designDialogsPickersContainer.addView(
                    rightNumberPickerText,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin =
                            context.resources.getDimensionPixelSize(R.dimen.multi_picker_right_text_margin_bottom)
                    }
                )
            }
        }
    }

    /**
     *  Функция для смены барабана по позиции
     *
     *  @param pickerDataItem - модель барабана
     */
    fun replaceNumberPicker(pickerDataItem: MultiPickerDataItem) {
        replaceMultiPicker.onNext(pickerDataItem)
    }

    /**
     *  Функция для смены барабана по позиции
     *
     *  @param tag - тэг для идентифицирования барабана в контейнере
     *  @param values - данные для барабана
     */
    fun setDisplayedValues(tag: String, values: List<String>) {
        viewBinding.designDialogsPickersContainer.findViewWithTag<MultiPicker>(tag)?.run {
            minValue = 0
            if (values.size < displayedValues.size) maxValue = values.size - 1
            if (value >= values.size) value = values.size - 1
            displayedValues = values.toTypedArray()
            if (values.size >= displayedValues.size) maxValue = values.size - 1
        }
    }

    /**
     *  Функция для смены выделенного элемента в барабане
     *
     *  @param tag - тэг для идентифицирования барабана в контейнере
     *  @param elementPosition - позиция элемента на которую нужно перейти
     */
    fun setPosition(tag: String, elementPosition: Int) {
        viewBinding.designDialogsPickersContainer.findViewWithTag<MultiPicker>(tag)?.run {
            val newElementPosition = when {
                elementPosition < 0 -> 0
                elementPosition >= displayedValues.size -> displayedValues.size - 1
                else -> elementPosition
            }
            previousPickerValues[tag] = displayedValues[newElementPosition]
            value = newElementPosition
        }
    }

    private fun <T> List<T>.safeGet(position: Int): T =
        when {
            position < 0 -> get(0)
            position >= size -> get(size - 1)
            else -> get(position)
        }

    private fun <T> Array<T>.safeGet(position: Int): T =
        when {
            position < 0 -> get(0)
            position >= size -> get(size - 1)
            else -> get(position)
        }

    /**
     *  Функция для получения позиции выделенного элемента
     *
     *  @param tag - тэг для идентифицирования барабана в контейнере
     */
    fun getPosition(tag: String): Int =
        viewBinding.designDialogsPickersContainer.findViewWithTag<MultiPicker>(tag).value

    /**
     *  Функция для получения предыдущей позиции выделенного элемента
     *
     *  @param tag - тэг для идентифицирования барабана в контейнере
     */
    fun getPreviousPosition(tag: String): String = previousPickerValues[tag] ?: ""

    @SuppressLint("CheckResult")
    private fun initReplaceMultiPicker() {
        replaceMultiPicker
            .debounce(REPLACE_MULTI_PICKER_TIMEOUT, TimeUnit.MILLISECONDS, Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { multiPickerItem ->
                viewBinding.designDialogsPickersContainer
                    .findViewWithTag<SbisTextView>("${multiPickerItem.tag}text")?.let {
                        viewBinding.designDialogsPickersContainer.removeView(it)
                    }
                viewBinding.designDialogsPickersContainer
                    .findViewWithTag<MultiPicker>(multiPickerItem.tag)?.let {
                        val pickerItemView = getNumberPicker(multiPickerItem)
                        val viewPosition = viewBinding.designDialogsPickersContainer.indexOfChild(it)
                        viewBinding.designDialogsPickersContainer.removeView(it)
                        viewBinding.designDialogsPickersContainer.addView(
                            pickerItemView, viewPosition,
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            ).apply {
                                setMargins(
                                    if (multiPickerItem.isAddMarginLeft)
                                        context
                                            .resources
                                            .getDimensionPixelSize(R.dimen.multi_picker_item_section_margin) else 0,
                                    0,
                                    if (multiPickerItem.isAddMarginRight)
                                        context
                                            .resources
                                            .getDimensionPixelSize(R.dimen.multi_picker_item_section_margin) else 0,
                                    0
                                )
                            }
                        )

                        if (multiPickerItem.rightText.isNotBlank()) {
                            val rightNumberPickerText = SbisTextView(context).apply {
                                tag = "${multiPickerItem.tag}text"
                                text = multiPickerItem.rightText
                                setTextSize(
                                    TypedValue.COMPLEX_UNIT_SP,
                                    multiPickerItem.rightTextSize.toFloat()
                                )
                                setTextColor(multiPickerItem.rightTextColor)
                            }
                            viewBinding.designDialogsPickersContainer.addView(
                                rightNumberPickerText, viewPosition + 1,
                                LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    bottomMargin =
                                        context
                                            .resources
                                            .getDimensionPixelSize(R.dimen.multi_picker_right_text_margin_bottom)
                                }
                            )
                        }
                    }
            }
    }

    private fun getNumberPicker(pickerDataItem: MultiPickerDataItem): MultiPicker =
        with(pickerDataItem) {
            MultiPicker(ContextThemeWrapper(context, valuesTextStyle)).apply {
                tag = this@with.tag
                minValue = 0
                maxValue = values.size - 1
                displayedValues = values.toTypedArray()
                value = selectedPosition
                descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                wrapSelectorWheel = isWrapSelectorWheel

                previousPickerValues[this@with.tag] = values.safeGet(selectedPosition)

                setOnValueChangedListener { _, _, index ->
                    onChangeListener.invoke(this@with.tag, displayedValues.safeGet(index))
                    previousPickerValues[this@with.tag] = displayedValues.safeGet(index)
                }
            }
        }
}