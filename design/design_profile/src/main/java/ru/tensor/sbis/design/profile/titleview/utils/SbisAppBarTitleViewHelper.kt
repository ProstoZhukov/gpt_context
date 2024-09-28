package ru.tensor.sbis.design.profile.titleview.utils

import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import ru.tensor.sbis.design.profile.databinding.DesignProfileSbisTitleViewBinding
import ru.tensor.sbis.design.profile.personcollage.PersonCollageView
import ru.tensor.sbis.design.profile.titleview.SbisTitleView
import ru.tensor.sbis.design.utils.extentions.hide
import ru.tensor.sbis.design.utils.extentions.show
import ru.tensor.sbis.fresco_view.SuperEllipseDraweeView
import kotlin.math.roundToInt

/**
 * Инструмент, обеспечивающий возможность использования [SbisTitleView] в компоненте графической шапки.
 * В свёрнутом виде в графической шапке заголовок отображается средствами `CollapsingToolbarLayout`. Изображение и
 * заголовок проявляются по мере сворачивания шапки.
 *
 * @author us.bessonov
 */
class SbisAppBarTitleViewHelper(private val binding: DesignProfileSbisTitleViewBinding) {

    private var subtitleEndPaddingProvider: (() -> Int)? = null

    /** @SelfDocumented */
    @Px
    fun getTitleTextSize() = binding.designProfileTitleViewTitle.textSize

    /** @SelfDocumented */
    @Px
    fun getTitleLeft(): Int = with(binding) {
        val image = getImageView()
        val imageOffset = if (image != null && image.isVisible) image.measuredWidth else 0
        imageOffset + designProfileTitleViewTitle.marginLeft
    }

    /** @SelfDocumented */
    @Px
    fun getTitleTop(): Int = with(binding) {
        /*
        Если подзаголовка нет, то не требуется сокращать высоту ожидаемой области шапки в свёрнутом виде. Выравнивание
        заголовка будет осуществляться согласно collapsedTitleGravity.
         */
        if (designProfileTitleViewSubtitle.text.isNullOrBlank()) return root.paddingTop
        val height = root.measuredHeight.takeUnless { it <= 0 }
            ?: return root.paddingTop
        val offset =
            ((height - designProfileTitleViewTitle.measuredHeight - designProfileTitleViewSubtitle.measuredHeight) / 2f)
                .roundToInt()
        return root.paddingTop + offset
    }

    /** @SelfDocumented */
    @Px
    fun getImageTop(): Int = with(binding) {
        root.paddingTop + (getImageView()?.top ?: 0)
    }

    /** @SelfDocumented */
    fun setTitleAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        binding.designProfileTitleViewTitle.alpha = alpha
    }

    /** @SelfDocumented */
    fun animateSubtitleVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.designProfileTitleViewSubtitle.show()
        } else {
            binding.designProfileTitleViewSubtitle.hide()
        }
    }

    /** @SelfDocumented */
    fun setSubTitleColor(@ColorRes color: Int) {
        binding.designProfileTitleViewSubtitle.setTextColor(ContextCompat.getColor(binding.root.context, color))
    }

    /** @SelfDocumented */
    fun setImageAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        getImageView()?.alpha = alpha
    }

    /**
     * Задаёт метод для определения текущего отступа справа у подзаголовка.
     * Может быт использован для предотвращения перекрытия текста внешним динамическим содержимым
     */
    fun setSubtitleEndPaddingProvider(paddingProvider: () -> Int) {
        subtitleEndPaddingProvider = paddingProvider
    }

    /** @SelfDocumented */
    fun hasImage() = getImageView()?.isVisible ?: false

    /** @SelfDocumented */
    @Px
    internal fun getSubtitleEndPadding() = subtitleEndPaddingProvider?.invoke() ?: 0

    private fun getImageView(): View? {
        var superEllipseView: SuperEllipseDraweeView? = null
        var personCollageView: PersonCollageView? = null
        for (index in 0 until (binding.root as ViewGroup).childCount) {
            val child = (binding.root as ViewGroup).getChildAt(index)
            if (child is PersonCollageView) {
                personCollageView = child.also {
                    // TODO: https://online.sbis.ru/opendoc.html?guid=68216ae0-8ee8-4f4a-b743-7c08029d00bb&client=3
                    it.clipChildren = false
                    it.clipToPadding = false
                }
            }
            if (child is SuperEllipseDraweeView) superEllipseView = child
        }

        return superEllipseView ?: personCollageView as View?
    }
}