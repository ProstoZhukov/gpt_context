package ru.tensor.sbis.order_message.adapter.holders

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.ViewGroup
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.custom_view_tools.utils.dpF
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.GroupData
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.utils.extentions.getColorFrom
import ru.tensor.sbis.design.R as RDesign

/**@SelfDocumented*/
@SuppressLint("ViewConstructor")
internal class HeaderItemView(
    context: Context, attrs: AttributeSet? = null, private val withImage: Boolean = false
) : ViewGroup(context, attrs) {

    companion object {
        private const val IMAGE_SIZE = 20
    }

    private val theme: OrderCompanyTheme = OrderCompanyTheme.getInstance(context)

    private val margin4 = resources.dp(4)
    private val margin6 = resources.dp(6)
    private val margin12 = resources.dp(12)

    private val imageSize: Int by lazy { resources.dp(IMAGE_SIZE) }

    private val titleLayout: TextLayout = TextLayout {
        paint.set(theme.titleTextPaint)
        val startMargin = if (withImage) margin4 else margin12
        padding = TextLayout.TextLayoutPadding(startMargin, margin6, margin12, margin6)
        isVisibleWhenBlank = false
    }

    private val imageView: PersonView? =
        if (withImage) PersonView(context).apply {
            setShape(Shape.CIRCLE)
            layoutParams = LayoutParams(imageSize, imageSize)
        } else null

    init {
        setWillNotDraw(false)
        imageView?.let { addView(it) }
    }

    /**@SelfDocumented*/
    fun setData(title: String, imageUrl: String = "", imageWithStub: Boolean = false) {
        val requestNeeded = titleLayout.configure { text = title }
        setImage(title, imageUrl, imageWithStub)
        if (requestNeeded) safeRequestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)

        imageView?.let {
            val imageExactly = makeExactlySpec(imageSize)
            it.measure(imageExactly, imageExactly)
        }

        val imageWidth = if (withImage) margin12 + imageSize else 0
        val titleMaxWidth = width - imageWidth
        titleLayout.buildLayout { layoutWidth = titleMaxWidth }

        val height = if (withImage) maxOf(imageSize + margin6 * 2, titleLayout.height) else titleLayout.height
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val imageEnd = margin12 + imageSize
        imageView?.let {
            val imageTop = (measuredHeight - imageSize) / 2
            it.layout(margin12, imageTop, imageEnd, imageTop + it.measuredHeight)
        }
        val titleLeft = if (withImage) imageEnd else 0
        val titleTop = if (withImage) (measuredHeight - titleLayout.height) / 2 else 0
        titleLayout.layout(titleLeft, titleTop)
    }

    override fun onDraw(canvas: Canvas) {
        titleLayout.draw(canvas)
    }

    private fun setImage(fullName: String, imageUrl: String, imageWithStub: Boolean = false) {
        if (imageWithStub) imageView?.setData(GroupData(null))
        else imageView?.setData(
            PersonData(photoUrl = imageUrl, initialsStubData = InitialsStubData.createByFullName(fullName))
        )
    }

    class OrderCompanyTheme private constructor(context: Context) {

        val titleTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).also {
            it.typeface = TypefaceManager.getRobotoRegularFont(context)
            it.color = context.getColorFrom(RDesign.color.palette_color_blue6)
            it.textSize = context.resources.dpF(14)
        }

        companion object {
            @Volatile
            private var INSTANCE: OrderCompanyTheme? = null

            fun getInstance(context: Context): OrderCompanyTheme =
                INSTANCE ?: synchronized(this) { INSTANCE ?: OrderCompanyTheme(context).also { INSTANCE = it } }
        }
    }
}