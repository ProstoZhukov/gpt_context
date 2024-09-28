package ru.tensor.sbis.common.testing

import io.reactivex.schedulers.TestScheduler

/**
 * Тестовое правило, которое использует [TestScheduler] планировщик. Применяется, если необходимо тестирование временных
 * задержек
 *
 * @see TrampolineSchedulerRule
 *
 * @author ma.kolpakov
 * @since 7/25/2019
 */
class TestSchedulerRule : SchedulerRule<TestScheduler>() {

    public override val scheduler = TestScheduler()
}