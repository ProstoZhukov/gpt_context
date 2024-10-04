package ru.tensor.sbis.design.selection.bl.vm.selection

import androidx.annotation.AnyThread
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import io.reactivex.Observable

/**
 * Базовый интерфейс вьюмодели выбора
 *
 * @author ma.kolpakov
 */
internal interface SelectionViewModel<SELECTION> {

    /**
     * Подписка на актуальный выбор, список выбранных элементов. Публикуется для отображения в панели превью и обработки
     * других правил бизнес логики. Выбор не публикуется до появления в нём элементов
     */
    val selection: Observable<SELECTION>

    /**
     * Результат выбора. Если пользователь отменил выбор, публикуется событие [MaybeEmitter.onComplete]
     */
    val result: Maybe<SELECTION>

    /**
     * Отмена выбора
     */
    fun cancel()

    /**
     * Синхронизация выбранных элементов с последними загруженными данными. Процедура необходима, чтобы изменения в
     * прикладных моделях применялись к уже выбранным.
     * Обновление списка внутреннее и не должно публиковать обновлений в [selection]
     */
    @AnyThread
    fun updateSelection(selection: SELECTION)
}