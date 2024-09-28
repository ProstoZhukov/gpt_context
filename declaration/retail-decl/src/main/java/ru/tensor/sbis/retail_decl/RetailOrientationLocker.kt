package ru.tensor.sbis.retail_decl

import android.app.Activity
import ru.tensor.sbis.plugin_struct.feature.Feature

/** Блокировка ориентации экрана. */
interface RetailOrientationLocker : Feature {

    /**
     * Отключение ландшафтного режима для телефонов и устройств с экраном меньше SIZE_XLARGE.
     * Для устройств(планшетов) с включенной опцией автоповорота ограничения не применяется.
     * Работает не для всех Activity, в ряде случаев система бросает IllegalStateException:
     * https://stackoverflow.com/questions/48072438/java-lang-illegalstateexception-only-fullscreen-opaque-activities-can-request-o
     */
    fun lockInPortraitOrientation(activity: Activity)

    /** Снять блокировку на изменение ориентации экрана. */
    fun unlockOrientation(activity: Activity)

    /** Проверить установлена ли блокировка ориентации экрана в портретный режим. */
    fun isLockInPortraitOrientation(activity: Activity): Boolean
}