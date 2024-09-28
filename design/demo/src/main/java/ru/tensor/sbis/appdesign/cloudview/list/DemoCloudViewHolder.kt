package ru.tensor.sbis.appdesign.cloudview.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.cloudview.data.DemoCloudViewUserData
import ru.tensor.sbis.appdesign.cloudview.data.DemoIncomeCloudViewUserData
import ru.tensor.sbis.appdesign.cloudview.data.DemoOutcomeCloudViewUserData
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.container.locator.watcher.ItemIdProvider

internal const val INCOME_CLOUD_VIEW_TYPE = R.layout.cloud_view_income_item
internal const val OUTCOME_CLOUD_VIEW_TYPE = R.layout.cloud_view_outcome_item

/**
 * @author ma.kolpakov
 */
internal class DemoCloudViewHolder(
    private val view: CloudView
) : RecyclerView.ViewHolder(view), ItemIdProvider {
    lateinit var data: DemoCloudViewUserData
    fun bind(data: DemoCloudViewUserData, itemClickListener: ((view: View) -> Unit)?) {
        this.data = data
        view.apply {
            date = data.date
            time = data.time
            author = data.author
            receiverInfo = data.receiverInfo
            edited = data.edited
            this.data = data.data
            this.setOnLongClickListener {
                itemClickListener?.invoke(it)
                true
            }
        }

        when (data) {
            is DemoIncomeCloudViewUserData  -> bindIncomeData(data)
            is DemoOutcomeCloudViewUserData -> bindOutcomeData(data)
        }
    }

    private fun bindIncomeData(data: DemoIncomeCloudViewUserData) {
        view.apply {
            isPersonal = data.isPersonal
        }
    }

    private fun bindOutcomeData(data: DemoOutcomeCloudViewUserData) {
        view.apply {
            edited = data.edited
            sendingState = data.sendingState
        }
    }

    override fun getId() = data.id.toString()
}