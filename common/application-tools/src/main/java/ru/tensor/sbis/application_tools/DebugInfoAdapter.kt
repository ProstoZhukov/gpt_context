package ru.tensor.sbis.application_tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.application_tools.DebugInfoAdapter.DebugInfoViewHolder
import ru.tensor.sbis.application_tools.debuginfo.DebugClickListener
import ru.tensor.sbis.application_tools.debuginfo.model.BaseDebugInfo
import ru.tensor.sbis.application_tools.debuginfo.model.DebugInfo
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * @author du.bykov
 *
 * @SelfDocumented
 */
class DebugInfoAdapter : RecyclerView.Adapter<DebugInfoViewHolder>() {
    private var mDataList: ArrayList<DebugInfo>? = null
    private var mDebugClickListener: DebugClickListener? = null
    fun setData(dataList: ArrayList<DebugInfo>) {
        mDataList = dataList
        notifyDataSetChanged()
    }

    fun setDebugClickListener(debugClickListener: DebugClickListener) {
        mDebugClickListener = debugClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DebugInfoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.application_tools_debug_info_list_item, parent, false)
        return DebugInfoViewHolder(view, mDebugClickListener)
    }

    override fun onBindViewHolder(
        holder: DebugInfoViewHolder,
        position: Int
    ) {
        holder.bind(mDataList!![position])
    }

    override fun getItemCount(): Int {
        return if (mDataList == null) 0 else mDataList!!.size
    }

    class DebugInfoViewHolder(
        itemView: View,
        debugClickListener: DebugClickListener?
    ) : RecyclerView.ViewHolder(itemView) {
        private val mDebugClickListener: DebugClickListener?
        private val mTitle: SbisTextView
        private val mData: SbisTextView
        fun bind(info: DebugInfo) {
            val type = info.type
            if (type === BaseDebugInfo.Type.CRASH) {
                mTitle.text = info.crashTitle
                mData.text = info.crashMessage
            } else {
                mTitle.setText(info.title)
                mData.text = info.description
            }
            if (type == BaseDebugInfo.Type.CRASH || type == BaseDebugInfo.Type.LOG) itemView.setOnClickListener { v: View? ->
                mDebugClickListener?.onDebugInfoClick(
                    info.type,
                    bindingAdapterPosition
                )
            }
            else itemView.setOnClickListener(null)
        }

        init {
            mTitle = itemView.findViewById(R.id.debug_info_title)
            mData = itemView.findViewById(R.id.debug_info_data)
            mDebugClickListener = debugClickListener
        }
    }
}