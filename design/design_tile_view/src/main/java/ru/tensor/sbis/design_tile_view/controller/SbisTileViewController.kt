package ru.tensor.sbis.design_tile_view.controller

import android.graphics.Canvas
import android.view.View
import androidx.annotation.Px
import ru.tensor.sbis.design_tile_view.TileViewImage
import ru.tensor.sbis.design_tile_view.util.ImageBorderDrawer
import ru.tensor.sbis.design_tile_view.view.SbisTileView
import ru.tensor.sbis.design_tile_view.view.SbisTileViewApi
import ru.tensor.sbis.design_tile_view.view.TileImageView

/**
 * Контракт контроллера компонента Плитка
 *
 * @author us.bessonov
 */
internal interface SbisTileViewController : SbisTileViewApi {

    /** @SelfDocumented */
    @get:Px
    val cornerRadius: Float

    /**
     * Для доп. информации при возникновении ошибки
     */
    val images: List<TileViewImage>

    /**
     * Ширина плитки, определённая в результате вызова [performMeasure]
     */
    @get:Px
    val width: Int

    /**
     * Высота плитки, определённая в результате вызова [performMeasure]
     */
    @get:Px
    val height: Int

    /**
     * Осуществляется ли сейчас загрузка изображения
     */
    val isLoading: Boolean

    /** @SelfDocumented */
    fun init(
        view: SbisTileView,
        image: TileImageView,
        @Px imageSize: Int,
        @Px imagePadding: Int,
        isBorderEnabled: Boolean,
        isBorderUnderContent: Boolean,
        imageBorderDrawer: ImageBorderDrawer = ImageBorderDrawer(view.context)
    )

    /** @SelfDocumented */
    fun initOverlayViews(contentView: View?, topView: View?, bottomView: View?)

    /**
     * @see [View.onMeasure]
     */
    fun performMeasure(@Px availableViewWidth: Int, @Px availableViewHeight: Int)

    /**
     * @see [View.onLayout]
     */
    fun performLayout()

    /**
     * @see [View.onDraw]
     */
    fun performDraw(canvas: Canvas)

    /**
     * @see [View.invalidate]
     */
    fun performInvalidate()

    /**
     * @see [View.onVisibilityAggregated]
     */
    fun onVisibilityAggregated(isVisible: Boolean)

}