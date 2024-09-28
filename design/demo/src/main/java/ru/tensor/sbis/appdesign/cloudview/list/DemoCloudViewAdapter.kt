package ru.tensor.sbis.appdesign.cloudview.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ListAdapter
import ru.tensor.sbis.appdesign.cloudview.data.DemoCloudViewUserData
import ru.tensor.sbis.appdesign.cloudview.data.DemoIncomeCloudViewUserData
import ru.tensor.sbis.appdesign.cloudview.data.DemoOutcomeCloudViewUserData
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.content.utils.MessagesViewPool

/**
 * @author ma.kolpakov
 */
internal class DemoCloudViewAdapter(
    private val viewPool: MessagesViewPool
) : ListAdapter<DemoCloudViewUserData, DemoCloudViewHolder>(DemoCloudViewDiffCallback) {

    var itemLongClickListener: ((view: View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, @LayoutRes viewType: Int): DemoCloudViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false) as CloudView
        view.setViewPool(viewPool)
        return DemoCloudViewHolder(view)
    }

    override fun onBindViewHolder(holder: DemoCloudViewHolder, position: Int) =
        holder.bind(getItem(position), itemLongClickListener)

    @LayoutRes
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DemoIncomeCloudViewUserData  -> INCOME_CLOUD_VIEW_TYPE
            is DemoOutcomeCloudViewUserData -> OUTCOME_CLOUD_VIEW_TYPE
        }
    }
}