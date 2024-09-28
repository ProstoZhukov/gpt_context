package ru.tensor.sbis.segmented_control.control.models

import android.view.ViewGroup.LayoutParams

/**
 * Варианты компоновки сегментов.
 *
 * @author ps.smirnyh
 */
enum class SbisSegmentedControlDistribution {

    /** Равное распределение пространства между сегментами. */
    EQUAL {
        override fun getLayoutParams(): LayoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    },

    /** Размер сегментов устанавливается по размеру контента. */
    FIT_CONTENT {
        override fun getLayoutParams(): LayoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    };

    /** @SelfDocumented */
    internal abstract fun getLayoutParams(): LayoutParams
}