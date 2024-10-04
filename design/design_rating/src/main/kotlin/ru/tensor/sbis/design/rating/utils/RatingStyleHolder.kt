package ru.tensor.sbis.design.rating.utils

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.rating.R
import ru.tensor.sbis.design.rating.SbisRatingView

/**
 * Класс для хранения параметров стиля [SbisRatingView].
 *
 * @author ps.smirnyh
 */
internal class RatingStyleHolder {

    private var oneIconColor: Int = Color.MAGENTA
    private var twoIconColor: Int = Color.MAGENTA
    private var threeIconColor: Int = Color.MAGENTA
    private var fourIconColor: Int = Color.MAGENTA
    private var fiveIconColor: Int = Color.MAGENTA

    private var goodIconColor: Int = Color.MAGENTA
    private var neutralIconColor: Int = Color.MAGENTA
    private var badIconColor: Int = Color.MAGENTA

    /** @SelfDocumented */
    internal var filledIconColor: Int = Color.MAGENTA

    /** @SelfDocumented */
    internal var emptyIconColor: Int = Color.MAGENTA

    /** @SelfDocumented */
    internal var iconColors = emptyList<Int>()

    /** @SelfDocumented */
    internal var otherIconColors = emptyList<Int>()

    /** Загрузить текущие значения из темы компонента. */
    fun load(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        context.withStyledAttributes(attrs, R.styleable.SbisRatingView, defStyleAttr, defStyleRes) {
            oneIconColor = getColor(R.styleable.SbisRatingView_SbisRatingView_oneIconColor, oneIconColor)
            twoIconColor = getColor(R.styleable.SbisRatingView_SbisRatingView_twoIconColor, twoIconColor)
            threeIconColor = getColor(R.styleable.SbisRatingView_SbisRatingView_threeIconColor, threeIconColor)
            fourIconColor = getColor(R.styleable.SbisRatingView_SbisRatingView_fourIconColor, fourIconColor)
            fiveIconColor = getColor(R.styleable.SbisRatingView_SbisRatingView_fiveIconColor, fiveIconColor)
            filledIconColor = getColor(R.styleable.SbisRatingView_SbisRatingView_filledIconColor, filledIconColor)
            emptyIconColor = getColor(R.styleable.SbisRatingView_SbisRatingView_emptyIconColor, emptyIconColor)
            goodIconColor = getColor(R.styleable.SbisRatingView_SbisRatingView_goodIconColor, goodIconColor)
            neutralIconColor = getColor(R.styleable.SbisRatingView_SbisRatingView_neutralIconColor, neutralIconColor)
            badIconColor = getColor(R.styleable.SbisRatingView_SbisRatingView_badIconColor, badIconColor)
        }
        iconColors = listOf(oneIconColor, twoIconColor, threeIconColor, fourIconColor, fiveIconColor)
        otherIconColors = listOf(badIconColor, neutralIconColor, goodIconColor)
    }
}