package ru.tensor.sbis.pin_code.decl

import android.os.Parcelable
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.pin_code.decl.ConfirmationType.INPUT_COMPLETION

/**
 * Конфигурация для отображения компонента ввода пин-кода.
 * @param headerIcon иконка в шапке.
 * @param header строка, текстовый заголовок в шапке.
 * @param description информационное сообщение.
 * @param isDefaultField нужно ли использовать обычное поле ввода вместо поля ввода с точками
 * @param isMaskedCode нужно ли скрывать вводимые пользователем символы.
 * @param isPhoneCode нужно ли использовать поле ввода телефона с фиксированной маской, где доступны только 4 последние цифры.
 * @param isNumericKeyboard разрешен ли ввод только с цифровой клавиатуры. По умолчанию true.
 * @param transportType тип доставки кода. По умолчанию [PinCodeTransportType.NONE]
 * @param codeLength длинна вводимого пин-кода. Допустимы значения от 4 до 30. По умолчанию 4.
 * @param confirmationType тип подтверждения завершения ввода пин-кода [ConfirmationType]. По умолчанию [ConfirmationType.INPUT_COMPLETION]
 * @param inputHint дополнительная подсказка для поля ввода неограниченного пин-кода. Отобразится в случае [isDefaultField] true
 * @param hasPeriod имеет ли пин-код период действия.
 * @param descriptionRes id ресурса для информационного сообщения.
 *
 * @throws IllegalArgumentException если не прошла валидация [codeLength]
 *
 * @author mb.kruglova
 */
@Parcelize
class PinCodeConfiguration(
    val headerIcon: SbisMobileIcon.Icon? = null,
    @StringRes
    val header: Int = ID_NULL,
    val description: String = "",
    val isDefaultField: Boolean = false,
    val isMaskedCode: Boolean = false,
    val isPhoneCode: Boolean = false,
    val isNumericKeyboard: Boolean = true,
    val transportType: PinCodeTransportType = PinCodeTransportType.NONE,
    @IntRange(from = MIN_CODE_LENGTH, to = MAX_CODE_LENGTH)
    val codeLength: Int = MIN_CODE_LENGTH.toInt(),
    val confirmationType: ConfirmationType = INPUT_COMPLETION,
    @StringRes
    val inputHint: Int = ID_NULL,
    val hasPeriod: Boolean = false,
    @StringRes
    val descriptionRes: Int = ID_NULL,
    @StringRes
    val customLinkButtonTitle: Int = ID_NULL
) : Parcelable {

    init {
        require(codeLength in MIN_CODE_LENGTH..MAX_CODE_LENGTH) {
            "codeLength must be declared in the range from $MIN_CODE_LENGTH to $MAX_CODE_LENGTH"
        }
    }
}

private const val MIN_CODE_LENGTH = 4L
private const val MAX_CODE_LENGTH = 30L