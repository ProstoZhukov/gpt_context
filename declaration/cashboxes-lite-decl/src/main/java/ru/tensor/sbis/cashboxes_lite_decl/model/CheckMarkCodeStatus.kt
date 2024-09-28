package ru.tensor.sbis.cashboxes_lite_decl.model

/** Перечисление возможных статусов проверки КМ. */
enum class CheckMarkCodeStatus(val statusType: CheckMarkCodeStatusType) {

    /** Позитивный результат */
    SUCCESS(CheckMarkCodeStatusType.NOT_ERROR),

    /** Код не найден в Маркировке */
    ERROR_NOT_FOUND(CheckMarkCodeStatusType.VALID_ERROR),

    /** Код не задействован. */
    ERROR_KM_NOT_UTILISED(CheckMarkCodeStatusType.VALID_ERROR),

    /** КМ не по формату или со сбойной крипточастью. */
    ERROR_KM_BAD_FORMAT(CheckMarkCodeStatusType.VALID_ERROR),

    /** КМ выведен из оборота. */
    ERROR_ALREADY_SOLD(CheckMarkCodeStatusType.VALID_ERROR),

    /** КМ заблокирован к продаже органом гос власти. */
    ERROR_BLOCKED_BY_GOV(CheckMarkCodeStatusType.VALID_ERROR),

    /** КМ не в обороте. */
    ERROR_NOT_SOLDABLE(CheckMarkCodeStatusType.VALID_ERROR),

    /** Истек срок годности. */
    ERROR_EXPIRED(CheckMarkCodeStatusType.VALID_ERROR),

    /** МРЦ ниже ЕМЦ. */
    ERROR_MRC_LESS_EMC(CheckMarkCodeStatusType.VALID_ERROR),

    /** Цена ниже МРЦ. */
    ERROR_PRICE_LESS_MRC(CheckMarkCodeStatusType.VALID_ERROR),

    /** Цена ниже ЕМЦ. */
    ERROR_PRICE_LESS_EMC(CheckMarkCodeStatusType.VALID_ERROR),

    /** Код не принадлежит продавцу. */
    ERROR_OTHER_SELLER(CheckMarkCodeStatusType.VALID_ERROR),

    /** Нет связи (код 1000). */
    ERROR_NO_CONNECTION(CheckMarkCodeStatusType.CRITICAL_FAILURE),

    /** Ошибка при инициализации соединения с ЧЗ (код 401). */
    ERROR_INIT_CONNECTION(CheckMarkCodeStatusType.CRITICAL_FAILURE),

    /** Авария сервиса ЧЗ (код 203). */
    ERROR_CDN_ACCIDENT(CheckMarkCodeStatusType.CRITICAL_FAILURE),

    /** Внутренняя ошибка сервиса (код 401). */
    ERROR_INTERNAL_SERVICE_ERROR(CheckMarkCodeStatusType.CRITICAL_FAILURE),

    /** Некая внешняя ошибка (код 429). */
    ERROR_ANY_EXTERNAL(CheckMarkCodeStatusType.CRITICAL_FAILURE),

    /** Неизвестная ошибка (кода 4XX, кроме 401 и 429) */
    ERROR_UNDEFINED(CheckMarkCodeStatusType.CRITICAL_FAILURE);

    companion object {

        /** Требуется-ли отображение статуса проверки КМ в номенклатуре. */
        fun isStatusMessageRequired(value: Int): Boolean {
            return when (value) {
                in arrayOf(
                    SUCCESS.ordinal,
                    ERROR_CDN_ACCIDENT.ordinal,
                    ERROR_INTERNAL_SERVICE_ERROR.ordinal,
                    ERROR_ANY_EXTERNAL.ordinal,
                    ERROR_UNDEFINED.ordinal,
                    ERROR_NO_CONNECTION.ordinal
                ) -> false
                else -> true
            }
        }

        /** Статус является не успешным. */
        fun isStatusUnsuccessful(value: Int): Boolean = value != SUCCESS.ordinal

        /**@SelfDocumented*/
        fun fromOrdinal(ordinal: Int): CheckMarkCodeStatus? = CheckMarkCodeStatus.values().getOrNull(ordinal)
    }

    /** Серьёзность результата проверки кода маркировки. */
    enum class CheckMarkCodeStatusType {

        /**@SelfDocumented*/
        NOT_ERROR,

        /** Ошибка с кодом 2xx, чаще всего не серьёзная. */
        VALID_ERROR,

        /** Критический сбой, серьёзный. */
        CRITICAL_FAILURE
    }
}