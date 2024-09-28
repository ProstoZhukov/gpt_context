package ru.tensor.sbis.appdesign.folderview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.DemoFolderItemBinding
import java.util.*

/**
 * @author us.bessonov
 */
internal class DemoFolderListAdapter : RecyclerView.Adapter<DemoFolderListAdapter.ViewHolder>() {

    private val items: MutableList<Folder> = ArrayList()
    private var onClickListener: ((Folder) -> Unit)? = null

    fun setItems(items: List<Folder>?) {
        this.items.clear()
        this.items.addAll(items!!)
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: ((Folder) -> Unit)?) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.demo_folder_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewBinding = DemoFolderItemBinding.bind(holder.itemView)
        val item = items[position]

        viewBinding.departmentTitle.text = item.title
        viewBinding.moreDepartmentsArrow.isVisible = true

        holder.itemView.setOnClickListener {
            onClickListener?.invoke(item)
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}