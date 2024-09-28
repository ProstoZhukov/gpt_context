package ru.tensor.sbis.cashboxes_lite_decl.government_prices

/** Ключи и тэги диалогов гос.ограничения цен на товары. */
object GovernmentPriceDialogKeys {

    /** Экшен кнопки "Продолжить с нарушением" в сценарии "Максимальная розничная цена ниже минимальной по закону". */
    const val GOVERNMENT_DIALOG_ACCEPT_EMP_MORE_MRC = "GOVERNMENT_DIALOG_ACCEPT_EMP_MORE_MRC"

    /** Экшен кнопки "Установить %s" в сценарии "Цена отличается от максимальной розничной". */
    const val GOVERNMENT_DIALOG_ACCEPT_PRICE_NOT_EQUALS_MAXIMUM_PRICE_FROM_PRODUCT_CODE =
        "GOVERNMENT_DIALOG_ACCEPT_PRICE_NOT_EQUALS_MAXIMUM_PRICE_FROM_PRODUCT_CODE"

    /** Экшен кнопки "Продолжить с нарушением" в сценарии "Цена отличается от максимальной розничной". */
    const val GOVERNMENT_DIALOG_CONTINUE_WITH_VIOLATION_WHEN_PRICE_NOT_EQUAL_MAXIMUM_PRICE_FROM_PROD_CODE =
        "CONTINUE_WHEN_PRICE_NOT_EQUALS_MAXIMUM_PRICE_FROM_PRODUCT_CODE_CONTINUE_WITH_VIOLATION"

    /** Экшен кнопки "Установить %s" в сценарии "Цена ниже минимальной по закону". */
    const val GOVERNMENT_DIALOG_ACCEPT_PRICE_LESS_MINIMAL_GOVERNMENT_PRICE =
        "GOVERNMENT_DIALOG_ACCEPT_PRICE_LESS_MINIMAL_GOVERNMENT_PRICE"

    /** Экшен кнопки "Продолжить с нарушением" в сценарии "Цена ниже минимальной по закону". */
    const val GOVERNMENT_DIALOG_CONTINUE_WITH_VIOLATION_WHEN_PRICE_LESS_MINIMAL_GOVERNMENT_PRICE =
        "GOVERNMENT_DIALOG_CONTINUE_WITH_VIOLATION_WHEN_PRICE_LESS_MINIMAL_GOVERNMENT_PRICE"

    /** Тэг диалога "Максимальная розничная цена ниже минимальной по закону". */
    const val DIALOG_TAG_PRODUCT_CODE_MAXIMAL_PRICE_PRICE_LESS_THAT_MINIMAL_GOVERNMENT =
        "DIALOG_TAG_PRODUCT_CODE_MAXIMAL_PRICE_PRICE_LESS_THAT_MINIMAL_GOVERNMENT"

    /** Тэг диалога "Цена отличается от максимальной розничной". */
    const val DIALOG_TAG_MERCH_PRICE_NOT_EQUALS_MAXIMUM_PRICE_FROM_PRODUCT_CODE =
        "DIALOG_TAG_MERCH_PRICE_NOT_EQUALS_MAXIMUM_PRICE_FROM_PRODUCT_CODE"

    /** Тэг диалога "Цена ниже минимальной по закону". */
    const val DIALOG_TAG_MERCH_PRICE_LESS_MINIMAL_GOVERNMENT_PRICE =
        "DIALOG_TAG_MERCH_PRICE_LESS_MINIMAL_GOVERNMENT_PRICE"

    /** Выбранная цена. */
    const val GOVERNMENT_DIALOG_PRICE_INCOMING_DATA = "GOVERNMENT_DIALOG_PRICE_INCOMING_DATA"
}