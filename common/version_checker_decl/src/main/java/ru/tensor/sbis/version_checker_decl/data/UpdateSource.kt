package ru.tensor.sbis.version_checker_decl.data

/**
 * Источник обновления приложения семейства СБИС
 * Информация по редким магазинам(касса): https://online.sbis.ru/opendoc.html?guid=f3b29400-ef59-441b-8837-67644216a74e
 *
 * @author as.chadov
 */
@Suppress("SpellCheckingInspection")
enum class UpdateSource {
    /** СБИС маркет */
    SBIS_MARKET,

    /** Маркет от гугл */
    GOOGLE_PLAY_STORE,

    /** Маркет вендора HUAWEI */
    APP_GALLERY,

    /** Маркет вендора SAMSUNG */
    GALAXY_STORE,

    /** Маркет вендора XIAOMI */
    GET_APPS,

    /** Маркет RuStore */
    RU_STORE,

    /**
     * Маркет NashStore
     * Предварительно требуется регистрация в сторе и логин в МП стора
     * Номер любой, верификация только по email
     */
    NASH_STORE,

    /**
     * На устройствах MS-POS/sunmi (кассовые терминалы)
     * Не тестировалось(!)
     */
    SUNMI_STORE,

    /**
     * На устройствах Нева/armax (кассовые терминалы)
     * Не тестировалось(!)
     */
    ARMAX,

    /**
     * На устройствах MS-POS/Tianyu (кассовые терминалы)
     * Не тестировалось(!)
     */
    TIANYU,

    /**
     * На устройствах MS-POS/newland (кассовые терминалы)
     * Не тестировалось(!)
     */
    NEWLAND,

    /** СБИС онлайн */
    SBIS_ONLINE;
}
