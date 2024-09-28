package ru.tensor.sbis.folderspanel

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter

/**
 * View для свайп-меню папки
 */
class SwipeMenuRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {

    init {
        layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            true
        )
        val dividerItemDecoration = object : androidx.recyclerview.widget.DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation) {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                val position = parent.getChildAdapterPosition(view)
                // первый элемент не декорируется
                if (position == 0) {
                    outRect.setEmpty()
                } else {
                    super.getItemOffsets(outRect, view, parent, state)
                }
            }
        }
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.folderspanel_swipe_menu_separator)!!)
        addItemDecoration(dividerItemDecoration)
    }

    fun addMenuItems(menuItems: List<Any>) {
        adapter = object : ViewModelAdapter() {
            init {
                cell<SwipeMenuItem>(R.layout.folderspanel_item_swipe_menu)
                reload(menuItems)
            }
        }
    }
}