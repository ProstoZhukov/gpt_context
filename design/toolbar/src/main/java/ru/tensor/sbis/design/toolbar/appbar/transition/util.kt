/**
 * Инструменты для обеспечения стандартного перехода SbisAppBarLayout между экранами
 *
 * @author ma.kolpakov
 * Создан 9/30/2019
 */
@file:JvmName("SbisAppBarTransitionUtil")

package ru.tensor.sbis.design.toolbar.appbar.transition

import android.content.Intent
import android.os.Bundle
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.model.AppBarModel

private const val STATE_KEY = "ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout_STATE_KEY"

/**
 * Сохранение состояния [SbisAppBarLayout] включая положение раскрытия
 */
fun AppBarModel.saveState(bundle: Bundle) {
    bundle.putParcelable(STATE_KEY, this)
}

/**
 * @see saveState
 */
fun AppBarModel.saveState(intent: Intent) {
    intent.putExtra(STATE_KEY, this)
}

/**
 * Сохранение состояния [SbisAppBarLayout] для перехода во внутренний раздел. При восстановлении [SbisAppBarLayout]
 * будет свёрнут
 */
fun AppBarModel.saveTransitionState(bundle: Bundle) {
    currentOffset = 0F
    bundle.putParcelable(STATE_KEY, this)
}

/**
 * @see saveTransitionState
 */
fun AppBarModel.saveTransitionState(intent: Intent) {
    currentOffset = 0F
    intent.putExtra(STATE_KEY, this)
}

/**
 * Восстановление состояния, если сохранено. Установка модели [AppBarModel] в [SbisAppBarLayout.model]
 */
fun SbisAppBarLayout.restoreState(bundle: Bundle?) {
    bundle ?: return
    bundle.getParcelable<AppBarModel>(STATE_KEY)?.let { model = it }
}

/**
 * @see restoreState
 */
fun SbisAppBarLayout.restoreState(intent: Intent?) {
    intent ?: return
    restoreState(intent.extras)
}
