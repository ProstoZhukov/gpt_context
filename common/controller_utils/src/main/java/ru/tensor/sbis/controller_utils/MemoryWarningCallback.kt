package ru.tensor.sbis.controller_utils

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
import android.content.res.Configuration
import ru.tensor.sbis.platform.generated.MemoryWarningProcessor

/**
 * Коллбэк на нехватку памяти.
 *
 * Вызывается, когда в устройстве очень мало памяти. Приложение еще не считается уничтожаемым процессом, но система
 * начинает уничтожать фоновые процессы, если приложение не освобождает ресурсы.
 *
 * Подключать в [Application] через [Application.registerComponentCallbacks]
 *
 * @author du.bykov
 */
class MemoryWarningCallback : ComponentCallbacks2 {

    override fun onLowMemory() = Unit

    override fun onConfigurationChanged(newConfig: Configuration) = Unit

    override fun onTrimMemory(level: Int) {
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) MemoryWarningProcessor.invoke()
    }
}