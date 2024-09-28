package ru.tensor.sbis.pushnotification_utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Класс представляет набор утилит для поддержки корректной настройки
 * PendingIntent для различных версий Android.
 * */
object PendingIntentSupportUtils {

    /**
     * Метод для получения PendingIntent.getActivity(...), в котором к переданному
     * значению флага будет дополнительно добавлено значение 'FLAG_IMMUTABLE'.
     * (Флаг 'FLAG_IMMUTABLE' будет добавлен для всех версий Android с API >= 23)
     *
     * @param context [Context]
     * @param requestCode код запроса.
     * @param intent объект [Intent] для отложенного действия.
     * @param intentFlags значения флага для обработки действия системой.
     *
     * @return [PendingIntent]
     */
    @JvmStatic
    @JvmOverloads
    fun getUpdateActivityImmutable(
        context: Context,
        requestCode: Int,
        intent: Intent,
        intentFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT
    ): PendingIntent = PendingIntent.getActivity(
        context, requestCode, intent, mutateToImmutableFlagsIfNeeded(intentFlags)
    )

    /**
     * Метод для получения PendingIntent.getActivity(...), в котором к переданному
     * значению флага будет дополнительно добавлено значение 'FLAG_MUTABLE'.
     * (Флаг 'FLAG_MUTABLE' будет добавлен для всех версий Android с API >= 31)
     *
     * @param context [Context]
     * @param requestCode код запроса.
     * @param intent объект [Intent] для отложенного действия.
     * @param intentFlags значения флага для обработки действия системой.
     *
     * @return [PendingIntent]
     */
    @JvmStatic
    @JvmOverloads
    fun getUpdateActivityMutable(
        context: Context,
        requestCode: Int,
        intent: Intent,
        intentFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT
    ): PendingIntent = PendingIntent.getActivity(
        context, requestCode, intent, mutateToMutableFlagsIfNeeded(intentFlags)
    )

    /**
     * Метод для получения PendingIntent.getBroadcast(...), в котором к переданному
     * значению флага будет дополнительно добавлено значение 'FLAG_IMMUTABLE'.
     * (Флаг 'FLAG_IMMUTABLE' будет добавлен для всех версий Android с API >= 23)
     *
     * @param context [Context]
     * @param requestCode код запроса.
     * @param intent объект [Intent] для отложенного действия.
     * @param intentFlags значения флага для обработки действия системой.
     *
     * @return [PendingIntent]
     */
    @JvmStatic
    @JvmOverloads
    fun getUpdateBroadcastImmutable(
        context: Context,
        requestCode: Int,
        intent: Intent,
        intentFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT
    ): PendingIntent = PendingIntent.getBroadcast(
        context, requestCode, intent, mutateToImmutableFlagsIfNeeded(intentFlags)
    )

    /**
     * Метод для получения PendingIntent.getBroadcast(...), в котором к переданному
     * значению флага будет дополнительно добавлено значение 'FLAG_MUTABLE'.
     * (Флаг 'FLAG_MUTABLE' будет добавлен для всех версий Android с API >= 31)
     *
     * @param context [Context]
     * @param requestCode код запроса.
     * @param intent объект [Intent] для отложенного действия.
     * @param intentFlags значения флага для обработки действия системой.
     *
     * @return [PendingIntent]
     */
    @JvmStatic
    @JvmOverloads
    fun getUpdateBroadcastMutable(
        context: Context,
        requestCode: Int,
        intent: Intent,
        intentFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT
    ): PendingIntent = PendingIntent.getBroadcast(
        context, requestCode, intent, mutateToMutableFlagsIfNeeded(intentFlags)
    )

    /**
     * Метод для получения PendingIntent.getService(...) API < 26 / PendingIntent.getForegroundService(...) API >= 26,
     * в котором к переданному значению значению флага будет дополнительно добавлено значение 'FLAG_IMMUTABLE'.
     * (Флаг 'FLAG_IMMUTABLE' будет добавлен для всех версий Android с API >= 23)
     *
     * @param context [Context]
     * @param requestCode код запроса.
     * @param intent объект [Intent] для отложенного действия.
     * @param intentFlags значения флага для обработки действия системой.
     *
     * @return [PendingIntent]
     */
    @JvmStatic
    @JvmOverloads
    fun getUpdateServiceImmutable(
        context: Context,
        requestCode: Int,
        intent: Intent,
        intentFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT
    ): PendingIntent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(
                context, requestCode, intent, mutateToImmutableFlagsIfNeeded(intentFlags)
            )
        } else {
            PendingIntent.getService(
                context, requestCode, intent, mutateToImmutableFlagsIfNeeded(intentFlags)
            )
        }

    /**
     * Метод для получения PendingIntent.getService(...) API < 26 / PendingIntent.getForegroundService(...) API >= 26,
     * в котором к переданному значению флага будет дополнительно добавлено значение 'FLAG_MUTABLE'.
     * (Флаг 'FLAG_MUTABLE' будет добавлен для всех версий Android с API >= 31)
     *
     * @param context [Context]
     * @param requestCode код запроса.
     * @param intent объект [Intent] для отложенного действия.
     * @param intentFlags значения флага для обработки действия системой.
     *
     * @return [PendingIntent]
     */
    @JvmStatic
    @JvmOverloads
    fun getUpdateServiceMutable(
        context: Context,
        requestCode: Int,
        intent: Intent,
        intentFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT
    ): PendingIntent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(
                context, requestCode, intent, mutateToMutableFlagsIfNeeded(intentFlags)
            )
        } else {
            PendingIntent.getService(
                context, requestCode, intent, mutateToMutableFlagsIfNeeded(intentFlags)
            )
        }

    /**
     * Метод выставляет флаг 'FLAG_IMMUTABLE' для переданного значения PendingIntent flags.
     * Выставление флага происходит для всех Android >= 23 API.
     *
     * @param originalFlags исходное значение PendingIntent flags.
     *
     * @return промодифицированное значение PendingIntent flags.
     */
    @JvmStatic
    fun mutateToImmutableFlagsIfNeeded(originalFlags: Int): Int {
        /*
            Android 12 требует обязательного указания флага FLAG_IMMUTABLE/FLAG_MUTABLE.
            Флаг 'FLAG_IMMUTABLE' доступен начиная с 23 API.
        */
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) originalFlags
        else originalFlags.mutateWithFlags(PendingIntent.FLAG_IMMUTABLE)
    }

    /**
     * Метод выставляет флаг 'FLAG_MUTABLE' для переданного значения PendingIntent flags.
     * Выставление флага происходит для всех Android >= 31 API.
     *
     * @param originalFlags исходное значение PendingIntent flags.
     *
     * @return промодифицированное значение PendingIntent flags.
     */
    @JvmStatic
    fun mutateToMutableFlagsIfNeeded(originalFlags: Int): Int {
        /*
            Android 12 требует обязательного указания флага FLAG_IMMUTABLE/FLAG_MUTABLE.
            Флаг 'FLAG_MUTABLE' доступен начиная с 31 API.
        */
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) originalFlags
        else originalFlags.mutateWithFlags(PendingIntent.FLAG_MUTABLE)
    }

    private fun Int.mutateWithFlags(mutateFlags: Int): Int = this or mutateFlags
}