package ru.tensor.sbis.tasks.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Тип реестра "на мне" или "от меня", обычно соответствует вкладке.
 *
 * @author aa.sviridov
 */
@Parcelize
enum class RegistryType : Parcelable {
    /**
     * На мне.
     */
    ON_ME,

    /**
     * От меня.
     */
    FROM_ME
}