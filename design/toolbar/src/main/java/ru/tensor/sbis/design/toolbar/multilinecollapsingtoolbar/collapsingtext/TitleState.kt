package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.collapsingtext

import android.graphics.Color

/**
 * Параметры текущего состояния текста.
 *
 * @author us.bessonov
 */
internal class TitleState(
    var color: Int = Color.MAGENTA,
    var x: Float = 0f,
    var y: Float = 0f,
    var size: Float = 0f,
    val shadow: TitleShadow = TitleShadow()
)

/**
 * Перечисление ключевых точек параметров отображения текста при сворачивании/разворачивании графической шапки.
 *
 * @author us.bessonov
 */
internal class TitleStates(
    val collapsed: TitleState,
    val expanded: TitleState,
    val mediate: TitleState?,
    val current: TitleState
)