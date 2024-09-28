package ru.tensor.sbis.design.toolbar.appbar.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.FloatRange
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout

/**
 * Модель внешнего вида для [SbisAppBarLayout]. Модель можно передвать между экранами для восстановления состояния при
 * переходах
 *
 * @property background Модель фона. По умолчанию используется [UndefinedBackground]
 * @property color Модель цветовой схемы
 * @property content Отображаемое содержимое
 * @property offset Положение раскрытия. По умолчанию [Float.NaN] - установить стандартное значение
 *
 * @author ma.kolpakov
 * Создан 9/23/2019
 */
@SuppressLint("Range")
@Parcelize
data class AppBarModel(
    val background: BackgroundModel = UndefinedBackground,
    val color: ColorModel? = null,
    val content: AppBarContent = AppBarContent(),
    @FloatRange(from = 0.0, to = 1.0)
    private var offset: Float = Float.NaN
) : Parcelable {

    /**
     * Текущее положение раскрытия [SbisAppBarLayout]
     */
    @get:FloatRange(from = 0.0, to = 1.0)
    var currentOffset: Float
        get() = offset
        internal set(value) {
            offset = value
            checkOffset()
        }

    init {
        checkOffset()
    }

    private fun checkOffset() {
        require(offset.isNaN() || offset in 0F..1F) { "Offset out of range $offset" }
    }
}