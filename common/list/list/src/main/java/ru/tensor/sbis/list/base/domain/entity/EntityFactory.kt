package ru.tensor.sbis.list.base.domain.entity

/**
 * Создает "бизнес модель"(БМ) и производит обновление данных БМ данными микросервиса.
 * @param ENTITY тип БМ
 * @param SERVICE_RESULT "сырые" данные, получаемые из микросервиса методами list и refresh
 */
interface EntityFactory<ENTITY, SERVICE_RESULT> {

    /**
     * Создать новую БМ.
     */
    fun createEntity(): ENTITY

    /**
     * Добавление в уже имеющуюся ранее БМ новой порции данных, полученную пагинацией.
     *
     * Пример:
     *  override fun updateEntityWithData(
     *      page: Int,
     *      entity: ContactListEntity,
     *      serviceResult: ListResultOfPerson
     *  ) {
     *      entity.update(page, serviceResult)
     *  }
     * @param entity БМ для обновления.
     * @param serviceResult новые данные из микросервиса.
     */
    fun updateEntityWithData(page: Int, entity: ENTITY, serviceResult: SERVICE_RESULT)

    fun updateEntityWithData(entity: ENTITY, serviceResults: List<Pair<Int, SERVICE_RESULT>>) {
        serviceResults.forEach {
            updateEntityWithData(it.first, entity, it.second)
        }
    }
}

//todo Избавить от этого класса.