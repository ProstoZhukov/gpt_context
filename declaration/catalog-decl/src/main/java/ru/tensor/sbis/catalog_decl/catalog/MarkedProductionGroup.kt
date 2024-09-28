package ru.tensor.sbis.catalog_decl.catalog

/**
 * Группы маркированной продукции.
 *
 * @author sp.lomakin
 */
enum class MarkedProductionGroup {

    /**
     * 0 - табак
     */
    TABACCO,

    /**
     * 1 - обувь
     */
    SHOES,

    /**
     * 2 - текстиль
     */
    TEXTILE,

    /**
     * 3 - шины
     */
    TIRES,

    /**
     * 4 - лекарства (устарело)
     */
    DRUG,

    /**
     * 5 - парфюмерия
     */
    PERFUME,

    /**
     * 6 - камеры
     */
    CAMERA,

    /**
     * 7 - велосипеды
     */
    BICYCLE,

    /**
     * 8 - медицинские изделия
     */
    MEDICAL_DEVICES,

    /**
     * 9 - молочка
     */
    MILK,

    /**
     * 10 - вода
     */
    WATER,

    /**
     * 11 - альтернативный табак
     */
    TOBACCO_ALT,

    /**
     * 12 - никотиносодержащая продукция
     */
    NICOTINE,

    /**
     *  Не используется на онлайн
     */
    INVALID,

    /**
     * 14 - пиво
     */
    BEER,

    /**
     * 15 - Биологически активные добавки
     */
    BAA,

    /**
     * 16 - Антисептик
     */
    ANTISEPTIC,

    /**
     * 16 - Безалкогольное пиво
     */
    NON_ALCO_BEER,

    /**
     * 17 - Меха
     */
    FURS,

    /**
     * 18 - Безалкогольные напитки
     */
    SOFT_DRINKS,

    /**
     * 19 - Морепродукты
     */
    CAVIAR,

    /**
     * 20 - Табачное сырьё
     */
    TOBACCO_NICOTINE_RAW,

    /**
     * 21 - Консервы
     */
    CONSERVE,

    /**
     * 22 - Корма для животных
     */
    PETFOOD,

    /**
     * 23 - Растительные масла
     */
    VEGETABLEOIL,

    /**
     * 24 - Ветеринарные препараты
     */
    VETPHARMA,

    /**
     * 25 - Оптоволокно
     */
    OPTICAL_FIBER,

    /**
     * 26 - Кабельная продукция
     */
    CABLE_PRODUCTION,

    /**
     * 27 - Отопительные приборы
     */
    HEATING_DEVICES,

    /**
     * 28 - Радиоэлектроника
     */
    RADIO_ELECTRONICS,

    /**
     *  Максимально возможно на онлайне +1
     */
    MAX_ONLINE_VALUE;
}