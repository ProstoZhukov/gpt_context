package ru.tensor.sbis.info_decl.news.ui.config

/**
 * Режим отображения реестра новостей.
 *
 * @param columnConstraint настройки ограничений по количеству отображаемых колонок
 *
 * @author am.boldinov
 */
sealed class NewsListLayoutMode(val columnConstraint: ColumnConstraint) {

    /**
     * Отображение плоским линейным списком.
     */
    object Linear : NewsListLayoutMode(ColumnConstraint.Fixed(1))

    /**
     * Отображение плиткой.
     */
    class Grid(columnConstraint: ColumnConstraint) : NewsListLayoutMode(columnConstraint)
}

/**
 * Настройки ограничений по количеству отображаемых колонок.
 */
sealed class ColumnConstraint {

    /**
     * Фиксированное количество колонок.
     * Необходимо учитывать, что при большом количестве колонок на телефоне контент может перестать помещаться.
     */
    class Fixed(val count: Int) : ColumnConstraint()

    /**
     * Автоматическое изменение количества колонок в зависимости от ширины экрана.
     */
    object Auto : ColumnConstraint()
}

