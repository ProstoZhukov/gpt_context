package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers

import ru.tensor.sbis.design.retail_models.Amount
import ru.tensor.sbis.design.retail_models.utils.PRICE_SCALE
import java.math.BigDecimal

/** Класс с набором утилит для работы с окнами оплаты. */
internal object PaymentUtils {

    /**
     * Рассчитать кол-во оставшихся к внесению денежных средств.
     *
     * @param totalCheckAmount сумма, которую необходимо внести пользователю.
     */
    fun Amount.calculateChangeValue(totalCheckAmount: BigDecimal = BigDecimal.ZERO) =
        total - totalCheckAmount

    /**
     * Преобразовать строковое значение числа в [BigDecimal].
     */
    fun convertUserInputToBigDecimalOrNull(inputText: String?): BigDecimal? = try {
        inputText
            ?.filterNot { it.isWhitespace() }
            ?.toBigDecimal()
            ?.setScale(PRICE_SCALE)
    } catch (e: NumberFormatException) {
        /* Ignored, DecimalFormatter прислал фигню, запишем '0'. */
        BigDecimal(0).setScale(PRICE_SCALE)
    } catch (e: ArithmeticException) {
        /* Ignored, DecimalFormatter прислал фигню, ждем. */
        null
    } catch (e: Exception) {
        /* Произошло что-то страшное, запишем '0'. */
        BigDecimal(0).setScale(PRICE_SCALE)
    }

}