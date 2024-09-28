package ru.tensor.sbis.pin_code.decl

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.pin_code.R

/**
 * Сценарий использования компонента.
 * @param configuration конфигурация для отображения компонента ввода пин-кода.
 *
 * @author mb.kruglova
 */
sealed class PinCodeUseCase(internal val configuration: PinCodeConfiguration) : Parcelable {

    /**
     * Сценарий для создания пин-кода. Будет показана зеленая кнопка подтверждения для согласия.
     * @param description информационное сообщение для пользователя.
     */
    @Parcelize
    class Create(private val description: String) : PinCodeUseCase(
        PinCodeConfiguration(
            header = R.string.pin_code_create_pin_header,
            description = description,
            isMaskedCode = false,
            isDefaultField = false,
            confirmationType = ConfirmationType.BUTTON,
            codeLength = CREATE_PIN_CODE_LENGTH,
            transportType = PinCodeTransportType.NONE
        )
    )

    /**
     * Сценарий для ввода кода подтверждения. Без вспомогательных звонков/смс/писем на почту.
     * @param description информационное сообщение для пользователя.
     */
    @Parcelize
    class SimpleConfirm(private val description: String) : PinCodeUseCase(
        PinCodeConfiguration(
            header = R.string.pin_code_enter_pin_header,
            description = description,
            isMaskedCode = false,
            isDefaultField = false,
            confirmationType = ConfirmationType.BUTTON,
            codeLength = CREATE_PIN_CODE_LENGTH,
            transportType = PinCodeTransportType.NONE
        )
    )

    /**
     * Сценарий для ввода кода подтверждения полученного по смс.
     * @param description информационное сообщение для пользователя.
     * @param deprecatedCodeLength если true то поле ввода будет содержать 5 символов для ввода пин-кода иначе 4. Временный флаг пока везде не перейдут на 4 символа.
     * @param timeout время для повторного запроса, если стандартное не подходит.
     * @param customLinkButtonTitle текст для кастомной кнопки-ссылки.
     */
    @Parcelize
    class ConfirmBySms(
        private val description: String,
        private val deprecatedCodeLength: Boolean = false,
        private val timeout: Long? = null,
        @StringRes
        private val customLinkButtonTitle: Int = ID_NULL
    ) : PinCodeUseCase(
        PinCodeConfiguration(
            headerIcon = SbisMobileIcon.Icon.smi_envelope,
            header = R.string.pin_code_enter_sms_code_header,
            description = description,
            confirmationType = ConfirmationType.INPUT_COMPLETION,
            isMaskedCode = false,
            isDefaultField = false,
            codeLength = if (deprecatedCodeLength) DEPRECATED_SMS_PIN_CODE_LENGTH else DEFAULT_SMS_PIN_CODE_LENGTH,
            transportType = if (timeout != null) PinCodeTransportType.CUSTOM(timeout) else PinCodeTransportType.SMS,
            customLinkButtonTitle = customLinkButtonTitle
        )
    )

    /**
     * Сценарий для ввода кода подтверждения полученного посредством последних цифр позвонившего номера.
     * @param description информационное сообщение для пользователя.
     * @param customLinkButtonTitle текст для кастомной кнопки-ссылки.
     */
    @Parcelize
    class ConfirmByCall(
        private val description: String,
        @StringRes
        private val customLinkButtonTitle: Int = ID_NULL
    ) : PinCodeUseCase(
        PinCodeConfiguration(
            headerIcon = SbisMobileIcon.Icon.smi_PhoneCell1,
            header = R.string.pin_code_we_are_calling_you_header,
            description = description,
            confirmationType = ConfirmationType.INPUT_COMPLETION,
            isMaskedCode = false,
            isDefaultField = false,
            transportType = PinCodeTransportType.CALL,
            customLinkButtonTitle = customLinkButtonTitle
        )
    )

    /**
     * Сценарий для ввода кода восстановления полученного через почту.
     * @param description информационное сообщение для пользователя.
     * @param customLinkButtonTitle текст для кастомной кнопки-ссылки.
     */
    @Parcelize
    class ConfirmRecoveryByEmail(
        private val description: String,
        @StringRes
        private val customLinkButtonTitle: Int = ID_NULL
    ) : PinCodeUseCase(
        PinCodeConfiguration(
            header = R.string.pin_code_recovery_code_header,
            description = description,
            confirmationType = ConfirmationType.INPUT_COMPLETION,
            isMaskedCode = false,
            isDefaultField = false,
            transportType = PinCodeTransportType.EMAIL,
            customLinkButtonTitle = customLinkButtonTitle
        )
    )

    /**
     * Сценарий для ввода кода полученного через почту.
     * @param description информационное сообщение для пользователя.
     * @param customLinkButtonTitle текст для кастомной кнопки-ссылки.
     */
    @Parcelize
    class ConfirmByEmail(
        private val description: String,
        @StringRes
        private val customLinkButtonTitle: Int = ID_NULL
    ) : PinCodeUseCase(
        PinCodeConfiguration(
            header = R.string.pin_code_enter_code_header,
            description = description,
            confirmationType = ConfirmationType.INPUT_COMPLETION,
            isMaskedCode = false,
            isDefaultField = false,
            transportType = PinCodeTransportType.EMAIL,
            customLinkButtonTitle = customLinkButtonTitle
        )
    )

    /**
     * Сценарий для подтверждения подписи. Будет отображено дефолтное поле ввода кода без ограничения на минимальный ввод символов.
     * @param description информационное сообщение для пользователя.
     * @param hasPeriod имеет ли пин-код период действия.
     * @param customLinkButtonTitle текст для кастомной кнопки-ссылки.
     */
    @Parcelize
    class ConfirmSignature(
        private val description: String,
        private val hasPeriod: Boolean = false,
        @StringRes
        private val customLinkButtonTitle: Int = ID_NULL
    ) : PinCodeUseCase(
        PinCodeConfiguration(
            header = R.string.pin_code_confirm_signature_header,
            description = description,
            confirmationType = ConfirmationType.BUTTON,
            codeLength = SIGNATURE_CODE_LENGTH,
            isMaskedCode = true,
            isDefaultField = true,
            isNumericKeyboard = false,
            transportType = PinCodeTransportType.NONE,
            inputHint = R.string.pin_code_confirmation_code_hint,
            hasPeriod = hasPeriod,
            customLinkButtonTitle = customLinkButtonTitle
        )
    )

    /**
     * Сценарий для ввода одноразового пароля подтверждения неизвестной длины, например от Клиент_Банка.
     * @param description информационное сообщение для пользователя.
     * @param isMaskedCode true скрывать ввод без возможности просмотра, false скрывать с возможностью просмотра по иконке "глаз"
     */
    @Parcelize
    class OtpConfirm(private val description: String, private val isMaskedCode: Boolean = true) : PinCodeUseCase(
        PinCodeConfiguration(
            header = R.string.pin_code_enter_pin_header,
            description = description,
            confirmationType = ConfirmationType.BUTTON,
            codeLength = OTP_CODE_LENGTH,
            isMaskedCode = isMaskedCode,
            isDefaultField = true,
            transportType = PinCodeTransportType.SMS
        )
    )

    /**
     * Сценарий для ввода кода подтверждения, полученного посредством последних цифр входящего номера.
     * @param description информационное сообщение для пользователя.
     * @param customLinkButtonTitle текст для кастомной кнопки-ссылки.
     */
    @Parcelize
    class ConfirmByPhone(
        private val description: String = "",
        @StringRes
        private val customLinkButtonTitle: Int = ID_NULL
    ) : PinCodeUseCase(
        PinCodeConfiguration(
            headerIcon = SbisMobileIcon.Icon.smi_phoneRing,
            header = R.string.pin_code_we_are_calling_you_header,
            description = description,
            descriptionRes = R.string.pin_code_we_are_calling_you_body,
            confirmationType = ConfirmationType.INPUT_COMPLETION,
            isPhoneCode = true,
            isMaskedCode = false,
            isDefaultField = false,
            transportType = PinCodeTransportType.CALL,
            customLinkButtonTitle = customLinkButtonTitle
        )
    )

    /**
     * Полностью кастомная конфигурация на основе [PinCodeConfiguration]
     */
    @Parcelize
    class Custom(private val config: PinCodeConfiguration) : PinCodeUseCase(config)
}

private const val OTP_CODE_LENGTH = 8
private const val SIGNATURE_CODE_LENGTH = 30
private const val CREATE_PIN_CODE_LENGTH = 5
private const val DEFAULT_SMS_PIN_CODE_LENGTH = 4
private const val DEPRECATED_SMS_PIN_CODE_LENGTH = 5