package ru.tensor.sbis.application_tools.leak

import android.app.Application
import leakcanary.AppWatcher
import leakcanary.LeakCanary
import shark.AndroidReferenceMatchers

private const val RETAINED_INSTANCES_THRESHOLD_UNTIL_DUMP_HEAP = 50

/**
 * При изменении методов в этом файле, стоит не забывать, что это реализация только для debug и есть еще release,
 * методы и сигнатуры методов должны совпадать в обеих папках
 */

/**
 * Устанавливает наблюдателя ссылок из LeakCanary для классов-компонентов Android для отладки утечек памяти
 * @param application Application
 */
fun deployLeakCanary(application: Application) {
    AppWatcher.manualInstall(application)
    SemManagersLeakingActivity.applyFix(application)
    LeakCanary.config = LeakCanary.config.copy(
        retainedVisibleThreshold = RETAINED_INSTANCES_THRESHOLD_UNTIL_DUMP_HEAP,
        referenceMatchers = AndroidReferenceMatchers.appDefaults +
                AndroidReferenceMatchers.staticFieldLeak(
                    "com.samsing.SomeSingleton",
                    "sContext"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    "$CLIPBOARD_MANAGER_CLASS_NAME${'$'}1",
                    "this${'$'}0"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    "$CLIPBOARD_MANAGER_CLASS_NAME${'$'}3",
                    "this${'$'}0"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    CLIPBOARD_MANAGER_CLASS_NAME,
                    "mPersonaManager"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    PERSONA_MANAGER_CLASS_NAME,
                    "mContext"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    "android.app.ContextImpl",
                    "mOuterContext"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    "com.android.internal.policy.PhoneLayoutInflater",
                    "mContext"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    "android.view.inputmethod.InputMethodManager",
                    "sInstance"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    "android.view.inputmethod.InputMethodManager\$ControlledInputConnectionWrapper",
                    "mParentInputMethodManager"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    "android.view.accessibility.AccessibilityManager",
                    "sInstance"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    "android.view.accessibility.AccessibilityManager",
                    "mAccessibilityStateChangeListeners"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    "android.app.HwChangeButtonWindowCtrl",
                    "mActivity"
                )
                +
                AndroidReferenceMatchers.staticFieldLeak(
                    "android.app.HwChangeButtonWindowCtrl",
                    "mInstanceMap"
                )
                +
                // Со стороны Android Data Binding на небольшое время придерживаются слушатели обновления значений.
                //   В sReferenceQueue находятся объекты, на которые уже нет других сильных ссылок.
                //   ViewDataBinding.processReferenceQueue() освобождает их, но с задержкой (подстраивается под анимацию).
                // https://stackoverflow.com/a/64670147
                AndroidReferenceMatchers.staticFieldLeak(
                    "androidx.databinding.ViewDataBinding",
                    "sReferenceQueue"
                )
                +
                AndroidReferenceMatchers.instanceFieldLeak(
                    "androidx.databinding.ViewDataBinding\$WeakListener",
                    "queue"
                )
                +
                AndroidReferenceMatchers.instanceFieldLeak(
                    "androidx.databinding.ViewDataBinding\$WeakListener",
                    "mTarget"
                )
                +
                // Иногда нативный код операционной системы ненадолго придерживает
                // `com.google.android.datatransport.runtime.scheduling.jobscheduling.JobInfoSchedulerService`.
                // На более новых версях ОС освобождается сразу после onDestroy.
                // https://github.com/square/leakcanary/issues/2175
                // https://github.com/googlecodelabs/android-workmanager/issues/232
                AndroidReferenceMatchers.instanceFieldLeak(
                    "android.app.job.JobService\$1",
                    "this\$0",
                    description = """
                        Временное удержание службы отправки логов Firebase нативным кодом со стороны операционной системы.
                        Наблюдается на отдельных устройствах с Android 5 и 6. Не актуально для последующих.
                    """.trimIndent()
                )
                +
                // Тосты при всплывании ликают вызвавшею вью пока не спрячутся, для правильного перехода по next
                AndroidReferenceMatchers.staticFieldLeak(
                    "android.widget.Toast\$TN",
                    "mNextView"
                )
                +
                // Утекают EditView с фокусом после уничтожения фрагмента / активити на устройствах самсунг
                //  https://issuetracker.google.com/issues/156230908
                //  https://github.com/square/leakcanary/issues/1359
                AndroidReferenceMatchers.instanceFieldLeak(
                    "android.view.inputmethod.InputMethodManager",
                    "mCurrentInputConnection"
                ) {
                    manufacturer == AndroidReferenceMatchers.SAMSUNG && sdkInt in 28..29
                }
                +
                // утекает MediaProjectionCallback, который тянет за собой MediaProjection, который тянет context
                // см. https://issuetracker.google.com/issues/294242497
                //
                AndroidReferenceMatchers.instanceFieldLeak(
                    "android.media.projection.MediaProjection",
                    "mContext"
                )
                +
                // Утечка памяти. UiModeManager android 14
                // см. https://issuetracker.google.com/issues/294776102
                // см. https://github.com/square/leakcanary/issues/2559
                AndroidReferenceMatchers.instanceFieldLeak(
                    "android.app.UiModeManager",
                    "mContext"
                )
    )
}

fun disableLeakCanary() {
    LeakCanary.config = LeakCanary.config.copy(dumpHeap = false)
}