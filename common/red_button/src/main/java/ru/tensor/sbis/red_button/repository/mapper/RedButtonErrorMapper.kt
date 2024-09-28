package ru.tensor.sbis.red_button.repository.mapper

import io.reactivex.functions.Function
import ru.tensor.sbis.CXX.SbisException
import ru.tensor.sbis.red_button.data.RedButtonError
import ru.tensor.sbis.red_button_service.generated.RedButtonClickEx
import ru.tensor.sbis.red_button_service.generated.RedButtonConfirmCodeEx
import ru.tensor.sbis.red_button_service.generated.RedButtonEx
import ru.tensor.sbis.red_button_service.generated.RedButtonMobilePhoneEx
import ru.tensor.sbis.red_button_service.generated.RedButtonNotClickEx
import ru.tensor.sbis.red_button_service.generated.RedButtonPinEx
import ru.tensor.sbis.red_button_service.generated.RedButtonWrongConnectionEx

/**
 * Маппер ошибок контроллера в кастомные ошибки
 *
 * @author ra.stepanov
 */
class RedButtonErrorMapper : Function<Throwable, RedButtonError> {

    /** @SelfDocumented */
    override fun apply(data: Throwable) = when (data) {
        is RedButtonWrongConnectionEx -> RedButtonError.NoInternet
        is RedButtonPinEx -> RedButtonError.Pin(data.errorUserMessage)
        is RedButtonConfirmCodeEx -> RedButtonError.ConfirmCode(data.errorUserMessage)
        is RedButtonMobilePhoneEx -> RedButtonError.MobilePhone(data.errorUserMessage)
        is RedButtonEx,
        is RedButtonClickEx,
        is RedButtonNotClickEx,
        is SbisException -> RedButtonError.General((data as SbisException).errorUserMessage)
        else -> RedButtonError.General(data.localizedMessage ?: "")
    }
}