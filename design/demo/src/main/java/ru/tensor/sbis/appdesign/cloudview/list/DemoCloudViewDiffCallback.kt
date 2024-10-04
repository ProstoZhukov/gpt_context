package ru.tensor.sbis.appdesign.cloudview.list

import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.appdesign.cloudview.data.DemoCloudViewUserData

/**
 * @author ma.kolpakov
 */
internal object DemoCloudViewDiffCallback : DiffUtil.ItemCallback<DemoCloudViewUserData>() {

    override fun areItemsTheSame(oldItem: DemoCloudViewUserData, newItem: DemoCloudViewUserData): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: DemoCloudViewUserData, newItem: DemoCloudViewUserData): Boolean =
        oldItem == newItem
}