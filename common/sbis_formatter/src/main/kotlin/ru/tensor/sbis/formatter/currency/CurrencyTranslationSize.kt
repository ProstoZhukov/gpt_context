package ru.tensor.sbis.formatter.currency

/**
 * Режим отображения длины названия валюты.
 *
 * @author ps.smirnyh
 */
enum class CurrencyTranslationSize {

    /** Значок валюты (100₽). */
    SIGN,

    /** Минимальный (100 р.; 100 р. 25 к.). */
    MIN,

    /** Средний (100 руб.; 100 руб. 25 коп). */
    MEDIUM,

    /** Полный (100 рублей; 100 рублей 25 копеек). */
    FULL;

    /**
     * Получить название валюты в зависимости от режима отображения длины названия.
     */
    internal fun getCurrencySuffix(): ResourceType = when (this) {
        SIGN -> ResourceType.StringRes("symbol")
        MIN -> ResourceType.StringRes("super_short_currency")
        MEDIUM -> ResourceType.StringRes("short_currency")
        FULL -> ResourceType.QuantityRes("currency_plural")
    }

    /**
     * Получить название дробной части валюты в зависимости от режима отображения длины названия.
     */
    internal fun getSubunitSuffix(mode: CurrencyTranslationMode): ResourceType {
        return when (this) {
            SIGN -> {
                when (mode) {
                    CurrencyTranslationMode.SPLIT -> ResourceType.StringRes("super_short_subunit")
                    CurrencyTranslationMode.SEPARATED -> ResourceType.StringRes("symbol")
                }
            }
            MIN -> ResourceType.StringRes("super_short_subunit")
            MEDIUM -> ResourceType.StringRes("short_subunit")
            FULL -> ResourceType.QuantityRes("subunit_plural")
        }
    }

}

/**
 * Тип ресурса для получения id ресурса по наименованию.
 *
 * @author ps.smirnyh
 */
internal sealed class ResourceType(val nameResource: String) {
    class StringRes(nameResource: String) : ResourceType(nameResource)

    class QuantityRes(nameResource: String) : ResourceType(nameResource)
}
