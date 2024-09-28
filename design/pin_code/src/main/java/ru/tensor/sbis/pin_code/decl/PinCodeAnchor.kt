package ru.tensor.sbis.pin_code.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.VerticalAlignment

/**
 * Класс якоря, необходимого для закрепления пин-кода относительно вызывающей View.
 * @param viewTag тэг View, к которой необходимо закрепить пин-код.
 * @param verticalAlignment выравнивание по вертикали пин-кода относительно вызывающей View.
 * @param horizontalAlignment выравнивание по горизонтали пин-кода относительно вызывающей View.
 *
 * @author mb.kruglova
 */
@Parcelize
class PinCodeAnchor(
    val viewTag: String,
    val verticalAlignment: VerticalAlignment? = null,
    val horizontalAlignment: HorizontalAlignment? = null
) : Parcelable