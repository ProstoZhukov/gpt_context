package ru.tensor.sbis.hallscheme.v2.presentation.model.decor

import android.graphics.BitmapShader
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import ru.tensor.sbis.hallscheme.v2.HallSchemeV2
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Decor
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi
import ru.tensor.sbis.hallscheme.v2.widget.RotatableImageView

/**
 * Абстрактный класс для отображения декора.
 * @author aa.gulevskiy
 */
internal abstract class DecorUi(
    protected val decor: Decor,
    protected val drawablesHolder: DrawablesHolder
) : HallSchemeItemUi(decor) {

    /**
     * Угол поворота.
     */
    val itemRotation = decor.itemRotation

    override fun draw(
        viewGroup: ViewGroup,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        super.draw(viewGroup, null)
    }

    override fun draw3D(
        viewGroup: ViewGroup,
        pressedShader: BitmapShader,
        unpressedShader: BitmapShader,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?,
    ) {
        super.draw3D(viewGroup, pressedShader, unpressedShader, null)
    }

    override fun getView(viewGroup: ViewGroup): View {
        val vectorDrawable = drawablesHolder.getDecorFlatDrawable(getFlatImageRes())
        return RotatableImageView.newInstance(viewGroup.context, decor, vectorDrawable)
            .apply { alpha = decor.opacity }
    }

    /**
     * Возвращает изображение декора в плоской теме.
     */
    @DrawableRes
    abstract fun getFlatImageRes(): Int

    override fun get3dView(viewGroup: ViewGroup): View {
        val view = getViewForDifferentImagePerAngle(viewGroup)
        view.setImageDrawable(drawablesHolder.getDecor3dDrawable(get3dImageRes()))
        view.alpha = decor.opacity
        return view
    }

    /**
     * Возвращает изображение в соответствии с углом поворота.
     */
    protected fun getViewForDifferentImagePerAngle(viewGroup: ViewGroup) =
        ImageView(viewGroup.context).apply {
            layoutParams = RelativeLayout.LayoutParams(decor.rotatedRect.width, decor.rotatedRect.height)

            adjustViewBounds = true

            x = decor.rotatedRect.left.toFloat()
            y = decor.rotatedRect.top.toFloat()
        }

    /**
     * Возвращает изображение декора в объёмной теме.
     */
    @DrawableRes
    abstract fun get3dImageRes(): Int
}