package ru.tensor.sbis.list.base.domain.fetcher

import androidx.annotation.CheckResult
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.domain.boundary.Repository
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity

/**
 * Вспомогательный класс для инкапсуляции логики создания подписок на репозитории. Выбирает правильный метод
 * для получения "бизнес модели"(БМ).
 *
 * @param ENTITY тип БМ.
 * @property repository репозиторий.
 * @property subscriber создатель подписки для формирования БМ и действий на [View].
 */
internal class RepositoryFetcher<ENTITY, FILTER>(
    private val repository: Repository<ENTITY, FILTER>,
    private val subscriber: EntityCreationSubscriber<ENTITY>
) where ENTITY : ListScreenEntity,
        ENTITY : FilterProvider<FILTER> {
    /**
     * Подписаться на обновление БМ данными из результата следующей подгрузки.
     *
     * @param entity БМ для добавления в нее страницы.
     * @param view отображение БМ.
     */
    @CheckResult
    internal fun updateListEntity(
        entity: ENTITY,
        filterAndPageProvider: FilterAndPageProvider<FILTER>,
        view: View<ENTITY>
    ) =
        subscriber.subscribeOnFetchingResult(
            repository.update(entity, filterAndPageProvider),
            view
        )

    fun clean() {
        repository.destroy()
    }
}