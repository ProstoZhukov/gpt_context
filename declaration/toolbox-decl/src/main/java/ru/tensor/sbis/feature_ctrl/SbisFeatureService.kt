package ru.tensor.sbis.feature_ctrl

import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

/**
 * Класс для работы с сервисом Управление функционалом feature-ctrl.
 *
 * @author mb.kruglova
 */
interface SbisFeatureService {

    /**
     * Метод, который возвращает статус включенности фичи по её наименованию.
     *
     * @param featureName: Имя соответствующей фичи.
     * @return состояние включенности фичи.
     */
    fun isActive(featureName: String): Boolean

    /**
     * Метод, который возвращает значение фичи по её наименованию.
     *
     * @param featureName: Наименование фичи.
     * @return значение фичи. Может вернуть пустое значение, если у фичи оно отсутствует.
     */
    fun getValue(featureName: String): String?

    /**
     * Optional метод для подписки на получение информации о фичах.
     *
     * @param featureList: Список наименований фич, информацию о которых хотим получить.
     * @return [Observable] для подписки на получение информации о фичах.
     */
    fun getFeatureInfoObservable(featureList: List<String>): Observable<SbisFeatureInfo>

    /**
     * Optional метод получения Flow, в котором публикуется информация о фичах.
     *
     * @param featureList: Список наименований фич, информацию о которых хотим получить.
     * @return [Flow], в котором публикуются результаты загрузки информации о фичах.
     */
    fun getFeatureInfoFlow(featureList: List<String>): Flow<SbisFeatureInfo>
}