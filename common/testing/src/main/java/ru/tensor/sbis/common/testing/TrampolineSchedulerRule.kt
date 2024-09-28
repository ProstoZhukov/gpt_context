package ru.tensor.sbis.common.testing

import io.reactivex.internal.schedulers.TrampolineScheduler
import io.reactivex.schedulers.Schedulers

/**
 * Тестовое правило, которое использует [Schedulers.trampoline] планировщик. Правило пригодно для большинства тестов
 *
 * @see TestSchedulerRule
 *
 * @author ma.kolpakov
 * @since 7/25/2019
 */
class TrampolineSchedulerRule : SchedulerRule<TrampolineScheduler>() {

    override val scheduler = Schedulers.trampoline() as TrampolineScheduler
}