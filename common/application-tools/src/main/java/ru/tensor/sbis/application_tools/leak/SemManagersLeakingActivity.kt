package ru.tensor.sbis.application_tools.leak

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build.MANUFACTURER
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.N
import android.os.Bundle
import java.lang.reflect.Field

/**
 * @author du.bykov
 *
 * Правка утечки памяти на samsung устройствах, связанных с контекстом в менеджерах
 */
class SemManagersLeakingActivity private constructor(private val application: Application) :
    Application.ActivityLifecycleCallbacks {

    override fun onActivityDestroyed(activity: Activity) {
        try {
            swapActivityToAppContextEmergencyMng()
            swapActivityToAppContextClipboardManager(activity)
        } catch (ignored: Exception) {
            // the same result is expected on subsequent tries.
        }
    }

    @Throws(Exception::class)
    private fun swapActivityToAppContextClipboardManager(activity: Activity) {
        if (SDK_INT >= M && MANUFACTURER == SEM_MANUFACTURER) {
            val instance: Any? = activity.getSystemService(Class.forName(CLIPBOARD_MANAGER_CLASS_NAME))
            if (instance != null) {
                val context: Field = instance.javaClass.getDeclaredField(LEAK_FIELD_NAME)
                context.isAccessible = true
                context.set(instance, activity.applicationContext)
            }
        }
    }

    @Throws(Exception::class)
    private fun swapActivityToAppContextEmergencyMng() {
        val semEmergencyManagerClass = Class.forName(EMERGENCY_MANAGER_CLASS_NAME)
        val instanceField = semEmergencyManagerClass.getDeclaredField("sInstance")
        instanceField.isAccessible = true
        val instance = instanceField.get(null)
        val contextField = semEmergencyManagerClass.getDeclaredField(LEAK_FIELD_NAME)
        contextField.isAccessible = true
        contextField.set(instance, application)
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    companion object {
        @SuppressLint("ObsoleteSdkInt")
        fun applyFix(application: Application) {
            if (MANUFACTURER == SEM_MANUFACTURER && SDK_INT <= N) {
                application.registerActivityLifecycleCallbacks(SemManagersLeakingActivity(application))
            }
        }
    }
}

private const val SEM_MANUFACTURER = "samsung"
private const val EMERGENCY_MANAGER_CLASS_NAME = "com.samsung.android.emergencymode.SemEmergencyManager"
private const val LEAK_FIELD_NAME = "mContext"
const val CLIPBOARD_MANAGER_CLASS_NAME = "com.samsung.android.content.clipboard.SemClipboardManager"
const val PERSONA_MANAGER_CLASS_NAME = "com.samsung.android.knox.SemPersonaManager"
