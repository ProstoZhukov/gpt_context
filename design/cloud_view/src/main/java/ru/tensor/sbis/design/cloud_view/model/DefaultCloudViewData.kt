package ru.tensor.sbis.design.cloud_view.model

import android.text.Spannable
import android.text.SpannableString
import java.util.UUID

/**
 * Реализация по умолчанию для [CloudViewData]
 *
 * @author ma.kolpakov
 */
data class DefaultCloudViewData @JvmOverloads constructor(
    override val text: Spannable? = null,
    override val content: List<CloudContent> = emptyList(),
    override val rootElements: List<Int> = content.indices.toList(),
    override val isDisabledStyle: Boolean = false,
    override val isAuthorBlocked: Boolean = false,
    override val messageUuid: UUID? = null
) : CloudViewData {

    @JvmOverloads
    constructor(
        text: String,
        content: List<CloudContent> = emptyList(),
        rootElements: List<Int> = content.indices.toList(),
        isDisabledStyle: Boolean = false
    ) : this(SpannableString(text), content, rootElements, isDisabledStyle)
}