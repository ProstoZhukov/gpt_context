package ru.tensor.sbis.design.selection.bl.vm.selection.multi.command

import io.reactivex.*
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModelImpl

/**
 * Обработчик команд по модификации пользовательского выбора в [MultiSelectionViewModelImpl]. Начинает работу после вызова
 * [startWith]
 *
 * @author ma.kolpakov
 */
internal class SelectionCommandHandler<DATA : SelectorItem>(
    private val scheduler: Scheduler
) {

    private var initialSelectionCompletable: Completable? = null

    private val commandReducerSubject = PublishSubject.create<SelectionCommand<DATA>>()

    private val commandSubject = PublishSubject.create<SelectionCommand<DATA>>().apply {
        observeOn(scheduler).subscribe(commandReducerSubject)
    }

    val command: Observable<SelectionCommand<DATA>> = commandSubject

    /**
     * Загружает пользовательские данные из потока [initialSelection] и запускает обработку команд. Команды применяются
     * последовательно к результату [initialSelection].
     *
     * @see postCommand
     */
    fun startWith(initialSelection: Single<List<DATA>>): Observable<List<DATA>> {
        check(initialSelectionCompletable == null) { "Command handler already started" }
        initialSelectionCompletable = initialSelection.ignoreElement()

        return initialSelection
            .flatMapObservable { selection ->
                commandReducerSubject
                    .observeOn(scheduler)
                    .scan(selection, SelectionCommandAccumulator<DATA>())
            }
            // фильтрация для команд, которые не меняли значения (см. SelectionCommand)
            .distinctUntilChanged { old, new -> old === new }
    }

    /**
     * Метод гарантирует публикацию команд только после загрузки пользовательского выбора
     *
     * @see startWith
     */
    fun postCommand(command: SelectionCommand<DATA>) {
        checkNotNull(initialSelectionCompletable) { "Command handler is not started yet" }
            .subscribeOn(scheduler)
            .andThen { observer: Observer<in SelectionCommand<DATA>> -> observer.onNext(command) }
            .subscribe(commandSubject)
    }
}