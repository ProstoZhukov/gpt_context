package ru.tensor.sbis.design.view.input.mask.ip

import android.text.InputType
import android.text.method.NumberKeyListener

/**
 * [NumberKeyListener] для [IpInputView] с добавлением символа "."
 *
 * @author ia.nikitin
 */
internal object IpInputKeyListener : NumberKeyListener() {

    override fun getInputType(): Int = InputType.TYPE_CLASS_NUMBER

    override fun getAcceptedChars(): CharArray = CHARACTERS

    private val CHARACTERS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.')
}