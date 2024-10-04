package ru.tensor.sbis.design.container

/**
 * Фабрика для создания контента, если контент реализует Parcelable контейнер будет восстанавливаться при повороте
 * @author ma.kolpakov
 */
interface ContentCreator<out CONTENT : Content> {
    fun createContent(): CONTENT
}