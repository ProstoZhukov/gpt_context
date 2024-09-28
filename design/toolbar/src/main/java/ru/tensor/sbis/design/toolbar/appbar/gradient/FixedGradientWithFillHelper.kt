package ru.tensor.sbis.design.toolbar.appbar.gradient

import android.view.View
import androidx.annotation.Px
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.toolbar.R

/**
 * Реализация [GradientHelper] для градиента фиксированной высоты, под которым располагается область с заливкой
 *
 * @author us.bessonov
 */
internal class FixedGradientWithFillHelper(private val gradientView: View) : GradientHelper {

    @Px
    private val fixedGradientHeight =
        gradientView.resources.getDimensionPixelSize(R.dimen.toolbar_gradient_fixed_height)

    override fun setFillHeight(fillHeight: Int) {
        gradientView.updateLayoutParams {
            height = fixedGradientHeight + fillHeight
        }
    }

    override fun updateGradient(color: Int?) {
        gradientView.background = color?.let { createExtendedGradientDrawable(it, fixedGradientHeight) }
    }

    override fun update(normalizedOffset: Float) {
        gradientView.alpha = getGradientAlpha(normalizedOffset)
    }

    override fun onExpandedTitleLineCountChanged(lineCount: Int) {
        // высота не зависит от числа строк заголовка
    }
}