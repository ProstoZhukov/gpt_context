package ru.tensor.sbis.pin_code.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import io.reactivex.Observable
import ru.tensor.sbis.common.util.findOrCreateViewModel
import ru.tensor.sbis.common.util.findViewModelHierarchical
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.MenuItemState
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.utils.CheckboxIcon
import ru.tensor.sbis.pin_code.PinCodeViewModel
import ru.tensor.sbis.pin_code.R
import ru.tensor.sbis.pin_code.decl.PinCodePeriod
import java.util.concurrent.TimeUnit

/**
 * Создание посекундного таймера обратного отсчёта.
 * @param defaultStartTimeSec количество секунд с которых должен начаться обратный отсчёт.
 * @param savedStartTimerTimeMs сохраненное время в миллисекундах начала работы таймера.
 * @return Observable испускающий секунды обратного отсчёта.
 *
 * @author mb.kruglova
 */
internal fun createCountDownTimer(
    savedStartTimerTimeMs: Long,
    defaultStartTimeSec: Long,
    currentTimeMs: Long = System.currentTimeMillis()
): Observable<Long> {
    val seconds = when (savedStartTimerTimeMs) {
        0L -> defaultStartTimeSec
        else -> {
            val timePassed = TimeUnit.MILLISECONDS.toSeconds(
                // пользователь может сменить время на телефоне, защищаемся от вероятных отрицательных значений
                (currentTimeMs - savedStartTimerTimeMs).coerceAtLeast(0)
            )
            (defaultStartTimeSec - timePassed).coerceAtLeast(0)
        }
    }

    return Observable.intervalRange(
        PERIOD_SEC,
        seconds,
        0,
        PERIOD_SEC,
        TimeUnit.SECONDS
    ).scan(seconds) { sum, _ -> (sum - PERIOD_SEC) }
}

private const val PERIOD_SEC = 1L

/**
 * Запустить вибрацию девайса.
 */
internal fun Activity.runVibration() {
    when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M ->
            (this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(VIBRATION_DURATION)

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
            (this.getSystemService(Vibrator::class.java) as Vibrator)
                .vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))

        else -> (this.getSystemService(Vibrator::class.java) as Vibrator).vibrate(VIBRATION_DURATION)
    }
}

private const val VIBRATION_DURATION = 100L

/**
 * Создать меню для времени сохранения подтверждения подписи в пин-коде.
 */
internal fun createPeriodPickerMenu(
    vm: PinCodeViewModel<*>,
    resources: Resources,
    currentPeriod: PinCodePeriod
) = SbisMenu(
    children = listOf(
        MenuItem(
            resources.getString(R.string.pin_code_period_picker_session_option),
            state = if (currentPeriod is PinCodePeriod.Session) MenuItemState.ON else MenuItemState.MIXED,
            handler = { vm.periodPickerAction(PinCodePeriod.Session()) }
        ),
        MenuItem(
            resources.getString(R.string.pin_code_period_picker_hour_option),
            state = if (currentPeriod is PinCodePeriod.Hour) MenuItemState.ON else MenuItemState.MIXED,
            handler = { vm.periodPickerAction(PinCodePeriod.Hour()) }
        ),
        MenuItem(
            resources.getString(R.string.pin_code_period_picker_half_hour_option),
            state = if (currentPeriod is PinCodePeriod.HalfHour) MenuItemState.ON else MenuItemState.MIXED,
            handler = { vm.periodPickerAction(PinCodePeriod.HalfHour()) }
        ),
        MenuItem(
            resources.getString(R.string.pin_code_period_picker_quarter_hour_option),
            state = if (currentPeriod is PinCodePeriod.QuarterHour) MenuItemState.ON else MenuItemState.MIXED,
            handler = { vm.periodPickerAction(PinCodePeriod.QuarterHour()) }
        )
    ),
    stateOnIcon = CheckboxIcon.MARKER
)

/** Найти ViewModel или, в противном случае, создать новую. */
internal inline fun <reified T : ViewModel> findOrCreateViewModelHierarchical(
    fragment: Fragment,
    storeOwner: ViewModelStoreOwner,
    crossinline create: () -> T
): T {
    return fragment.findViewModelHierarchical()
        ?: findOrCreateViewModel(
            storeOwner = storeOwner,
            create = create,
            key = null
        )
}