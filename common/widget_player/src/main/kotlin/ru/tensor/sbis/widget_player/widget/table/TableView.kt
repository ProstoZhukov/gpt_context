package ru.tensor.sbis.widget_player.widget.table

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Trace
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.collection_view.CollectionView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.view_ext.SimplifiedTextView
import ru.tensor.sbis.jsonconverter.generated.CellContentMeasurer
import ru.tensor.sbis.jsonconverter.generated.Margin
import ru.tensor.sbis.jsonconverter.generated.Size
import ru.tensor.sbis.jsonconverter.generated.TableGeometry
import ru.tensor.sbis.jsonconverter.generated.TableGeometrySettings
import ru.tensor.sbis.jsonconverter.generated.TablesController
import ru.tensor.sbis.richtext.R
import ru.tensor.sbis.richtext.span.view.table.RichTableFullDialog
import ru.tensor.sbis.widget_player.WidgetPlayer
import ru.tensor.sbis.widget_player.api.WidgetSource
import ru.tensor.sbis.widget_player.converter.ElementTree
import ru.tensor.sbis.widget_player.converter.WidgetBody
import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.converter.findWidgetStoreOwner
import ru.tensor.sbis.widget_player.layout.HorizontalScrollLayout
import ru.tensor.sbis.widget_player.layout.VerticalBlockLayout
import ru.tensor.sbis.widget_player.widget.table.model.TableCellElement
import ru.tensor.sbis.widget_player.widget.table.model.TableViewData
import java.lang.ref.WeakReference

/**
 * @author am.boldinov
 */
@SuppressLint("ViewConstructor")
internal class TableView(context: Context, options: TableOptions) : VerticalBlockLayout(context) {

    private val scrollLayout = HorizontalScrollLayout(context).apply {
        setFadingEdgeLength(options.fadingEdgeLength.getValuePx(context))
        isHorizontalFadingEdgeEnabled = true
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    private val tableLayout = TableLayout(context, options).apply {
        layoutParams =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
    }

    private var expandButton: View? = null
    private var fullDialog: WeakReference<RichTableFullDialog>? = null

    private val expandButtonRef
        get() = run {
            expandButton ?: createExpandButton().also {
                expandButton = it
            }
        }

    init {
        id = R.id.richtext_table_view
        fixedParentWidth = true
        scrollLayout.addView(tableLayout)
        addView(scrollLayout)
    }

    fun setViewData(data: TableViewData) {
        tableLayout.setViewData(data)
        if (data.isShrink()) {
            expandButtonRef.let {
                it.setOnClickListener {
                    showFullTableDialog(data)
                }
                if (it.parent == null) {
                    addView(it)
                }
            }
            if (data.isFullDataShowing) {
                showFullTableDialog(data)
            }
        } else {
            expandButton?.takeIf { it.parent != null }?.let {
                removeView(it)
            }
        }
    }

    fun findCellContainer(id: WidgetID): ViewGroup? {
        return tableLayout.children.find {
            it.tag == id
        } as? ViewGroup
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        hideFullTableDialog()
    }

    private fun showFullTableDialog(data: TableViewData) {
        val current = fullDialog?.get()
        if (current == null || !current.isShowing) {
            val view = createFullTableView(data)
            if (view != null) {
                val dialog = RichTableFullDialog(context, view)
                dialog.setOnCancelListener {  // manual cancel
                    data.isFullDataShowing = false
                    hideFullTableDialog()
                }
                fullDialog = WeakReference(dialog)
                dialog.show()
                data.isFullDataShowing = true
            }
        }
    }

    private fun hideFullTableDialog() {
        fullDialog?.get()?.dismiss()
        fullDialog?.clear()
        fullDialog = null
    }

    private fun createExpandButton(): View {
        return SimplifiedTextView(context).apply {
            id = R.id.richtext_table_expand_button
            layoutParams =
                LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    val margin = Offset.X2S.getDimenPx(context)
                    topMargin = margin
                    bottomMargin = margin
                }
            val padding = Offset.X3S.getDimenPx(context)
            setPadding(0, padding, 0, padding)
            text = resources.getString(R.string.richtext_table_open_fully)
            paint.typeface = TypefaceManager.getRobotoRegularFont(context)
            setTextColor(TextColor.LINK.getValue(context))
            setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSize.XS.getScaleOffDimen(context))
        }
    }

    private fun createFullTableView(data: TableViewData): View? {
        return findWidgetStoreOwner()?.let { storeOwner ->
            return WidgetPlayer(context).apply {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val padding = resources.getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.default_content_padding)
                setPadding(padding, padding, padding, padding)
                clipToPadding = false
                setWidgetSource(
                    source = WidgetSource.Body(
                        WidgetBody(
                            ElementTree(root = data.root.toFullTableElement()),
                            storeOwner.widgetStore
                        )
                    )
                )
            }
        }
    }
}

@SuppressLint("ViewConstructor")
private class TableLayout(context: Context, options: TableOptions) : ViewGroup(context), CollectionView {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = options.borderColor.getValue(context)
        style = Paint.Style.STROKE
        strokeWidth = options.borderThickness.getValue(context)
    }
    private val tableSettings = TableGeometrySettings(
        tableMargin = Margin(),
        cellContentMargin = with(options.cellContentMargin.getValuePx(context)) {
            Margin(this, this, this, this)
        },
        borderWidth = borderPaint.strokeWidth.toInt(),
        minimalColumnWidth = options.minimalColumnWidth.getValuePx(context)
    )

    private val adapter = Adapter().also {
        it.attachView(this)
    }

    private val cellMeasurer = object : CellContentMeasurer() {
        override fun getWidth(width: Int, cellOrder: Short): Int {
            val viewHolder = adapter.getOrderedViewHolder(cellOrder.toInt())
            return viewHolder?.getCellSize(width)?.width ?: 0
        }

        override fun getHeight(width: Int, cellOrder: Short): Int {
            val viewHolder = adapter.getOrderedViewHolder(cellOrder.toInt())
            return viewHolder?.getCellSize(width)?.height ?: 0
        }
    }

    private var geometry: TableGeometry? = null

    init {
        setWillNotDraw(false)
    }

    fun setViewData(data: TableViewData) {
        adapter.setData(data)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Trace.beginSection("TableLayout onMeasure")
        if (adapter.itemCount > 0 && adapter.getData() != null) {
            invalidateRequestedMeasureCache()
            geometry = TablesController.getGeometryPrecomputed(
                table = adapter.getData()!!.table,
                settings = tableSettings,
                availableWidth = MeasureSpec.getSize(widthMeasureSpec),
                measurer = cellMeasurer
            ).apply {
                with(tableSize) {
                    setMeasuredDimension(width, height)
                }
            }
        } else {
            geometry = null
            setMeasuredDimension(0, 0)
        }
        Trace.endSection()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Trace.beginSection("TableLayout onLayout")
        geometry?.let {
            val left = paddingStart
            val top = paddingTop
            it.frames.forEachIndexed { index, frame ->
                getChildAt(index)?.let { child ->
                    val childLeft = left + frame.startPointX
                    val childTop = top + frame.startPointY
                    val childRight = childLeft + frame.width
                    val childBottom = childTop + frame.height
                    child.layout(childLeft, childTop, childRight, childBottom)
                }
            }
        }
        Trace.endSection()
    }

    override fun addChildInLayout(view: View, position: Int) {
        addViewInLayout(view, position, view.layoutParams, true)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBorders(canvas)
    }

    private fun drawBorders(canvas: Canvas) {
        geometry?.borders?.forEach {
            val startX = it.startPointX
            val stopX = it.endPointX
            val startY = it.startPointY
            val stopY = it.endPointY
            canvas.drawLine(startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), borderPaint)
        }
    }

    private fun invalidateRequestedMeasureCache() {
        repeat(adapter.itemCount) {
            val viewHolder = adapter.getViewHolder(it)
            if (viewHolder.view.isLayoutRequested) { // инвалидируем кеш только у тех, кто запросил новый layout
                viewHolder.invalidateMeasureCache()
            }
        }
    }

    private class Adapter : CollectionView.Adapter<CellViewHolder>() {

        private var data: TableViewData? = null

        override fun getItemCount(): Int {
            return data?.cellCount ?: 0
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        override fun onCreateViewHolder(
            parent: CollectionView,
            viewType: Int
        ): CellViewHolder {
            Trace.beginSection("RichTable onCreateViewHolder")
            val viewHolder = CellViewHolder(VerticalBlockLayout((parent as View).context).apply {
                layoutParams =
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            })
            Trace.endSection()
            return viewHolder
        }

        override fun onBindViewHolder(
            holder: CellViewHolder,
            position: Int
        ) {
            Trace.beginSection("RichTable onBindViewHolder")
            data?.let {
                holder.bind(it.getCell(position))
            }
            Trace.endSection()
        }

        override fun getStashSize(): Int {
            return 0
        }

        fun getOrderedViewHolder(cellOrder: Int): CellViewHolder? {
            val position = data?.getCellPosition(cellOrder) ?: cellOrder
            return getViewHolder(position)
        }

        fun clear() {
            if (data != null) {
                data = null
                notifyDataChanged()
            }
        }

        fun getData(): TableViewData? {
            return data
        }

        fun setData(data: TableViewData) {
            this.data = data
            notifyDataChanged()
        }
    }

    private class CellViewHolder(view: View) : CollectionView.ViewHolder(view) {

        private val measuredSize = Size()

        private var lastAvailableWidth = 0

        fun bind(cell: TableCellElement) {
            invalidateMeasureCache()
            view.tag = cell.id
        }

        fun getCellSize(availableWidth: Int): Size {
            if (lastAvailableWidth != availableWidth && !isSmallMeasuredWidth(availableWidth)) {
                Trace.beginSection("RichTable measureCell")
                measureView(view, availableWidth)
                measuredSize.width = view.measuredWidth
                measuredSize.height = view.measuredHeight
                Trace.endSection()
            }
            lastAvailableWidth = availableWidth
            return measuredSize
        }

        fun invalidateMeasureCache() {
            lastAvailableWidth = 0
            measuredSize.width = 0
            measuredSize.height = 0
        }

        private fun isSmallMeasuredWidth(availableWidth: Int): Boolean {
            // если на следующей итерации происходит измерение меньшего значения, а вьюха уже в него вписывается
            // то повторное измерение не производим
            return lastAvailableWidth > availableWidth && measuredSize.width <= availableWidth
        }

        private fun measureView(view: View, availableWidth: Int) {
            val widthSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST)
            view.measure(widthSpec, MeasureSpec.UNSPECIFIED)
        }

    }
}