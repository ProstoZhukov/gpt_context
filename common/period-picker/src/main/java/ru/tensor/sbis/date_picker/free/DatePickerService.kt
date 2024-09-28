package ru.tensor.sbis.date_picker.free

import io.reactivex.Observable
import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Сервис, используемый компонентом выбора периода.
 * Представляет собой обёртчку над контроллером и вся работа с ним должна быть инкапсулирована внутри.
 *
 * @author mb.kruglova
 */
interface DatePickerService : Feature {

    /**
     * Сохраняет период в истории.
     *
     * @param key ключ, управляющий доступом к истории.
     * @param period сохраняемый период.
     */
    fun saveHistory(key: String, period: Period)

    /**
     * Возвращает историю выбора периодов.
     *
     * @param key ключ, управляющий доступом к истории.
     * @return список ранее выбранных периодов.
     */
    fun getHistory(key: String): List<Period>

    /**
     * Возвращает выходные дни, производит их асинхронную синхронизацию для обновления кеша.
     *
     * @param period период, для которого требуется получить выходные дни.
     * @return список выходных дней в пределах заданного периода.
     */
    fun getDaysOff(period: Period): List<Date>

    /**
     * Метод для получения обновлений по выходным дням.
     *
     * @param period период, для которого требуется получать выходные дни.
     * @return [Observable], который излучает список дат-выходных.
     */
    fun createDaysOffObservable(period: Period): Observable<List<Calendar>>
}