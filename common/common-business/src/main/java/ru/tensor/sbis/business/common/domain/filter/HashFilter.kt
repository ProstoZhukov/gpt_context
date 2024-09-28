package ru.tensor.sbis.business.common.domain.filter

import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallback
import ru.tensor.sbis.crud.generated.DataRefreshCallback

/**
 * Интерфейс "хэш"-фильтра на UI.
 * Реализуется фильтром оберткой над фильтром контроллера что поддерживает подсчет хэша через [FilterHasher].
 * Используется для проверки коллбэка синхронизации [DataRefreshCallback] на таргет.
 */
interface HashFilter {

    /**
     * Проверяет связь коллбэка синхронизации [DataRefreshCallback] и текущего состояния фильтра
     *
     * @param callback информация о завершенном запросе на синхронизацию
     * @return Возвращает true если синхронизация завершена для текущего состояния фильтра
     */
    fun equalCallback(callback: RefreshCallback): Boolean
}