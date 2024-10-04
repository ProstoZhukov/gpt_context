package ru.tensor.sbis.design.toolbar.appbar.offset

import androidx.annotation.FloatRange
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout

/**
 * Подписка на состояние раскрытия [SbisAppBarLayout]. В отличае от [AppBarLayout.OnOffsetChangedListener] в подписку
 * доставляется нормализованная, а не абсолютная величина. Это позволяет отвязать потребителей от реальных размеров
 *
 * @author ma.kolpakov
 * Создан 9/23/2019
 */
interface NormalOffsetObserver {

    /**
     * Реакция на изменение состояния раскрытия
     *
     * @param position процент раскрытия [SbisAppBarLayout]. Величина в диапазоне от 0 до 1, где 0 - свёрнутое состояние
     */
    fun onOffsetChanged(@FloatRange(from = 0.0, to = 1.0) position: Float)
}