package ru.tensor.sbis.list.view.item.comparator

import androidx.recyclerview.widget.DiffUtil

/**
 * Реализация используется для утилиты [DiffUtil], используемой в компоненте списка [ru.tensor.sbis.list.view.SbisList].
 * @param ITEM тип элемента для сравнения, должен быть того же типа, что и сам объект, реализующий интерфейс.
 *
 * ````
 * internal data class ContactListVM(
 *      val title: String,
 *      val phone: String?,
 *      private val personId: UUID
 *  ) : ComparableItem<ContactListVM> {
 *
 *      override fun areTheSame(otherItem: ContactListVM) = personId == otherItem.personId

 *      override fun hasTheSameContent(otherItem: ContactListVM) = this == otherItem
 *  }
 * ````
 */
interface ComparableItem<ITEM> {
    /**
     * Сравнить, является ли этот элемент одной и той же сущностью что и переданный. Сравнение не должно проводиться
     * по ссылке на объект, но по какому-то полю, однозначно идентифицирующему объект - uuid, id,
     * уникальный заголовок в пределах списка.
     * Гарантированно, что элемент для сравнения будет того же типа.
     * @param otherItem ITEM объект, с которым будет сравниваться текущий.
     * @return Boolean возвращает true при положительном результате сравнения.
     */
    fun areTheSame(otherItem: ITEM): Boolean

    /**
     * Сравнить уже сам контент и, если он отличается, то, в последствии, [DiffUtil] выполнит замену элемента списка на новый
     * и выполнится перерисовка строки списка.
     * Гарантированно, что элемент для сравнения будет того же типа.
     * @param otherItem ITEM
     * @return Boolean возвращает true при положительном результате сравнения.
     */
    fun hasTheSameContent(otherItem: ITEM): Boolean = this == otherItem
}