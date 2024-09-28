package ru.tensor.sbis.catalog_decl.catalog

/**
 * Тип сортировки каталога
 *
 * @author as.mozgolin
 */
enum class CatalogSortType {
    /** сортировку выбирает контроллер исходя из фильтра и сущности */
    DEFAULT,
    /** по алфавиту */
    ALPHABET,
    /** по алфавиту (в обратном порядке) */
    ALPHABET_DESC,
    /** пользовательская сортировка */
    USER,
    /** пользовательская сортировка (в обратном порядке) */
    USER_DESC
}