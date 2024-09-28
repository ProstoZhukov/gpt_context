package ru.tensor.sbis.cashboxes_lite_decl.model

/**
 * Цены по умолчанию на табак
 */
enum class DefaultTobaccoPrice {
    MAXIMUM,
    CURRENT
}

/**
 * Результат проверки цены табака
 * @param defaultTobaccoPrice - цена табака по умолчанию [DefaultTobaccoPrice], которую нужно установить для варианта [SkipPriceChecking]
 */
sealed class CheckTobaccoPriceResult(val defaultTobaccoPrice: DefaultTobaccoPrice?)

/** Необходимо пропустить проверку и применить текущую цену */
class SkipPriceChecking(defaultTobaccoPrice: DefaultTobaccoPrice?) : CheckTobaccoPriceResult(defaultTobaccoPrice)

/** Товар не является табаком, проверка МРЦ не требуется */
object NotTobacco : CheckTobaccoPriceResult(null)

/** Необходимо выбрать цену табака - текущую или МРЦ */
object CheckTobaccoPrice : CheckTobaccoPriceResult(null)

/** Необходимо выбрать цену табака при МРЦ ниже минимально допустимой */
object CheckUnifiedMinimumTobaccoPrice : CheckTobaccoPriceResult(null)

/** Необходимо добавить товар с текущей ценой */
val CheckTobaccoPriceResult?.needToAddWithCurrentPrice: Boolean
    get() {
        return this == null || this is NotTobacco || this is SkipPriceChecking &&
            defaultTobaccoPrice == DefaultTobaccoPrice.CURRENT
    }

/** Необходимо добавить товар с максимальной розничной ценой */
val CheckTobaccoPriceResult?.needToAddWithMaxRetailPrice: Boolean
    get() {
        return this is SkipPriceChecking && defaultTobaccoPrice == DefaultTobaccoPrice.MAXIMUM
    }

/** Необходимо предоставить выбор пользователю: добавить с единой минимальной ценой или МРЦ */
val CheckTobaccoPriceResult?.needToAskUserAddWithUnifiedMinimumPriceOrMaxRetailPrice: Boolean
    get() {
        return this is CheckUnifiedMinimumTobaccoPrice
    }

/** Необходимо предоставить выбор пользователю */
val CheckTobaccoPriceResult?.needToAskUser: Boolean
    get() {
        return this is CheckTobaccoPrice
    }

/** Необходимо добавить товар с максимальной розничной ценой */
const val SET_THRESHOLD_RETAIL_PRICE_ACTION_ID = "SET_THRESHOLD_RETAIL_PRICE_ACTION_ID"

/** Необходимо добавить товар с текущей ценой */
const val SKIP_THRESHOLD_RETAIL_PRICE_ACTION_ID = "SKIP_THRESHOLD_RETAIL_PRICE_ACTION_ID"

/** Необходимо добавить товар с минимально допустимой ценой */
const val SET_UNIFIED_MINIMUM_PRICE_ACTION_ID = "SET_UNIFIED_MINIMUM_PRICE_ACTION_ID"