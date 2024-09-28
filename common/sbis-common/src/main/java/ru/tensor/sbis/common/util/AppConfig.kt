package ru.tensor.sbis.common.util

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import ru.tensor.sbis.common.BuildConfig
import timber.log.Timber

object AppConfig {
    private lateinit var sPreferences: SharedPreferences
    private var applicationCurrentVersion = ""
    private var applicationCurrentVersionCode = ""

    @JvmStatic
    var landscapeKeyboardHeight: Int
        get() = if (sPreferences.contains(Keys.LANDSCAPE_KEYBOARD_HEIGHT)) {
            sPreferences.getInt(Keys.LANDSCAPE_KEYBOARD_HEIGHT, 0)
        } else {
            0
        }
        set(height) {
            sPreferences.edit().putInt(Keys.LANDSCAPE_KEYBOARD_HEIGHT, height).apply()
        }

    var portraitConversationKeyboardHeight: Int
        get() = sPreferences.getInt(Keys.PORTRAIT_CONVERSATION_KEYBOARD_HEIGHT, 0)
        set(height) {
            sPreferences.edit().putInt(Keys.PORTRAIT_CONVERSATION_KEYBOARD_HEIGHT, height).apply()
        }
    var landscapeConversationKeyboardHeight: Int
        get() = sPreferences.getInt(Keys.LANDSCAPE_CONVERSATION_KEYBOARD_HEIGHT, 0)
        set(height) {
            sPreferences.edit().putInt(Keys.LANDSCAPE_CONVERSATION_KEYBOARD_HEIGHT, height).apply()
        }

    @JvmStatic
    var portraitKeyboardHeight: Int
        get() = sPreferences.getInt(Keys.PORTRAIT_KEYBOARD_HEIGHT, 0)
        set(height) {
            sPreferences.edit().putInt(Keys.PORTRAIT_KEYBOARD_HEIGHT, height).apply()
        }

    fun init(context: Context) {
        sPreferences = context.getSharedPreferences("SbisMobile", Context.MODE_PRIVATE)
        try {
            val pInfo1 = context.packageManager.getPackageInfo(context.packageName, 0)
            applicationCurrentVersion = pInfo1.versionName
        } catch (e1: PackageManager.NameNotFoundException) {
            Timber.e(e1)
        }
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            applicationCurrentVersionCode = pInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
        }
    }

    fun getApplicationCurrentVersion() = applicationCurrentVersion

    fun getApplicationCurrentVersionCode() = applicationCurrentVersionCode

    fun enableDebugMode(enable: Boolean) {
        sPreferences.edit().putBoolean(Keys.DEBUG_MODE_ON, enable).apply()
        Timber.d("Debug mode %s", if (enable) "enabled." else "disabled.")
    }

    @JvmStatic
    fun isDebug(): Boolean = sPreferences.getBoolean(Keys.DEBUG_MODE_ON, BuildConfig.DEBUG)

    private object Keys {
        const val PORTRAIT_KEYBOARD_HEIGHT = "portraitKeyboardHeight"
        const val LANDSCAPE_KEYBOARD_HEIGHT = "landscapeKeyboardHeight"
        const val PORTRAIT_CONVERSATION_KEYBOARD_HEIGHT = "portraitConversationKeyboardHeight"
        const val LANDSCAPE_CONVERSATION_KEYBOARD_HEIGHT = "landscapeConversationKeyboardHeight"
        const val DEBUG_MODE_ON = "debugModeOn"
    }
}