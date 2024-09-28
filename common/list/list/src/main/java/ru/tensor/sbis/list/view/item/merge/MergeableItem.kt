package ru.tensor.sbis.list.view.item.merge

/**
 * Реализация используется для обновления вью модели.
 * @param ITEM тип элемента для сравнения, должен быть того же типа, что и сам объект, реализующий интерфейс.
 *
 * ````
 * internal data class ContactListVM(
 *      val title: MutableLiveData<String>,
 *      val phone: MutableLiveData<String>,
 *      private val personId: UUID
 *  ) : MergeableItem<ContactListVM> {
 *
 *      override fun areTheSame(otherItem: ContactListVM) = personUuid == otherItem.personId
 *
 *      override fun mergeFrom(otherItem: ContactListVM) {
 *          title.value = otherItem.title.value
 *          phone.value = otherItem.phone.value
 *      }
 *
 *  }
 * ````
 */
interface MergeableItem<ITEM> {

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
     * Загрузить в текущий объект данные из [otherItem]. Назначение этого метода - обновление данных вью модели
     * без повторного биндинга этой модели во вью целиком. Обновление же полей вью элементов должно быть обеспечено за
     * счет использования LiveData.
     */
    fun mergeFrom(otherItem: ITEM)
}