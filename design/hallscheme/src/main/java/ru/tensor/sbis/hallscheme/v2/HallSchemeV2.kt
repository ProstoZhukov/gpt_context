package ru.tensor.sbis.hallscheme.v2

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Shader
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.drawable.DrawableFactory
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.image.CloseableStaticBitmap
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.otaliastudios.zoom.ZoomApi.Companion.TRANSFORMATION_CENTER_INSIDE
import com.otaliastudios.zoom.ZoomApi.Companion.TYPE_REAL_ZOOM
import com.otaliastudios.zoom.ZoomApi.Companion.TYPE_ZOOM
import com.otaliastudios.zoom.ZoomLayout
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.HallSchemeModel
import ru.tensor.sbis.hallscheme.v2.business.model.TableStatus
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.data.HallSchemeItemDto
import ru.tensor.sbis.hallscheme.v2.data.HallSchemeModelDto
import ru.tensor.sbis.hallscheme.v2.presentation.factory.HallSchemeFactory
import ru.tensor.sbis.hallscheme.v2.presentation.factory.creator.ItemCreator
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi
import ru.tensor.sbis.hallscheme.v2.util.DpToPxConverter
import ru.tensor.sbis.hallscheme.v2.widget.AbstractItemView
import java.util.*
import kotlin.math.min

/**
 * View для отображения элементов схемы зала с возможностью зума и скроллинга.
 * @author aa.gulevskiy
 */
class HallSchemeV2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.hallSchemeTheme,
    @StyleRes defStyleRes: Int = R.style.HallSchemeDarkTheme
) : FrameLayout(
    ThemeContextBuilder(context, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    private val zoomLayout = ZoomLayout(getContext()).apply {
        setTransformation(TRANSFORMATION_CENTER_INSIDE)
        setOverScrollHorizontal(false)
        setOverScrollVertical(false)
        setOverPinchable(false)
        setHasClickableChildren(true)
    }

    private val tablesLayout = RelativeLayout(getContext()).apply { clipChildren = false }

    private lateinit var draweeHierarchy: GenericDraweeHierarchy
    private val schemeBackgroundView = SimpleDraweeView(getContext())
    private var backgroundViewLp = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    private val presenter: HallSchemeV2Contract.Presenter

    private val aligningRules = arrayOf(
        RelativeLayout.CENTER_HORIZONTAL,
        RelativeLayout.ALIGN_PARENT_RIGHT,
        RelativeLayout.ALIGN_PARENT_LEFT
    )

    /**
     * Хелпер для хранения спецификаций и др.
     */
    private val hallSchemeSpecHolder: HallSchemeSpecHolder

    /**
     * Хранит цвета для различных статусов столов/баров
     * (в данный момент используется для окраски стульев).
     */
    private val specificColorsMap: Map<TableStatus, Int>

    private lateinit var hallSchemeFactory: HallSchemeFactory
    private lateinit var itemCreator: ItemCreator

    private var maxZoom: Float = MAX_ZOOM
    private var onHallSchemeItemClickListener: OnHallSchemeItemClickListener? = null

    private var leftPadding: Int = 0
    private var topPadding: Int = 0

    private var selectedViewId: Int? = null
    private val multiSelectedTables = mutableSetOf<UUID>()

    private companion object {
        // default maximum zoom factor
        private const val MAX_ZOOM = 2.0f
        private const val DEFAULT_SCALE = 1.0f
    }

    private val viewImpl = object : HallSchemeV2Contract.View {
        override fun drawItems(items: List<HallSchemeItemUi>) {
            hallSchemeFactory.getHallSchemeDrawer().drawItems(
                items,
                tablesLayout,
                onHallSchemeItemClickListener
            )

            selectedViewId?.let { selectTable(it) }
            multiSelectedTables.forEach { selectTableForMultiSelect(it) }
        }

        override fun clearScheme() {
            tablesLayout.removeAllViews()
        }

        // --- Раздел работы с фонами (начало)
        override fun showEmptyBackground() {
            draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(resources).build()
            prepareDraweeForRequest(true, "", null)
        }

        override fun showBackgroundIfTablesPinned(
            isRemoteUrl: Boolean,
            url: String,
            left: Int,
            top: Int,
            translate: Int,
            backgroundZoom: Float
        ) {
            val converter = DpToPxConverter(context)
            draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(resources)
                .setActualImageScaleType { outTransform, _, _, _, _, _ ->
                    // Преобразуем единицы измерения
                    outTransform.setScale(converter.factor * backgroundZoom, converter.factor * backgroundZoom)
                    // Выполняем сдвиг изображения
                    outTransform.postTranslate(
                        -converter.fromDpToPixels(left.toFloat()) * translate.toFloat(),
                        -converter.fromDpToPixels(top.toFloat()) * translate.toFloat()
                    )
                    outTransform
                }
                .build()
            prepareDraweeForRequest(isRemoteUrl, url, null)
            alignViewByRule(RelativeLayout.ALIGN_PARENT_LEFT)
            schemeBackgroundView.adjustViewBounds = true
        }

        override fun showRepeatedBackground(isRemoteUrl: Boolean, url: String) {
            draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(resources)
                .setActualImageScaleType(null)
                .build()
            prepareDraweeForRequest(isRemoteUrl, url, RepeatedDrawableFactory())
        }

        override fun showStretchedBackground(isRemoteUrl: Boolean, url: String) {
            draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(resources)
                .setActualImageFocusPoint(PointF(0F, 0F))
                .setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP)
                .build()
            prepareDraweeForRequest(isRemoteUrl, url, null)
        }

        override fun showLeftTopBackground(isRemoteUrl: Boolean, url: String) {
            draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(resources).build()
            prepareDraweeForRequest(isRemoteUrl, url, null)
            alignViewByRule(RelativeLayout.ALIGN_PARENT_LEFT)
        }

        override fun showCenterTopBackground(isRemoteUrl: Boolean, url: String) {
            draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(resources).build()
            prepareDraweeForRequest(isRemoteUrl, url, null)
            alignViewByRule(RelativeLayout.CENTER_HORIZONTAL)
        }

        override fun showRightTopBackground(isRemoteUrl: Boolean, url: String) {
            draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(resources).build()
            prepareDraweeForRequest(isRemoteUrl, url, null)
            alignViewByRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        }
        // --- Раздел работы с фонами (конец)

        override fun initHallSchemeView() {
            resetScale()
            drawRootSimpleDraweeView()
        }

        override fun setItemsLayoutSize(top: Int, left: Int, bottom: Int, right: Int) {
            val converter = DpToPxConverter(context)

            val minX = converter.fromDpToPixels(left.toFloat())
            val minY = converter.fromDpToPixels(top.toFloat())
            val maxX = converter.fromDpToPixels(right.toFloat())
            val maxY = converter.fromDpToPixels(bottom.toFloat())

            val layoutWidth = maxX - minX
            val layoutHeight = maxY - minY

            leftPadding = -minX
            topPadding = -minY

            setTableLayoutSize(layoutWidth, layoutHeight, leftPadding, topPadding)
            setScale(layoutWidth, layoutHeight)

            val lp = schemeBackgroundView.layoutParams as RelativeLayout.LayoutParams
            lp.setMargins(-leftPadding, -topPadding, 0, 0)
        }

        override fun setBackgroundViewSize(imageWidth: Int, imageHeight: Int) {
            val converter = DpToPxConverter(context)
            backgroundViewLp.width = converter.fromDpToPixels(imageWidth.toFloat())
            backgroundViewLp.height = converter.fromDpToPixels(imageHeight.toFloat())
            schemeBackgroundView.layoutParams = backgroundViewLp
        }

        override fun setBackgroundViewSizeMatchParent() {
            backgroundViewLp.width = MATCH_PARENT
            backgroundViewLp.height = MATCH_PARENT
            schemeBackgroundView.layoutParams = backgroundViewLp
        }

        override fun applyScale(scale: Float) {
            val minZoom = if (scale == 0f) DEFAULT_SCALE else scale
            val zoomType = if (minZoom >= DEFAULT_SCALE) TYPE_ZOOM else TYPE_REAL_ZOOM
            zoomLayout.setMinZoom(minZoom, zoomType)
        }
    }

    // initialization region start
    init {
        val ctx = getContext()

        initMaxZoom(ctx, attrs)
        hallSchemeSpecHolder = initHallSchemeSpecificationsHolder()
        addRootViews()
        presenter = HallSchemeV2Presenter(viewImpl)

        specificColorsMap = mutableMapOf()

        listOf(
            TableStatus.HasReadyDishes,
            TableStatus.Occupied,
            TableStatus.OccupiedForBooking,
            TableStatus.Default,
            TableStatus.Disabled,
            TableStatus.OccupiedForUser
        ).forEach { status ->
            specificColorsMap[status] = ContextCompat.getColor(ctx, ctx.getThemeColor(status.attr))
        }
    }

    private fun initHallSchemeSpecificationsHolder(): HallSchemeSpecHolder {
        val extraWidth = context.resources.getDimensionPixelOffset(R.dimen.hall_scheme_table_extra_width)
        val tableSpec = initTableSpec(extraWidth)
        val chairSpec = initChairSpec()
        val sofaSpec = initSofaSpec()
        val billSpec = initBillSpec()
        val bookingSpec = initBookingSpec()
        val assigneeSpec = initAssigneeSpec()
        return HallSchemeSpecHolder(tableSpec, chairSpec, sofaSpec, billSpec, bookingSpec, assigneeSpec)
    }

    private fun initTableSpec(extraWidth: Int): HallSchemeSpecHolder.TableSpec {
        val roundCornerRadius = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_corner_radius)
        val circleEnhancement = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_circle_enhancement)
        val padding = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_padding)
        return HallSchemeSpecHolder.TableSpec(extraWidth, roundCornerRadius, circleEnhancement, padding)
    }

    private fun initChairSpec(): HallSchemeSpecHolder.ChairSpec {
        val chairHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_chair_height)
        val chairFullHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_chair_full_height)
        val chairWidth = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_chair_width)
        return HallSchemeSpecHolder.ChairSpec(chairHeight, chairFullHeight, chairWidth)
    }

    private fun initSofaSpec(): HallSchemeSpecHolder.SofaSpec {
        val straightHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_sofa_straight_height)
        val straightWidth = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_sofa_straight_width)
        val sectionWidth = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_sofa_section_width)
        val cornerHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_sofa_corner_height)
        val cornerWidth = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_sofa_corner_width)
        return HallSchemeSpecHolder.SofaSpec(straightHeight, straightWidth, sectionWidth, cornerHeight, cornerWidth)
    }

    private fun initBillSpec(): HallSchemeSpecHolder.BillSpec {
        val billHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_bill_height)
        val billWidth = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_bill_width)
        val billOffset = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_bill_offset)
        return HallSchemeSpecHolder.BillSpec(billHeight, billWidth, billOffset)
    }

    private fun initBookingSpec(): HallSchemeSpecHolder.BookingSpec {
        val bookingHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_booking_height)
        val bookingIntersection = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_booking_intersection)
        return HallSchemeSpecHolder.BookingSpec(bookingHeight, bookingIntersection)
    }

    private fun initAssigneeSpec(): HallSchemeSpecHolder.AssigneeSpec {
        val size = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_assignee_size)
        return HallSchemeSpecHolder.AssigneeSpec(size)
    }

    private fun initMaxZoom(context: Context, attrs: AttributeSet?) {
        val attributeArray: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.HallScheme, 0, 0
        )
        maxZoom = attributeArray.getFloat(R.styleable.HallScheme_maxZoom, MAX_ZOOM)
        zoomLayout.setMaxZoom(maxZoom, TYPE_REAL_ZOOM)
        attributeArray.recycle()
    }

    private fun addRootViews() {
        addView(zoomLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        zoomLayout.addView(tablesLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }
    // initialization region end

    /**
     * Метод для получения модели для отображения схемы зала.
     * Вынесен в отдельный метод, чтобы была возможность при необходимости запускать в отдельном потоке.
     *
     * @param hallSchemeModelDto модель данных для схемы зала
     */
    fun getHallSchemeModel(hallSchemeModelDto: HallSchemeModelDto): HallSchemeModel {
        this.hallSchemeFactory = HallSchemeFactory(hallSchemeModelDto.planTheme, hallSchemeModelDto.textureType)

        val defaultItemColor = context.getThemeColor(R.attr.hall_scheme_item_default_color)

        itemCreator = hallSchemeFactory.getHallSchemeItemCreator(
            this.context,
            ColorsHolder(specificColorsMap, defaultItemColor),
            hallSchemeSpecHolder
        )

        return itemCreator.createSchemeModel(hallSchemeModelDto)
    }

    /**
     * Метод для отображения схемы зала.
     * @param hallSchemeModel модель схемы зала.
     * @param onHallSchemeItemClickListener коллбек для клика на элемент схемы.
     * @param selectedViewId идентификатор выбранного столика.
     */
    fun show(
        hallSchemeModel: HallSchemeModel,
        onHallSchemeItemClickListener: OnHallSchemeItemClickListener,
        selectedViewId: Int? = null
    ) {
        this.onHallSchemeItemClickListener = onHallSchemeItemClickListener
        this.selectedViewId = selectedViewId

        multiSelectedTables.clear()
        viewImpl.clearScheme()
        showAfterLayout { presenter.setHallSchemeModel(hallSchemeModel) }
    }

    private fun showAfterLayout(showSchemeCommand: () -> Unit) {
        // Если Схема уже в лэйауте, показываем элементы, иначе - ждём пока она будет добавлена в лэйаут
        if (this.isInLayout) {
            showSchemeCommand.invoke()
        } else {
            val listener = object : OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
                ) {
                    showSchemeCommand.invoke()
                    this@HallSchemeV2.removeOnLayoutChangeListener(this)
                }
            }

            this.addOnLayoutChangeListener(listener)
        }
    }

    /**
     * Полностью перерисовывает схему при условии, что она первоначально была отрисована методом [show].
     * Можно использовать в тех случаях, когда меняется размер основного контейнера, а данные остаются прежними,
     * для корректного размещения элементов в контейнере.
     */
    @Suppress("unused")
    fun relayout() {
        showAfterLayout { presenter.relayout() }
    }

    /**
     * Предназначен для перерисовки столов на схеме при условии, что они присутствуют на схеме
     * и необходимо поменять атрибуты (цвет, фон) и отображаемую на них информацию.
     *
     * Внимание! Предпогалается, что размеры объектов и их координаты остаются прежними.
     * При каких-либо существенных изменениях схемы, влияющих на расположение объектов
     * (изменении координат или размеров объектов, добавлении, удалении объектов,
     * изменении количества стульев у столов, изменении параметров фона и др.),
     * необходимо выполнить полную перерисовку схемы методом [show].
     *
     * @param itemsForUpdate список объектов с данными по столам, которые необходимо перерисовать.
     */
    fun updateItems(itemsForUpdate: List<HallSchemeItemDto>) {
        val mappedItems = itemCreator.mapItems(itemsForUpdate)
        presenter.redrawItems(mappedItems)
    }

    private fun prepareDraweeForRequest(isRemoteUrl: Boolean, url: String, drawableFactory: DrawableFactory?) {
        initDraweeView()

        val imageRequest =
            if (isRemoteUrl) {
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build()
            } else {
                val resId = resources.getIdentifier(url, "drawable", context.packageName)
                if (resId == 0) {
                    ImageRequestBuilder.newBuilderWithSource(Uri.parse("http:/$url")).build()
                } else {
                    ImageRequestBuilder.newBuilderWithResourceId(resId).build()
                }
            }

        val controller = getDraweeControllerBuilder(imageRequest, drawableFactory).build()
        schemeBackgroundView.controller = controller
    }

    private fun getDraweeControllerBuilder(
        imageRequest: ImageRequest,
        drawableFactory: DrawableFactory?
    ): PipelineDraweeControllerBuilder {

        val builder = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest)
            .setControllerListener(CustomBaseControllerListener())

        if (drawableFactory != null) builder.setCustomDrawableFactory(drawableFactory)

        return builder
    }

    private fun initDraweeView() {
        schemeBackgroundView.apply { hierarchy = draweeHierarchy }
    }

    private fun alignViewByRule(rule: Int) {
        aligningRules.forEach { backgroundViewLp.removeRule(it) }
        backgroundViewLp.addRule(rule)
    }

    private inner class RepeatedDrawableFactory : DrawableFactory {
        override fun createDrawable(image: CloseableImage): Drawable? {
            if (image is CloseableStaticBitmap) {
                val converter = DpToPxConverter(context)
                val scale = converter.factor
                val bitmap = image.underlyingBitmap

                return BitmapDrawable(
                    resources,
                    Bitmap.createScaledBitmap(
                        bitmap,
                        (bitmap.width * scale).toInt(),
                        (bitmap.height * scale).toInt(),
                        true
                    )
                ).apply {
                    setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                }
            }
            return null
        }

        override fun supportsImageType(image: CloseableImage): Boolean = image is CloseableStaticBitmap
    }

    private inner class CustomBaseControllerListener : BaseControllerListener<ImageInfo>() {

        override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
            this@HallSchemeV2.presenter.imageLoadingSuccess(imageInfo?.width, imageInfo?.height)
        }

        override fun onFailure(id: String?, throwable: Throwable?) {
            super.onFailure(id, throwable)
            this@HallSchemeV2.presenter.imageLoadingFailure()
        }
    }

    private fun drawRootSimpleDraweeView() {
        schemeBackgroundView.layoutParams = backgroundViewLp
        tablesLayout.removeView(schemeBackgroundView)
        tablesLayout.addView(schemeBackgroundView)
    }

    private fun setTableLayoutSize(layoutWidth: Int, layoutHeight: Int, leftPadding: Int, topPadding: Int) {
        val lp = tablesLayout.layoutParams as LayoutParams
        lp.width = layoutWidth
        lp.height = layoutHeight
        tablesLayout.setPadding(leftPadding, topPadding, 0, 0)
    }

    private fun setScale(layoutWidth: Int, layoutHeight: Int) {
        val scaleX = min(DEFAULT_SCALE, width.toFloat() / layoutWidth)
        val scaleY = min(DEFAULT_SCALE, height.toFloat() / layoutHeight)
        presenter.setScale(min(scaleX, scaleY))
        viewImpl.applyScale(min(scaleX, scaleY))
    }

    private fun resetScale() {
        zoomLayout.setMinZoom(DEFAULT_SCALE, TYPE_ZOOM)
    }

    // Selection region start
    /**
     * Выделить столик.
     * @param cloudId идентификатор столика.
     * @return true - произошёл выбор столика.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun selectTable(cloudId: Int): Boolean {
        clearMultiSelection()

        val tableForSelect = findTable(cloudId)
        if (tableForSelect == null || isTableDisabled(tableForSelect)) {
            return false
        }

        if (selectedViewId != null) {
            unSelectTable(selectedViewId!!)
        }

        selectTableView(tableForSelect)
        return true
    }

    private fun isTableDisabled(viewForSelect: AbstractItemView) = viewForSelect.status == TableStatus.Disabled

    private fun findTable(id: UUID): AbstractItemView? {
        return hallSchemeFactory.getHallSchemeDrawer().findTable(tablesLayout, id, null)
    }

    private fun findTable(cloudId: Int): AbstractItemView? {
        return hallSchemeFactory.getHallSchemeDrawer().findTable(tablesLayout, null, cloudId)
    }

    /**
     * Снятие выделения со столика.
     * @param cloudId идентификатор столика.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun unSelectTable(cloudId: Int) {
        hallSchemeFactory.getHallSchemeDrawer().findViewAndDoAction(tablesLayout, null, cloudId) { view ->
            view.unSelect()
            if (cloudId == selectedViewId) selectedViewId = null
        }
    }

    private fun selectTableView(view: AbstractItemView) {
        view.select()
        selectedViewId = view.cloudId as Int
    }

    /**
     * Выделить столик в режиме multiselect.
     * @param id идентификатор столика.
     * @return true - произошёл выбор столика.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun selectTableForMultiSelect(id: UUID): Boolean {
        selectedViewId?.let { unSelectTable(it) }

        val tableForSelect = findTable(id) ?: return false
        tableForSelect.selectForMultiSelect()
        multiSelectedTables.add(tableForSelect.tableId as UUID)

        return true
    }

    /**
     * Снятие выделения со столика в режиме multiselect.
     * @param id идентификатор столика.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun unSelectTableForMultiSelect(id: UUID) {
        hallSchemeFactory.getHallSchemeDrawer().findViewAndDoAction(tablesLayout, id, null) { view ->
            view.unSelectForMultiSelect()
            multiSelectedTables.remove(id)
        }
    }

    /**
     * Возвращает множество выбранных столов в режиме muliselect.
     * @return Set<UUID> - множество уникальных идентифиакторов столов.
     */
    @Suppress("unused")
    fun getSelectedTablesForMultiSelect(): Set<UUID> {
        return HashSet(multiSelectedTables)
    }

    /**
     * Сбрасывание muliselect-а.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun clearMultiSelection() {
        val iterator = multiSelectedTables.iterator()
        iterator.forEach {
            hallSchemeFactory.getHallSchemeDrawer().findViewAndDoAction(tablesLayout, it, null) { view ->
                view.unSelectForMultiSelect()
                iterator.remove()
            }
        }
    }
    // Selection region end

    /**@SelfDocumented*/
    interface OnHallSchemeItemClickListener {
        /**@SelfDocumented*/
        fun onItemClick(hallSchemeItem: HallSchemeItem)

        /**
         * True - если коллбек обработал событие по long tap-у и дальнейшее события click обрабатывать не надо.
         */
        fun onItemLongTap(hallSchemeItem: HallSchemeItem): Boolean = false
    }
}