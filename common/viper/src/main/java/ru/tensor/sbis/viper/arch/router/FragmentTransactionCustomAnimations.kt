package ru.tensor.sbis.viper.arch.router

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import ru.tensor.sbis.viper.R
import ru.tensor.sbis.design.R as RDesign

/**
 * Модель с анимациями для смены фрагментов
 *
 * @param enter Анимация для фрагмента, который добавляется
 * @param exit Анимация для фрагмента, который закрывается
 * @param popEnter Анимация для фрагмента, который вовзращается из стека после вызова {@link FragmentManager#popBackStack()}
 * @param popExit Анимация для фрагмента, который закрывается после вызова {@link FragmentManager#popBackStack()}
 *
 * @author ga.malinskiy
 */
data class FragmentTransactionCustomAnimations(
    @AnimatorRes @AnimRes val enter: Int = 0,
    @AnimatorRes @AnimRes val exit: Int = 0,
    @AnimatorRes @AnimRes val popEnter: Int = 0,
    @AnimatorRes @AnimRes val popExit: Int = 0
) {

    companion object {
        fun getRightInRightOutAnimations(): FragmentTransactionCustomAnimations =
            FragmentTransactionCustomAnimations(RDesign.anim.right_in, RDesign.anim.right_out, RDesign.anim.right_in, RDesign.anim.right_out)

        fun getTopInTopOutAnimations(): FragmentTransactionCustomAnimations =
            FragmentTransactionCustomAnimations(R.anim.top_in, R.anim.top_out, R.anim.top_in, R.anim.top_out)

        fun getBottomInBottomOutAnimations(): FragmentTransactionCustomAnimations =
            FragmentTransactionCustomAnimations(
                R.anim.bottom_in,
                R.anim.bottom_out,
                R.anim.bottom_in,
                R.anim.bottom_out
            )

        fun getFadeInFadeOutAnimations(): FragmentTransactionCustomAnimations =
            FragmentTransactionCustomAnimations(RDesign.anim.fade_in, RDesign.anim.fade_out, RDesign.anim.fade_in, RDesign.anim.fade_out)
    }
}