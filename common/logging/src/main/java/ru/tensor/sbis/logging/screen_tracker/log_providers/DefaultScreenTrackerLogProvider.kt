package ru.tensor.sbis.logging.screen_tracker.log_providers

import android.app.Activity
import androidx.fragment.app.Fragment
import ru.tensor.sbis.toolbox_decl.logging.screen_tracker.ScreenTrackerMarker

/**
 * Реализация [ScreenTrackerLogProvider] по умолчанию.
 * Если активити или фрагмент (экран) имплементирует интерфейс [ScreenTrackerMarker], из него будет взят тег и параметры,
 * иначе открытие будет залогировано с именем класса в качестве тега
 *
 * @author av.krymov
 */
internal object DefaultScreenTrackerLogProvider : ScreenTrackerLogProvider {

    override fun getLogMessage(screen: Any, action: String): String {
        val type = screen.screenType
        return if (screen is ScreenTrackerMarker) {
            "$SCREEN_TRACKER $type ${screen.screenTag()} $action ${screen.screenParameters()}"
        } else {
            "$SCREEN_TRACKER $type ${screen::class.java.name} $action"
        }
    }

    private val Any.screenType: String
        get() = when (this) {
            is Activity -> ACTIVITY
            is Fragment -> FRAGMENT
            else -> SCREEN
        }

    private const val SCREEN_TRACKER = "SCREEN_TRACKER"
    private const val ACTIVITY = "ACTIVITY"
    private const val FRAGMENT = "FRAGMENT"
    private const val SCREEN = "SCREEN"
}