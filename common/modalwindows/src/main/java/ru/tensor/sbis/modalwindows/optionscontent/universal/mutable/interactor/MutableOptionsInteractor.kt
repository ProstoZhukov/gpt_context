package ru.tensor.sbis.modalwindows.optionscontent.universal.mutable.interactor

import io.reactivex.Completable
import io.reactivex.Observable
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption

/**
 * Интерактор, ответственный за загрузку опций в диалоговом окне
 *
 * @author sr.golovkin
 */
interface MutableOptionsInteractor<OPTION: BottomSheetOption> {

    /**
     * Загрузить опции
     */
    fun loadOptions(): Observable<List<OPTION>>

    /**
     * Обработать нажатие на опцию
     */
    fun performActionOnOptionClick(option: OPTION): Completable
}