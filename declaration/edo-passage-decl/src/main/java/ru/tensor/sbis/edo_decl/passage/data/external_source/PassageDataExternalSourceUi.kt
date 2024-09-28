package ru.tensor.sbis.edo_decl.passage.data.external_source

import androidx.fragment.app.FragmentManager

/**
 * Реализация [PassageDataExternalSource] с привязкой к [FragmentManager]
 * Можно использовать, если для предоставления данных для переходу необходимо показать фрагмент
 *
 * Реализация должна синхронизировать запрос дополнительных данных и установку/очистку [FragmentManager]
 * Может быть несколько ситуаций
 * 1. Данные запрошены, [FragmentManager] установлен
 * Никаких особенностей, фрагмент отображается во время запроса
 * 2. Данные запрошены, [FragmentManager] не установлен
 * Необходимо показать фрагмент на следующем [setFragmentManager]
 * 3. Данные запрошены, во время ожидания повёрнут экран
 * На следующем [setFragmentManager] фрагмент нужно найти и связать его с [PassageDataExternalSource.Callback]
 * 4. Данные запрошены, во время ожидания приложение было убито системой
 * На следующем [setFragmentManager] фрагмент нужно найти и связать его с [PassageDataExternalSource.Callback]
 * Если [PassageDataExternalSource.Callback] отсутствует, т.е. данные ещё не были запрошены повторно, то ожидать запроса,
 * это обязанность вызывающей стороны
 *
 * @author sa.nikitin
 */
interface PassageDataExternalSourceUi : PassageDataExternalSource {

    /**
     * Установить [FragmentManager]
     * Вызовется на onResume
     */
    fun setFragmentManager(fragmentManager: FragmentManager)

    /**
     * Удалить [FragmentManager], т.е. очистить ссылку на него
     * Вызовется на onPause
     */
    fun removeFragmentManager()
}