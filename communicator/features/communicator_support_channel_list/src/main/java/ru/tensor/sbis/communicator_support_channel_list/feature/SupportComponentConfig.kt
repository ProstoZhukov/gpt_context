package ru.tensor.sbis.communicator_support_channel_list.feature

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Конфиг файл для communicator_support_channel_list
 */
@Parcelize
internal sealed class SupportComponentConfig : Parcelable {

    /**
     * Надо ли показывать кнопку "назад" на экране списка источников консультаций
     */
    abstract val showLeftPanelOnToolbar: Boolean

    /**
     * Конфиг для отображения поддержки СБИС
     */
    object SabySupport : SupportComponentConfig() {
        @IgnoredOnParcel
        override val showLeftPanelOnToolbar = false
    }

    /**
     * Конфиг для отображения поддержки клиентов
     */
    object ClientSupport : SupportComponentConfig() {
        @IgnoredOnParcel
        override val showLeftPanelOnToolbar = true
    }

    /**
     * Конфиг для отображения в SabyGet
     */
    class SabyGet(
        override val showLeftPanelOnToolbar: Boolean = true,
        val isBrand: Boolean = false,
        val salePoint: UUID? = null,
        val hasAccordion: Boolean = false
    ) : SupportComponentConfig()
}
