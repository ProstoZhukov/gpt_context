package ru.tensor.sbis.appdesign.cloudview.data

import ru.tensor.sbis.design.cloud_view.model.CloudViewData
import ru.tensor.sbis.design.cloud_view.model.PersonModel
import ru.tensor.sbis.design.cloud_view.model.ReceiverInfo
import ru.tensor.sbis.design.cloud_view.model.SendingState
import java.util.*

/**
 * @author ma.kolpakov
 */
internal sealed class DemoCloudViewUserData {
    abstract val id: Int
    abstract val date: Date?
    abstract val time: Date
    abstract val author: PersonModel?
    abstract val receiverInfo: ReceiverInfo?
    abstract val data: CloudViewData
    abstract val edited: Boolean
}

internal data class DemoIncomeCloudViewUserData(
    override val id: Int,
    override val date: Date?,
    override val time: Date,
    override val author: PersonModel?,
    override val receiverInfo: ReceiverInfo?,
    override val data: CloudViewData,
    override val edited: Boolean,
    val isPersonal: Boolean
) : DemoCloudViewUserData()

internal data class DemoOutcomeCloudViewUserData(
    override val id: Int,
    override val date: Date?,
    override val time: Date,
    override val author: PersonModel?,
    override val receiverInfo: ReceiverInfo?,
    override val data: CloudViewData,
    override val edited: Boolean,
    val sendingState: SendingState
) : DemoCloudViewUserData()