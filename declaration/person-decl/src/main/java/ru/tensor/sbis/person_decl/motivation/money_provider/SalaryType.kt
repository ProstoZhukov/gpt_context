package ru.tensor.sbis.person_decl.motivation.money_provider


/** тип выбранного подсчета зарплаты */
enum class SalaryType {
    /** На руки */
    PURE,

    /** Без НДФЛ */
    WITHOUT_TAX,

    /** С НДФЛ */
    WITH_TAX,
}
