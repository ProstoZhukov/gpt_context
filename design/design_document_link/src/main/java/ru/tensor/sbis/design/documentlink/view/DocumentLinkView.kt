package ru.tensor.sbis.design.documentlink.view

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toRect
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import ru.tensor.sbis.design.documentlink.R
import ru.tensor.sbis.design.documentlink.models.DocumentLinkModel
import ru.tensor.sbis.design.documentlink.utils.DocumentLinkStyle

/**
 * Виджет документа-основания.
 * При построении "плоской" view нужно использовать [DocumentLinkDrawable]
 *
 * @author da.zolotarev
 */
class DocumentLinkView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val footingDocDrawable = DocumentLinkDrawable(context, attrs)

    private val accessHelper = AccessHelper()

    /** [DocumentLinkDrawable.documentLinkModel] */
    var documentLinkModel: DocumentLinkModel
        get() = footingDocDrawable.documentLinkModel
        set(value) {
            footingDocDrawable.documentLinkModel = value
            requestLayout()
        }

    /** [DocumentLinkDrawable.style] */
    var style: DocumentLinkStyle
        get() = footingDocDrawable.style
        set(value) {
            footingDocDrawable.style = value
        }

    init {
        background = footingDocDrawable
        ViewCompat.setAccessibilityDelegate(this, accessHelper)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        footingDocDrawable.withArrowIcon = l != null
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(background.minimumWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(background.minimumHeight, MeasureSpec.EXACTLY)
        )
    }

    private inner class AccessHelper : ExploreByTouchHelper(this) {
        private val HEADER_ID = 0
        private val COMMENT_ID = 1
        private val VIEW_ID = 2

        override fun getVirtualViewAt(x: Float, y: Float) = when {
            footingDocDrawable.headerBounds.contains(x, y) -> HEADER_ID
            footingDocDrawable.commentBounds.contains(x, y) -> COMMENT_ID
            Rect(0, 0, width, height).contains(x.toInt(), y.toInt()) -> COMMENT_ID
            else -> HOST_ID
        }

        override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
            virtualViewIds.apply {
                add(HEADER_ID)
                add(COMMENT_ID)
                add(VIEW_ID)
            }
        }

        override fun onPopulateNodeForVirtualView(virtualViewId: Int, node: AccessibilityNodeInfoCompat) {
            when (virtualViewId) {
                HEADER_ID -> {
                    node.className = DocumentLinkView::class.simpleName
                    node.contentDescription = this@DocumentLinkView.resources.getString(
                        R.string.design_document_link_header_description,
                        footingDocDrawable.documentLinkModel.title
                    )
                    node.setBoundsInParent(footingDocDrawable.headerBounds.toRect())
                }
                COMMENT_ID -> {
                    node.className = DocumentLinkView::class.simpleName
                    node.contentDescription = this@DocumentLinkView.resources.getString(
                        R.string.design_document_link_comment_description,
                        footingDocDrawable.documentLinkModel.comment
                    )
                    node.setBoundsInParent(footingDocDrawable.commentBounds.toRect())
                }
                VIEW_ID -> {
                    node.className = DocumentLinkView::class.simpleName
                    node.contentDescription =
                        this@DocumentLinkView.resources.getString(R.string.design_document_link_container_description)
                    node.setBoundsInParent(Rect(0, 0, width, height))
                    node.isClickable = footingDocDrawable.withArrowIcon
                }
            }
        }

        override fun onPerformActionForVirtualView(virtualViewId: Int, action: Int, arguments: Bundle?) = false
    }

    override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        return if (accessHelper.dispatchHoverEvent(event)) {
            true
        } else {
            super.dispatchHoverEvent(event)
        }
    }
}