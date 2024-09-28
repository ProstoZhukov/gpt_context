package ru.tensor.sbis.application_tools.logcrashesinfo.appinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.logcrashesinfo.appinfo.models.AppInfoRowViewModel
import ru.tensor.sbis.application_tools.logcrashesinfo.appinfo.models.AppInfoViewModel
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * @author du.bykov
 *
 * Адаптер списка крана информации о краше.
 */
class AppInfoAdapter(appInfoViewModel: AppInfoViewModel) : RecyclerView.Adapter<AppInfoAdapter.AppInfoViewHolder>() {

    private val appInfoViewModels: List<AppInfoRowViewModel> = appInfoViewModel.appInfoRowViewModels

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppInfoViewHolder {
        return AppInfoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.application_tools_app_info_row,
                    parent,
                    false
                ) as LinearLayout
        )
    }

    override fun onBindViewHolder(
        holder: AppInfoViewHolder,
        position: Int
    ) {
        holder.render(appInfoViewModels[position])
    }

    override fun getItemCount(): Int {
        return appInfoViewModels.size
    }

    inner class AppInfoViewHolder(private val mRootView: LinearLayout) : RecyclerView.ViewHolder(mRootView) {

        fun render(appInfoViewModel: AppInfoRowViewModel) {
            val appInfoAttr = mRootView.findViewById<SbisTextView>(R.id.app_info_attr)
            val appInfoVal = mRootView.findViewById<SbisTextView>(R.id.app_info_val)

            appInfoAttr.text = appInfoViewModel.attr
            appInfoVal.text = appInfoViewModel.value
        }
    }
}