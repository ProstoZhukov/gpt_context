package ru.tensor.sbis.design.view.input.base

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.custom_view_tools.TextLayoutConfig
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.base.utils.style.BaseStyleHolder

/**
 * Класс для управления логиков создания фона валидации и обновления текста и цвета валидации.
 *
 * @author ps.smirnyh
 */
internal class ValidationStatusAdapter(
    styleHolder: BaseStyleHolder
) {
    private val statesReadOnly = arrayOf(
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf(android.R.attr.state_focused),
        intArrayOf()
    )
    private val validationColors = mutableMapOf<Int, ValidationStatusSelector>()
    private val validationColorsProvider: Map<Int, () -> ValidationStatusSelector>

    init {
        with(styleHolder.style) {
            validationColorsProvider = mapOf(
                R.attr.inputView_validationDefaultColor to {
                    createValidationStatusSelectorInfo(
                        validationDefaultColor,
                        validationTextDefaultColor,
                        validationDefaultColorReadOnly,
                        true
                    )
                },
                R.attr.inputView_validationErrorColor to
                    {
                        createValidationStatusSelectorInfo(
                            validationErrorColor,
                            validationTextErrorColor
                        )
                    },
                R.attr.inputView_validationWarningColor to
                    {
                        createValidationStatusSelectorInfo(
                            validationWarningColor,
                            validationTextWarningColor
                        )
                    },
                R.attr.inputView_validationSuccessColor to
                    {
                        createValidationStatusSelectorInfo(
                            validationSuccessColor,
                            validationTextSuccessColor
                        )
                    }
            )
        }
    }

    /**
     * Обновляет текст, видимость и цвет валидации.
     *
     * @param newValue новое значение [ValidationStatus].
     * @param update функция для обновления фона и текста валидации.
     */
    fun updateValidation(
        newValue: ValidationStatus,
        update: (newBackground: ColorStateList, newLayoutConfig: TextLayoutConfig) -> Unit
    ) {
        val validationStatusSelector =
            validationColors.getOrPut(newValue.tag, validationColorsProvider.getValue(newValue.tag))
        update(validationStatusSelector.selector) {
            this.text = newValue.message
            paint.color = validationStatusSelector.color
            isVisible = newValue.message.isNotEmpty()
        }
    }

    /**
     * Создаёт селектор для состояния валидации.
     *
     * @param color цвет состояния валидации.
     * @return новый селектор для поля ввода, см. [ColorStateList].
     */
    private fun createValidationStatusSelectorInfo(
        @ColorInt color: Int,
        @ColorInt colorText: Int,
        @ColorInt colorReadOnly: Int = 0,
        isSupportReadOnly: Boolean = false
    ): ValidationStatusSelector {
        val colorStateList = if (isSupportReadOnly) {
            ColorStateList(
                statesReadOnly,
                intArrayOf(
                    colorReadOnly,
                    color,
                    color
                )
            )
        } else {
            ColorStateList.valueOf(color)
        }

        return ValidationStatusSelector(colorText, colorStateList)
    }
}