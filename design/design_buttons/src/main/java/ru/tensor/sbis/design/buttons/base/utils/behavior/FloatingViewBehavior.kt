package ru.tensor.sbis.design.buttons.base.utils.behavior

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import androidx.annotation.Px
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

/**
 * Класс, отвечающий за поведение в координаторе плавающей View.
 *
 * @author ma.kolpakov
 */
internal class FloatingViewBehavior : CoordinatorLayout.Behavior<View>(), SnackBarInfoBehavior {

    @Px
    override var snackBarHeight = 0

    @Px
    override var snackBarBottom = 0

    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        if (params.dodgeInsetEdges == Gravity.NO_GRAVITY) {
            // по умолчанию установим отталкивание от view, которые выезжают снизу
            params.dodgeInsetEdges = Gravity.BOTTOM
        }
    }

    @SuppressLint("RestrictedApi")
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        snackBarHeight = 0
        snackBarBottom = 0
        return if (dependency is Snackbar.SnackbarLayout) {
            /*
            Запоминаются размеры snackbar, т.к. при появлении почему-то публикуется максимальное
            смещение, а затем смещения в соответствии с анимацией. На уровне view нужна фильтрация
            таких событий.
            Пока не удалось решить проблему, если snackbar появляется между двумя view, где верхняя
            должна сместиться выше (есть небольшой рывок).
             */
            snackBarHeight = dependency.height
            snackBarBottom = parent.bottom - dependency.bottom + dependency.height
            true
        } else {
            super.layoutDependsOn(parent, child, dependency)
        }
    }
}