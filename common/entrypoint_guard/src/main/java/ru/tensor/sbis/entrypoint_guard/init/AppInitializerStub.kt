package ru.tensor.sbis.entrypoint_guard.init

import android.app.Application
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard

/**
 * Реализация [AppInitializer] без логики.
 * Выставляется по умолчанию [EntryPointGuard] для обеспечения плавной миграции по приложениям.
 *
 * @author kv.martyshenko
 */
internal object AppInitializerStub : AppInitializer<Any?> {

    override val initStatus: StateFlow<AppInitStateHolder.InitStatus> =
        MutableStateFlow(AppInitStateHolder.InitStatus.InitCompleted)

    override val progressStatus: StateFlow<Nothing?> = MutableStateFlow(null)
    override fun isProgressStatusRequireUserInteraction(state: Any?): Boolean {
        return false
    }

    override fun initialize(application: Application, scope: CoroutineScope) = Unit

    override val progressHandler: AppInitializer.ProgressHandler<Any?> = object : AppInitializer.ProgressHandler<Any?> {

        override fun <A : AppCompatActivity> handle(
            activity: A,
            container: FrameLayout,
            state: Any?
        ) where A : EntryPointGuard.EntryPoint = Unit

    }

}