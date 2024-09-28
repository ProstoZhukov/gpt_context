package ru.tensor.sbis.list.base.data

import androidx.annotation.WorkerThread

/**
 * Обертка над микросервисом контроллера, который возвращает объект класса [SERVICE_RESULT] методами `list` и `refresh`
 * с переданным в них объектом [FILTER].
 */
interface ServiceWrapper<SERVICE_RESULT, FILTER> {


    /**
     * Передать микросервису [callback].
     * Обычно, возвращаемый объект должен иметь тип `ru.tensor.sbis.platform.generated.Subscription`.
     *
     * Реализация выглядит так:
     * ```
     * @WorkerThread
     * override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Subscription =
     *     controller.get().setDataRefreshCallback(object : DataRefreshCallback() {
     *         override fun execute(p0: HashMap<String, String>) {
     *             callback(p0)
     *         }
     *     }
     * )
     *```
     * Если микросервис не предоставляет механизм обновления, нужно вернуть `null`.
     */
    @WorkerThread
    fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any?

    /**
     * Получить данные от микросервиса методами `list` с аргументом [filter].
     *
     * Реализация выглядит так:
     * ```
     *  @WorkerThread
     *  override fun list(filter: Filter) = controller.get().list(filter)!!
     *  ```
     */
    @WorkerThread
    fun list(filter: FILTER): SERVICE_RESULT

    /**
     * Получить данные от микросервиса методами `refresh` с аргументом [filter].
     *
     * Реализация выглядит так:
     * ```
     *  @WorkerThread
     *  override fun refresh(filter: Filter) = controller.get().refresh(filter)!!
     *  ```
     */
    fun refresh(filter: FILTER, params: Map<String, String>): SERVICE_RESULT
}
