/**
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.confirmation_dialog

import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.design_confirmation.R

private val OK_BUTTON =
    ButtonModel(
        ConfirmationButtonId.OK,
        R.string.design_confirmation_dialog_button_ok,
        PrimaryButtonStyle,
        true,
        R.id.confirmation_dialog_button_ok
    )
private val YES_BUTTON =
    ButtonModel(
        ConfirmationButtonId.YES,
        R.string.design_confirmation_dialog_button_yes,
        PrimaryButtonStyle,
        true,
        R.id.confirmation_dialog_button_yes
    )
private val NO_BUTTON =
    ButtonModel(
        ConfirmationButtonId.NO,
        R.string.design_confirmation_dialog_button_no,
        viewId = R.id.confirmation_dialog_button_no
    )
private val CANCEL_BUTTON =
    ButtonModel(
        ConfirmationButtonId.CANCEL,
        R.string.design_confirmation_dialog_button_cancel,
        viewId = R.id.confirmation_dialog_button_cancel
    )

/**
 * Предустановленный набор кнопок (Да, Нет, Отмена).
 */
val YES_NO_CANCEL = listOf(CANCEL_BUTTON, NO_BUTTON, YES_BUTTON)

/**
 * Предустановленный набор кнопок (Да, Нет).
 */

val YES_NO = listOf(NO_BUTTON, YES_BUTTON)

/**
 * Предустановленный набор кнопок (Да).
 */
val YES = listOf(YES_BUTTON)

/**
 * Предустановленный набор кнопок (Ok).
 */
val OK = listOf(OK_BUTTON)

/**
 * Предустановленный набор кнопок (Ok, Отмена).
 */
val OK_CANCEL = listOf(CANCEL_BUTTON, OK_BUTTON)
