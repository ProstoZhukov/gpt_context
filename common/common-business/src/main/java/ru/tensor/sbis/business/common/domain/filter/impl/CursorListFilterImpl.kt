package ru.tensor.sbis.business.common.domain.filter.impl

import androidx.annotation.VisibleForTesting
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.CursorBuilder
import ru.tensor.sbis.business.common.domain.filter.ListFilter
import ru.tensor.sbis.business.common.domain.filter.navigation.CursorNavigation
import ru.tensor.sbis.business.common.domain.filter.navigation.NavigationType
import timber.log.Timber

/**
 * Реализация списочного фильтра с "Курсорной" навигацией
 *
 * @property CPP_FILTER тип фильтра контроллера
 * @property CPP_CURSOR тип курсора фильтра
 *
 * @property currentOffset смещение ранее полученных разворотов
 * @property lastAppliedOffset последнее примененное смещение (необходимо для отката)
 */
abstract class CursorListFilterImpl<
        CPP_FILTER : Any,
        CPP_CURSOR : Any>(hashProvider: HashFilterProvider) :
    BaseListFilterImpl<CPP_FILTER, CPP_CURSOR>(hashProvider) {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var currentCursor: CPP_CURSOR? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var cursorBackward: CPP_CURSOR? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var currentOffset: Int = 0

    private var lastAppliedOffset: Int = 0

    /**
     * Для курсорной навигации убрана возможность определения ранее полученной страницы.
     * При необходимости можно поддержать введя понятие условных страниц.
     */
    override val shownPage: Boolean = false

    override val isFirstPageToSync: Boolean
        get() = currentCursor == null

    override val isFirstPageOnlySynced: Boolean
        get() {
            val cppFilter = lastCppFilter
            return cppFilter == null || getLastSyncNavigation(cppFilter).position == null
        }

    override val navigationType = NavigationType.POSITION

    /**
     * Двигает позицию навигации
     *
     * Курсор навигации обновляется если:
     * 1. [forceInc] == true
     * 1.1 [newPosition] получен после синхронизации, поскольку он может отличаться от того что был из кэша
     * 1.2 [newPosition] получен по результатам поиска, поскольку результаты поиска не кэшируются
     * 2. если курсор последнего синхронизированного фильтра != новому [newPosition], ведь события
     * синхронизации может и не быть
     *
     * @see ListFilter.incPosition
     *
     * TODO https://online.sbis.ru/opendoc.html?guid=0dce8af3-678e-41ed-a7b5-74fc57f5e8fb
     * можно упростить до обработки только по [forceInc] когда refresh-callback будет гарантирован
     */
    override fun incPosition(
        newPosition: CursorBuilder<CPP_CURSOR>,
        offset: Int,
        forceInc: Boolean
    ) {
        val lastSyncCppFilter = lastCppFilter ?: return
        val lastCursor = getLastSyncNavigation(lastSyncCppFilter).position
        /** Корректируем возможный сдвиг [currentOffset] если текущий курсор [currentCursor] еще не был использован
         * Не необходимо если курсор будет обновляться только по [forceInc] */
        if (forceInc && lastCursor != currentCursor) {
            currentOffset -= lastAppliedOffset
        }
        /** обновляем [currentCursor] если получили данные с облака ИЛИ если текущий курсор [currentCursor] уже был использован */
        if (forceInc || lastCursor == currentCursor) {
            currentCursor = newPosition.createCursor()
            currentOffset += offset
            lastAppliedOffset = offset
        }
    }

    override fun reset() {
        currentCursor = null
        cursorBackward = null
        currentOffset = 0
        resetSyncedState()
    }

    override fun getNavigation() = CursorNavigation<CPP_CURSOR>(
        limit = limit,
        offset = currentOffset,
        position = currentCursor,
        positionBackward = cursorBackward
    )

    /**
     * Получить курсорную навигацию из последнего синхронизируемого фильтра
     */
    abstract override fun getLastSyncNavigation(lastFilter: CPP_FILTER): CursorNavigation<CPP_CURSOR>

    override fun incPage() = Timber.e("Метод не должен быть использован при навигации по курсору")
}