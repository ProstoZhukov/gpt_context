package ru.tensor.sbis.communicator.design

import com.mikepenz.iconics.typeface.IIcon
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.SbisMobileIcon
import timber.log.Timber


/**
 * Позволяет получить иконку [SbisMobileIcon.Icon] из строк названия или ее полного значения "smi_IconName"
 */
val String.icon: IIcon? get() = getSbisMobileIconSafely(this)

private fun getSbisMobileIconSafely(iconName: String): IIcon? =
    SbisMobileIcon().run {
        val iconPrefix =
            if (iconName.contains(mappingPrefix)) StringUtils.EMPTY else mappingPrefix
        try {
            // С онлайна некоторые иконки статусов приходят с другим именем - здесь меняем на нужную
            val actualIconName = when (iconName) {
                "statusMeditation" -> "StatusMeditation"
                "CallGroup" -> "Headphone"
                "Lunch" -> "SwipeRestget"
                "Implementation" -> "implementation"
                else -> iconName
            }
            getIcon("${iconPrefix}_$actualIconName")
        } catch (e: IllegalArgumentException) {
            Timber.d(e)
            null
        }
    }
