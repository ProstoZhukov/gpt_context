package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.collapsingtext

import android.graphics.RectF

/**
 * Желаемое состояние отображаемого текста для некоторой степени разворота шапки.
 *
 * @author us.bessonov
 */
internal class CollapsingTextState(
    val bounds: RectF = RectF(),
    val titleState: TitleState = TitleState(),
    val subtitleState: TitleState = TitleState(),
    val rightSubtitleState: TitleState = TitleState(),
)