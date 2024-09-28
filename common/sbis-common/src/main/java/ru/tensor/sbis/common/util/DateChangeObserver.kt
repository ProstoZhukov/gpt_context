package ru.tensor.sbis.common.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver
import java.util.*
import javax.inject.Inject

/**
 * Класс, предназначенный для отслеживания изменения текущей даты устройства при непосредственной её смене в настройках,
 * и вследствие изменения часового пояса или времени
 */
class DateChangeObserver @Inject constructor(context: Context) {

    private val applicationContext = context.applicationContext

    private val intentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_TIME_TICK)
        addAction(Intent.ACTION_TIMEZONE_CHANGED)
        addAction(Intent.ACTION_TIME_CHANGED)
    }

    private val timeChangeReceiver = object : EntryPointBroadcastReceiver() {
        override fun onReady(context: Context, intent: Intent) {
            dateChangeSubject.onNext(Calendar.getInstance())
        }
    }

    private val dateChangeSubject = BehaviorSubject.create<Calendar>()

    private val dateChangeObservable = dateChangeSubject
        .distinctUntilChanged { date1, date2 -> date1.isTheSameDay(date2) }
        .doOnSubscribe {
            ContextCompat.registerReceiver(
                applicationContext,
                timeChangeReceiver,
                intentFilter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
        .doOnDispose { applicationContext.unregisterReceiver(timeChangeReceiver) }
        //TODO: имеются проблемы при использовании replay(1).refCount(); исправить реализацию multicast по результатам выполнения https://online.sbis.ru/doc/57ef3ae3-7596-44aa-adc2-f207bd3182f4
        .share()
        .doOnSubscribe { dateChangeSubject.onNext(Calendar.getInstance()) }

    /**
     * Возвращает [Observable] с текущей датой. Вызов onNext происходит при изменении даты, а также при подписке
     *
     * @return [Observable] со значением [Calendar], соответствующим текущей дате
     */
    fun observeDateChanges(): Observable<Calendar> = dateChangeObservable

    /**
     * Возвращает последнее опубликованное значение, либо текущую дату, в случае его отсутствия
     *
     * @return актуальное значение [Calendar]
     */
    fun getLatestValue(): Calendar = dateChangeSubject.value ?: Calendar.getInstance()

    private fun Calendar.isTheSameDay(other: Calendar): Boolean {
        return get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
    }
}