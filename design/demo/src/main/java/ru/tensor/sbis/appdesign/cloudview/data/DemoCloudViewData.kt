package ru.tensor.sbis.appdesign.cloudview.data

import android.text.Spannable
import android.text.SpannableString
import ru.tensor.sbis.design.cloud_view.model.CloudContent
import ru.tensor.sbis.design.cloud_view.model.CloudViewData

/**
 * @author ma.kolpakov
 */
data class DemoCloudViewData(
    override val text: Spannable?,
    override val content: List<CloudContent> = emptyList(),
    override val rootElements: List<Int> = emptyList(),
    override val isDisabledStyle: Boolean = false
) : CloudViewData {

    constructor(
        messageText: String?,
        vararg content: CloudContent
    ) : this(
        messageText?.run(::SpannableString),
        content.asList(),
        content.indices.toList(),
        false
    )
}