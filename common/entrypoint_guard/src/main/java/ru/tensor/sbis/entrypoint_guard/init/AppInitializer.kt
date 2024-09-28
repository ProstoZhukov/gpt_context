package ru.tensor.sbis.entrypoint_guard.init

import android.app.Application
import android.widget.FrameLayout
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard

/**
 * Инициализатор приложения.
 *
 * @author kv.martyshenko
 */
interface AppInitializer<T> : AppInitStateHolder<T> {

    /**
     * Запустить инициализацию приложения.
     */
    @MainThread
    fun initialize(application: Application, scope: CoroutineScope)

    /**
     * Обработчик промежуточных статусов прогресса.
     */
    val progressHandler: ProgressHandler<out T>

    /**
     * Обработчик промежуточных статусов прогресса.
     */
    interface ProgressHandler<T> {
        /**
         * Обработать состояние (показать экран или проигнорировать).
         *
         * @param activity
         * @param container контейнер, в котором можно разместить контент.
         * @param state состояние прогресса.
         */
        @MainThread
        fun <A> handle(activity: A, container: FrameLayout, state: T)
                where A: AppCompatActivity, A: EntryPointGuard.EntryPoint

    }
}