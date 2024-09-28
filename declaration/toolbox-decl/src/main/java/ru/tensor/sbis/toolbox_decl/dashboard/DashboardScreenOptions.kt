package ru.tensor.sbis.toolbox_decl.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Набор опций для кастомизации экрана с дашбордом.
 *
 * @property navigationPanelPadding необходимость добавления отступа снизу для ННП.
 * @property floatingPanelPadding необходимость добавления отступа снизу для плавающей кнопки.
 *
 * @author am.boldinov
 */
@Parcelize
class DashboardScreenOptions(
    val navigationPanelPadding: Boolean = true,
    val floatingPanelPadding: Boolean = false
) : Parcelable